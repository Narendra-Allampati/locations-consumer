package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.GeographyDoc;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper;
import com.maersk.referencedata.locationsconsumer.repositories.LocationsRepository;
import com.maersk.shared.kafka.serialization.KafkaDeserializerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsService {

    private final KafkaReceiver<String, geographyMessage> kafkaReceiver;
    private final LocationsRepository locationsRepository;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return kafkaReceiver
                .receive()
                .doOnNext(event -> log.info("Received event: key {}, value {}", event.key(), event.value()))
                .doOnError(error -> log.error("Error receiving Geography record", error))
                .flatMap(this::handleSubscriptionResponseEvent)
                .doOnNext(event -> event.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<ReceiverRecord<String, geographyMessage>> handleSubscriptionResponseEvent(ReceiverRecord<String, geographyMessage> geographyRecord) {
        try {
            return Mono.just(geographyRecord)
                    .map(KafkaDeserializerUtils::extractDeserializerError)
                    .<ReceiverRecord<String, geographyMessage>>handle((tuple, sink) -> {
                        if (tuple.getT2().isEmpty() && Objects.nonNull(tuple.getT1().value())) {
                            sink.next(tuple.getT1());
                        } else {
                            log.error("Error while processing geographyMessage " + tuple.getT2().get());
                        }
                    })
                    .flatMap(this::storeEvent)
                    .doOnError(ex -> log.warn("Error processing event {}", geographyRecord.key(), ex))
                    .doOnError(ex -> log.error("Error processing event after all retries {}", geographyRecord.key(), ex))
                    .onErrorResume(ex -> Mono.empty())
                    .doOnNext(__ -> log.info("Successfully processed event"))
                    .then(Mono.just(geographyRecord));
        } catch (Exception ex) {
            log.error("Error processing event {}", geographyRecord.key(), ex);
            return Mono.just(geographyRecord);
        }
    }

    private Mono<Void> storeEvent(ReceiverRecord<String, geographyMessage> geographyRecord) {
        getMappedObject(geographyRecord.value());
        return Mono.empty();
    }

    private Geography getMappedObject(geographyMessage geographyMessage) {
        return GeographyMapper.mapAvroToDomainModel(geographyMessage);
    }

    private Mono<GeographyDoc> handleLocationsResponse(ReceiverRecord<String, geographyMessage> geographyRecord) {
        geographyMessage geographyMessage = geographyRecord.value();
        final var geography = geographyMessage.getGeographyEntity().getGeography();

        GeographyDoc geographyDoc = GeographyDoc.builder()
                .geoRowId(geography.getGeoRowID())
                .operationType(new String(geographyRecord.headers().lastHeader("EventType").value()))
                .geoType(geography.getGeoType())
                .status(geography.getStatus())
                .validFrom(geography.getValidFrom())
                .validTo(geography.getValidTo())
                .payload(geographyMessage)
                .build();
        log.info("Geography: " + geographyDoc);
        return locationsRepository.save(geographyDoc);

    }

    private boolean filterDeserializationError(ReceiverRecord<String, geographyMessage> record) {
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
