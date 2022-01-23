package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.geography.smds.operations.msk.*;

import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateName;
import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedArea;
import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedAreaLocation;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Country;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Parent;
import com.maersk.referencedata.locationsconsumer.domains.postgres.SubCityParent;
import com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper;
import com.maersk.referencedata.locationsconsumer.repositories.AlternateCodeRepository;
import com.maersk.referencedata.locationsconsumer.repositories.AlternateNameRepository;
import com.maersk.referencedata.locationsconsumer.repositories.GeographyRepository;
import com.maersk.shared.kafka.serialization.KafkaDeserializerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsService {

    private final KafkaReceiver<String, geographyMessage> kafkaReceiver;
    private final GeographyRepository geographyRepository;
    private final AlternateCodeRepository alternateCodeRepository;
    private final AlternateNameRepository alternateNameRepository;

    private static final String TIME_ZONE_UTC = "UTC";

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
                    .<com.maersk.geography.smds.operations.msk.geographyMessage>handle((tuple, sink) -> {
                        if (tuple.getT2().isEmpty() && Objects.nonNull(tuple.getT1().value())) {
                            sink.next(tuple.getT1().value());
                        } else {
                            log.error("Error while processing geographyMessage " + tuple.getT2().get());
                        }
                    })
                    .flatMap(geographyMessage -> mapAndSaveGeographyEvent(geographyMessage.getGeography()))
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

    private Mono<Void> mapAndSaveGeographyEvent(geography geography) {

        final var geoID = geography.getGeoId();

        Geography geo = mapGeographyEventToGeography(geography);

        final var alternateNames = mapToAlternateNames(geography.getAlternateNames(), geoID);

        final var alternateCodes = mapToAlternateCodes(geography.getAlternateCodes(), geoID);

        return Mono.zip(geographyRepository.save(geo),
                        alternateNameRepository.saveAll(alternateNames).collectList(),
                        alternateCodeRepository.saveAll(alternateCodes).collectList())
                .then();
    }

    private Geography mapGeographyEventToGeography(geography geography) {
        return Geography.builder()
                .geoRowId(geography.getGeoId())
                .geoType(geography.getGeoType())
                .name(geography.getName())
                .status(geography.getStatus())
                .validFrom(geography.getValidFrom())
                .validTo(geography.getValidTo())
                .longitude(geography.getLongitude())
                .latitude(geography.getLatitude())
                .timeZone(geography.getTimeZone())
                .daylightSavingTime(geography.getDaylightSavingTime())
                .utcOffsetMinutes(geography.getUtcOffsetMinutes())
                .daylightSavingStart(geography.getDaylightSavingStart())
                .daylightSavingEnd(geography.getDaylightSavingEnd())
                .daylightSavingShiftMinutes(geography.getDaylightSavingShiftMinutes())
                .description(geography.getDescription())
                .workaroundReason(geography.getWorkaroundReason())
                .restricted(geography.getRestricted())
                .postalCodeMandatoryFlag(geography.getPostalCodeMandatory())
                .stateProvinceMandatory(geography.getStateProvinceMandatory())
                .dialingCode(geography.getDialingCode())
                .dialingCodeDescription(geography.getDialingCodeDescription())
                .portFlag(geography.getPortFlag())
                .olsonTimeZone(geography.getOlsonTimezone())
                .bdaType(geography.getBdaType())
                .hsudName(geography.getHsudName())
                .build();
    }

    private String findCode(List<alternateCode> alternateCodes, String type) {
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(alternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    private List<BusinessDefinedAreaLocation> mapToBusinessDefinedAreaLocations(List<bdaLocation> bdaLocations) {
        // TODO refactor to cleaner code
        if (null == bdaLocations) {
            return Collections.emptyList();
        }

        return bdaLocations.stream().map(location ->
                        BusinessDefinedAreaLocation.builder()
                                .name(location.getName())
                                .status(location.getStatus())
                                .type(location.getType())
                                .alternateCodes(mapToBDALocationAlternateCodes(location.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<BusinessDefinedArea> mapToBusinessDefinedAreas(List<bda> bdas) {

        return Optional.ofNullable(bdas)
                .orElse(Collections.emptyList()).stream().map(bda ->
                        BusinessDefinedArea.builder()
                                .name(bda.getName())
                                .bdaType(bda.getBdaType())
                                .alternateCodes(mapToBDAAlternateCodes(bda.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<SubCityParent> mapToSubCityParents(List<subCityParent> subCityParents) {
        return Optional.ofNullable(subCityParents)
                .orElse(Collections.emptyList()).stream().map(subCityParent ->
                        SubCityParent.builder()
                                .name(subCityParent.getName())
                                .type(subCityParent.getType())
                                .alternateCodes(mapToSubCityParentAlternateCodes(subCityParent.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<Parent> mapToParents(List<parent> parents) {
        return Optional.ofNullable(parents)
                .orElse(Collections.emptyList()).stream().map(parent ->
                        Parent.builder()
                                .name(parent.getName())
                                .bdaType(parent.getBdaType())
                                .alternateCodeList(mapToParentAlternateCodes(parent.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<Country> mapToCountrys(List<country> countries) {
        return Optional.ofNullable(countries)
                .orElse(Collections.emptyList()).stream().map(country ->
                        Country.builder()
                                .name(country.getName())
                                .type(country.getType())
                                .alternateCodes(mapToCountryAlternateCodes(country.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToBDALocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToBDAAlternateCodes(List<bdaAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToParentAlternateCodes(List<parentAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToCountryAlternateCodes(List<countryAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToAlternateCodes(List<alternateCode> alternateCodes, String geoID) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoID)
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateName> mapToAlternateNames(List<alternateName> alternateNames, String geoID) {
        return Optional.ofNullable(alternateNames)
                .orElse(Collections.emptyList()).stream().map(alternateName ->
                        AlternateName.builder()
                                .rowId(geoID)
                                .name(alternateName.getName())
                                .status(alternateName.getStatus())
                                .description(alternateName.getDescription())
                                .build()

                )
                .collect(Collectors.toList());

    }

    private ZonedDateTime mapLongToZonedDateTime(Long epoch) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.of(TIME_ZONE_UTC));
    }

    private Mono<Void> storeEvent(ReceiverRecord<String, geographyMessage> geographyRecord) {
        getMappedObject(geographyRecord.value());
        return Mono.empty();
    }

    private Geography getMappedObject(geographyMessage geographyMessage) {
        return GeographyMapper.mapAvroToDomainModel(geographyMessage);
    }


//    private Mono<Geography> handleLocationsResponse(ReceiverRecord<String, geographyMessage> geographyRecord) {
//
//        Mono.just(geographyRecord)
//                .map()
//
//        final var geography = geographyRecord.value().getGeography();
//        log.info("Type is: " + geography.getGeoType());
//        final var geoID = findCode(geography.getAlternateCodes(), "GEOID");
//        Geography geo = mapAndSaveGeographyEvent(geography);
//
//        geographyRepository.save(geo);
//
//        saveAlternateCodes(geography, geoID);
//
//
//        return Mono.empty();
//
//    }
}
