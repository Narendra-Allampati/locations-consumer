package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.facility.smds.operations.msk.facility;
import com.maersk.facility.smds.operations.msk.facilityMessage;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityAlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityType;
import com.maersk.referencedata.locationsconsumer.mappers.FacilityMapper;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.AddressesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.ContactDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilitiesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityAlternateCodesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityServicesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityTypesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FencesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.OpeningHoursRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.TransportModesRepository;
import com.maersk.referencedata.locationsconsumer.startup.FacilityTypesMappingsLoad;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Anders Clausen on 20/03/2022.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilitiesService {

    private final KafkaReceiver<String, facilityMessage> facilityKafkaReceiver;

    private final AddressesRepository addressesRepository;
    private final ContactDetailsRepository contactDetailsRepository;
    private final FacilitiesRepository facilitiesRepository;
    private final FacilityAlternateCodesRepository facilityAlternateCodesRepository;
    private final FacilityDetailsRepository facilityDetailsRepository;
    private final FacilityServicesRepository facilityServicesRepository;
    private final FacilityTypesRepository facilityTypesRepository;
    private final FencesRepository fencesRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final TransportModesRepository transportModesRepository;

    private final FacilityTypesMappingsLoad facilityTypesMappingsLoad;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return facilityKafkaReceiver
                .receive()
                .name("facility events")
                .tag("source", "kafka")
                .metrics()
                .take(1, true)
                .doOnError(error -> log.warn("Error receiving Facility record, exception -> {}, retry will be attempted",
                        error.getLocalizedMessage(), error))
                .retryWhen(Retry.indefinitely()
                                .filter(ErrorHandlingUtils::isRetriableKafkaError))
                .doOnError(error -> log.warn("Error thrown whilst processing facility records, error isn't a " +
                        "known retriable error, will attempt to retry processing records , exception -> {}", error.getLocalizedMessage(), error))
                .retryWhen(Retry.fixedDelay(100, Duration.ofMinutes(1)))
                .concatMap(this::handleFacilityEvent)
                .subscribe(event -> event.receiverOffset()
                                         .acknowledge());
    }

    private Mono<ReceiverRecord<String, facilityMessage>> handleFacilityEvent(ReceiverRecord<String, facilityMessage> facilityRecord) {
        return Mono.just(facilityRecord)
                   .map(KafkaDeserializerUtils::extractDeserializerError)
                   .<facilityMessage>handle((tuple, sink) -> {
                       if (tuple.getT2()
                                .isEmpty() && Objects.nonNull(tuple.getT1()
                                                                   .value())) {
                           sink.next(tuple.getT1()
                                          .value());
                       } else {
                           log.error("Error while processing Facility " + tuple.getT2()
                                                                               .get());
                       }
                   })
                   .doOnNext(event -> log.info("Received facility event: key {}, facilityId{}, partition number {}", facilityRecord.key()
                           , event.getFacility()
                                  .getFacilityId(), facilityRecord.receiverOffset()
                                                                  .topicPartition()
                                                                  .partition()))
                   .flatMap(facilityMessage -> createOrUpdateFacility(facilityMessage.getFacility()))
                   .doOnError(ex -> log.error("Error processing event after all retries {} and value {}", facilityRecord.key(), facilityRecord.value(), ex))
                   .onErrorResume(ex -> Mono.empty())
                   .then(Mono.just(facilityRecord));
    }

    private Mono<String> createOrUpdateFacility(facility facilityEvent) {
        return facilitiesRepository.findById(facilityEvent.getFacilityId())
                                   .flatMap(facilityFromDB -> updateFacility(facilityEvent))
                                   .switchIfEmpty(Mono.defer(() -> saveFacilityEvent(facilityEvent)));
    }

    private Mono<String> updateFacility(facility facilityEvent) {
        return facilitiesRepository.deleteById(facilityEvent.getFacilityId())
                                   .doOnSuccess(event -> log.info("Deleted facility event: key {}", facilityEvent.getFacilityId()))
                                   .then(Mono.defer(() -> saveFacilityEvent(facilityEvent)));
    }

    private Mono<String> saveFacilityEvent(facility facilityEvent) {

        String facilityId = facilityEvent.getFacilityId();

        final var facility = FacilityMapper.mapToFacility(facilityEvent);

        final var address = FacilityMapper.mapToAddress(facilityEvent.getAddress(), facilityId);

        List<FacilityAlternateCode> facilityAlternateCodes = FacilityMapper.mapToAlternateCodeLinks(facilityEvent.getAlternateCodes(), facilityId);

        final var facilityDetail = FacilityMapper.mapToFacilityDetail(facilityEvent.getFacilityDetail(), facilityId);

        final var facilityTypeWrappers = FacilityMapper.mapToFacilityTypesWrapper(facilityEvent.getFacilityDetail(), facilityId);
        List<FacilityType> facilityTypes = facilityTypeWrappers.map(FacilityMapper::getFacilityTypesAsList)
                                                               .orElse(Collections.emptyList());
        facility.setSiteType(getFacilityTypeWithHighestPrecedence(facilityTypes));

        final var openingHours = FacilityMapper.mapToFacilityOpeningHours(facilityEvent.getOpeningHours(), facilityId);

        final var transportModes = FacilityMapper.mapToTransportModes(facilityEvent.getTransportModes(), facilityId);

        final var facilityServices = FacilityMapper.mapToFacilityServices(facilityEvent.getFacilityServices(), facilityId);

        final var fences = FacilityMapper.mapToFences(facilityEvent.getFences(), facilityId);

        final var contactDetails = FacilityMapper.mapToContactDetails(facilityEvent.getContactDetails(), facilityId);

        return Flux.concat(facilitiesRepository.save(facility)
                                               .then(),
                           addressesRepository.save(address)
                                              .then(),
                           facilityAlternateCodesRepository.saveAll(facilityAlternateCodes)
                                                           .then(),
                           facilityDetailsRepository.saveAll(Mono.justOrEmpty(facilityDetail))
                                                    .then(),
                           facilityTypesRepository.saveAll(facilityTypes)
                                                  .then(),
                           openingHoursRepository.saveAll(openingHours)
                                                 .then(),
                           transportModesRepository.saveAll(transportModes)
                                                   .then(),
                           facilityServicesRepository.saveAll(facilityServices)
                                                     .then(),
                           fencesRepository.saveAll(fences)
                                           .then(),
                           contactDetailsRepository.saveAll(contactDetails)
                                                   .then())
                   .then(Mono.just("1"));
    }

    private String getFacilityTypeWithHighestPrecedence(List<FacilityType> facilityTypes) {

        return facilityTypes.stream()
                            .map(x -> {
                                return facilityTypesMappingsLoad.getRankingFromCode(x.getCode());
                            })
                            .min(Integer::compareTo)
                            .map(facilityTypesMappingsLoad::getSiteTypeFromRanking)
                            .orElse(null);

    }
}
