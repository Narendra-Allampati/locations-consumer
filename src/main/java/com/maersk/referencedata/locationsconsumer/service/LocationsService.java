package com.maersk.referencedata.locationsconsumer.service;

import com.maersk.Geography.smds.operations.MSK.GeographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.GeographyDoc;
import com.maersk.referencedata.locationsconsumer.repositories.LocationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsService {

    private final KafkaReceiver<String, GeographyMessage> kafkaReceiver;
    private final LocationsRepository locationsRepository;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return kafkaReceiver
                .receive()
//                .take(1000)
//                .delayElements(Duration.ofSeconds(1))
                .doOnNext(record -> log.info("Received event: key {}, value {}", record.key(), record.value()))
                .doOnError(error -> log.error("Error receiving Geography record", error))
                .flatMap(this::handleSubscriptionResponseEvent)
                .doOnNext(record -> record.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<ReceiverRecord<String, GeographyMessage>> handleSubscriptionResponseEvent(ReceiverRecord<String, GeographyMessage> record) {
        try {
            return Mono.just(record)
                    .filter(this::filterDeserializationError)
//                    .map(ConsumerRecord::value)
//                    .flatMap(this::handleLocationsResponse)
                    .flatMap(this::tempStep)
                    .doOnError(ex -> log.warn("Error processing event {}", record.key(), ex))
//                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                    .doOnError(ex -> log.error("Error processing event after all retries {}", record.key(), ex))
                    .onErrorResume(ex -> Mono.empty())
                    .doOnNext(__ -> log.info("Successfully processed event"))
                    .then(Mono.just(record));
        } catch (Exception ex) {
            log.error("Error processing event {}", record.key(), ex);
            return Mono.just(record);
        }
    }

    private Mono<GeographyDoc> tempStep(ReceiverRecord<String, GeographyMessage> record) {
        GeographyMessage geographyMessage = record.value();
        final var geography = geographyMessage.getGeographyEntity().getGeography();

        GeographyDoc geographyDoc = GeographyDoc.builder()
                .geoRowId(geography.getGeoRowID())
                .operationType(new String(record.headers().lastHeader("EventType").value()))
                .geoType(geography.getGeoType())
                .status(geography.getStatus())
                .validFrom(geography.getValidFrom())
                .validTo(geography.getValidTo())
                .payload(geographyMessage)
                .build();
log.info("Geography: " + geographyDoc);
        return locationsRepository.save(geographyDoc);

//        return handleLocationsResponse(record.value(), new String(record.headers().lastHeader("EventType").value()));
    }

    private Mono<Void> handleLocationsResponse(GeographyMessage geographyMessage, String header) {
        final var geography = geographyMessage.getGeographyEntity().getGeography();

        GeographyDoc geographyDoc = GeographyDoc.builder()
                .geoRowId(geography.getGeoRowID())
                .operationType(header)
                .geoType(geography.getGeoType())
                .status(geography.getStatus())
                .validFrom(geography.getValidFrom())
                .validTo(geography.getValidTo())
                .payload(geographyMessage)
                .build();

        return locationsRepository.save(geographyDoc).then();
    }


    private boolean filterDeserializationError(ReceiverRecord<String, GeographyMessage> record) {
        byte[] keyDeserializationException = Optional
                .ofNullable(record.headers().lastHeader(ErrorHandlingDeserializer.KEY_DESERIALIZER_EXCEPTION_HEADER))
                .map(Header::value)
                .orElse(null);
        if (keyDeserializationException != null) {
            log.error("Failed to deserialize key", deserializeExceptionObject(keyDeserializationException));
            return false;
        }
        byte[] valueDeserializationException = Optional
                .ofNullable(record.headers().lastHeader(ErrorHandlingDeserializer.VALUE_DESERIALIZER_EXCEPTION_HEADER))
                .map(Header::value)
                .orElse(null);
        if (valueDeserializationException != null) {
            log.error("Failed to deserialize value", deserializeExceptionObject(valueDeserializationException));
            return false;
        }
        return true;
    }

    private Exception deserializeExceptionObject(byte[] exception) {
        try (var bais = new ByteArrayInputStream(exception)) {
            try (var oos = new ObjectInputStream(bais)) {
                return (Exception) oos.readObject();
            } catch (ClassNotFoundException ex) {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }
}
