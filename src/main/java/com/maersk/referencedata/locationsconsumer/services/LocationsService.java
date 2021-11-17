package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.Geography.smds.operations.MSK.alternateCodes;
import com.maersk.Geography.smds.operations.MSK.alternateNames;
import com.maersk.Geography.smds.operations.MSK.bda;
import com.maersk.Geography.smds.operations.MSK.bdaAlternateCode;
import com.maersk.Geography.smds.operations.MSK.bdaLocation;
import com.maersk.Geography.smds.operations.MSK.bdaLocationAlternateCode;
import com.maersk.Geography.smds.operations.MSK.country;
import com.maersk.Geography.smds.operations.MSK.countryAlternateCodes;
import com.maersk.Geography.smds.operations.MSK.geography;
import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import com.maersk.Geography.smds.operations.MSK.parent;
import com.maersk.Geography.smds.operations.MSK.parentAlternateCode;
import com.maersk.Geography.smds.operations.MSK.subCityParent;
import com.maersk.Geography.smds.operations.MSK.subCityParentAlternateCode;
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
                    .<geographyMessage>handle((tuple, sink) -> {
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

        final var geoID = findCode(geography.getAlternateCodes(), "GEOID");

        Geography geo = mapGeographyEventToGeography(geography, geoID);

        final var alternateNames = mapToAlternateName(geography.getAlternateNames(), geoID);

        final var alternateCodes = mapToAlternateCode(geography.getAlternateCodes(), geoID);

        return Mono.zip(geographyRepository.save(geo),
                        alternateNameRepository.saveAll(alternateNames).collectList(),
                        alternateCodeRepository.saveAll(alternateCodes).collectList())
                .then();
    }

    private Geography mapGeographyEventToGeography(geography geography, String geoID) {
        return Geography.builder()
                .geoRowId(geoID)
                .geoType(geography.getGeoType())
                .name(geography.getName())
                .status(geography.getStatus())
                .validFrom(mapLongToZonedDateTime(geography.getValidFrom()))
                .validTo(mapLongToZonedDateTime(geography.getValidTo()))
                .longitude(geography.getLongitude())
                .latitude(geography.getLatitude())
                .timeZone(geography.getTimeZone())
                .daylightSavingTime(geography.getDaylightSavingTime())
                .utcOffsetMinutes(geography.getUtcOffsetMinutes())
                .daylightSavingStart(mapLongToZonedDateTime(geography.getDaylightSavingStart()))
                .daylightSavingEnd(mapLongToZonedDateTime(geography.getDaylightSavingEnd()))
                .daylightSavingShiftMinutes(geography.getDaylightSavingShiftMinutes())
                .description(geography.getDescription())
                .workaroundReason(geography.getWorkaroundReason())
                .restricted(geography.getRestricted())
                .postalCodeMandatoryFlag(geography.getPostalCodeMandatoryFlag())
                .stateProvinceMandatory(geography.getStateProvienceMandatory())
                .dialingCode(geography.getDialingCode())
                .dialingCodeDescription(geography.getDialingCodedescription())
                .portFlag(geography.getPortFlag())
                .olsonTimeZone(geography.getOlsonTimezone())
                .bdaType(geography.getBdaType())
                .hsudName(geography.getHsudName())
//                .alternateNames(mapToAlternateName(geography.getAlternateNames()))
//                .alternateCodes(mapToAlternateCode(geography.getAlternateCodes()))
//                .countries(mapToCountry(geography.getCountry()))
//                .parents(mapToParent(geography.getParent()))
//                .subCityParents(mapToSubCityParent(geography.getSubCityParent()))
//                .businessDefinedAreas(mapToBusinessDefinedArea(geography.getBda()))
//                .businessDefinedAreaLocations(mapToBusinessDefinedAreaLocation(geography.getBdaLocations()))
                .build();
    }

    private String findCode(List<alternateCodes> alternateCodes, String type) {
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(com.maersk.Geography.smds.operations.MSK.alternateCodes::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    private List<BusinessDefinedAreaLocation> mapToBusinessDefinedAreaLocation(List<bdaLocation> bdaLocations) {
        // TODO refactor to cleaner code
        if (null == bdaLocations) {
            return Collections.emptyList();
        }

        return bdaLocations.stream().map(location ->
                        BusinessDefinedAreaLocation.builder()
                                .name(location.getName())
                                .status(location.getStatus())
                                .type(location.getType())
                                .alternateCodes(mapToBDALocationAlternateCode(location.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<BusinessDefinedArea> mapToBusinessDefinedArea(List<bda> bdas) {

        return Optional.ofNullable(bdas)
                .orElse(Collections.emptyList()).stream().map(bda ->
                        BusinessDefinedArea.builder()
                                .name(bda.getName())
                                .bdaType(bda.getBdaType())
                                .alternateCodes(mapToBDAAlternateCode(bda.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<SubCityParent> mapToSubCityParent(List<subCityParent> subCityParents) {
        return Optional.ofNullable(subCityParents)
                .orElse(Collections.emptyList()).stream().map(subCityParent ->
                        SubCityParent.builder()
                                .name(subCityParent.getName())
                                .type(subCityParent.getType())
                                .alternateCodes(mapToSubCityParentAlternateCode(subCityParent.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<Parent> mapToParent(List<parent> parents) {
        return Optional.ofNullable(parents)
                .orElse(Collections.emptyList()).stream().map(parent ->
                        Parent.builder()
                                .name(parent.getName())
                                .bdaType(parent.getBdaType())
                                .alternateCodeList(mapToParentAlternateCode(parent.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<Country> mapToCountry(List<country> countries) {
        return Optional.ofNullable(countries)
                .orElse(Collections.emptyList()).stream().map(country ->
                        Country.builder()
                                .name(country.getName())
                                .type(country.getType())
                                .alternateCodes(mapToCountryAlternateCode(country.getAlternateCodes()))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToBDALocationAlternateCode(List<bdaLocationAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToBDAAlternateCode(List<bdaAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToSubCityParentAlternateCode(List<subCityParentAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToParentAlternateCode(List<parentAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToCountryAlternateCode(List<countryAlternateCodes> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCode.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToAlternateCode(List<alternateCodes> alternateCodes, String geoID) {
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

    private List<AlternateName> mapToAlternateName(List<alternateNames> alternateNames, String geoID) {
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
