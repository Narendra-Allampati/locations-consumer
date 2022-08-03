package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.geography.smds.operations.msk.geography;
import com.maersk.geography.smds.operations.msk.geographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.Geography;
import com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper;
import com.maersk.referencedata.locationsconsumer.repositories.locations.AlternateCodeRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.AlternateNameRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.CountryRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.GeographyRepository;
import com.maersk.shared.kafka.serialization.KafkaDeserializerUtils;
import com.maersk.shared.kafka.utilities.ErrorHandlingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.maersk.referencedata.locationsconsumer.constants.LocationsConstants.POSTAL_CODE;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsService {

    private final KafkaReceiver<String, geographyMessage> locationsKafkaReceiver;

    private final AlternateCodeRepository alternateCodeRepository;
    private final AlternateNameRepository alternateNameRepository;
    private final CountryRepository countryRepository;
    private final GeographyRepository geographyRepository;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return locationsKafkaReceiver
                .receive()
                .take(3, true)
                .name("geo events")
                .tag("source", "kafka")
                .metrics()
                .doOnError(error -> log.warn("Error receiving Geography record, exception -> {}, retry will be attempted",
                        error.getLocalizedMessage(), error))
                .retryWhen(Retry.indefinitely()
                                .filter(ErrorHandlingUtils::isRetriableKafkaError))
                .doOnError(error -> log.warn("Error thrown whilst processing geography records, error isn't a " +
                        "known retriable error, will attempt to retry processing records , exception -> {}", error.getLocalizedMessage(), error))
                .retryWhen(Retry.fixedDelay(100, Duration.ofMinutes(1)))
                .concatMap(this::handleLocationEvent)
                .subscribe(result -> result.receiverOffset()
                                           .acknowledge());
    }

    private Mono<ReceiverRecord<String, geographyMessage>> handleLocationEvent(ReceiverRecord<String, geographyMessage> geographyRecord) {

        return Mono.just(geographyRecord)
                   .map(KafkaDeserializerUtils::extractDeserializerError)
                   .<com.maersk.geography.smds.operations.msk.geographyMessage>handle((tuple, sink) -> {
                       if (tuple.getT2()
                                .isEmpty() && Objects.nonNull(tuple.getT1()
                                                                   .value())) {
                           sink.next(tuple.getT1()
                                          .value());
                       } else {
                           log.error("Error while processing geographyMessage " + tuple.getT2()
                                                                                       .get());
                       }
                   })
                   .doOnNext(event -> log.info("Received geo event: key {}, geoId {}, partition number {}", geographyRecord.key()
                           , event.getGeography()
                                  .getGeoId(), geographyRecord.receiverOffset()
                                                              .topicPartition()
                                                              .partition()))
                   .flatMap(geographyMessage -> createOrUpdate(geographyMessage.getGeography()))
                   .doOnError(ex -> log.warn("Error processing event {} and value {}", geographyRecord.key(), geographyRecord.value(), ex))
                   .onErrorResume(ex -> Mono.empty())
                   .then(Mono.just(geographyRecord));
    }

    private Mono<String> createOrUpdate(geography geography) {
        if (POSTAL_CODE.equals(geography.getGeoType())) {
            return Mono.just("postal code");
        }

        return geographyRepository.findById(geography.getGeoId())
                                  .flatMap(geographyFromDB -> updateGeography(geography))
                                  .switchIfEmpty(Mono.defer(() -> saveGeographyEvent(geography)));
    }

    private Mono<String> updateGeography(geography geography) {
        return geographyRepository.deleteById(geography.getGeoId())
                                  .then(Mono.defer(() -> saveGeographyEvent(geography)));
    }

    private Mono<String> saveGeographyEvent(geography geography) {

        String geoID = geography.getGeoId();

        Optional<Geography> geo = GeographyMapper.mapGeographyEventToGeography(geography);

        final var alternateNames = GeographyMapper.mapToAlternateNames(geography.getAlternateNames(), geoID);

        List<AlternateCode> alternateCodes = GeographyMapper.mapToAlternateCodes(geography.getAlternateCodes(), geoID);

        return Flux.concat(geographyRepository.saveAll(Mono.justOrEmpty(geo))
                                              .then()
                           , alternateNameRepository.saveAll(alternateNames)
                                                    .then()
                           , alternateCodeRepository.saveAll(alternateCodes)
                                                    .doOnError(e ->
                                                            log.warn("Geo ID {} with geo name {} and geo type {}",
                                                                    geoID, geography.getName(), geography.getGeoType(), e))
                                                    .then())
                   .then(Mono.just("1"));
    }
}
