package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.geography.smds.operations.msk.*;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.*;
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCodePostgres;
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateName;
import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedArea;
import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedAreaLocation;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Country;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Parent;
import com.maersk.referencedata.locationsconsumer.domains.postgres.SubCityParent;
import com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper;
import com.maersk.referencedata.locationsconsumer.repositories.*;
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

    private final AlternateCodeRepository alternateCodeRepository;
    private final BusinessDefinedAreasRepository businessDefinedAreasRepository;
    private final CitiesRepository citiesRepository;
    private final CitySubAreaRepository citySubAreaRepository;
    private final ContinentRepository continentRepository;
    private final CountriesRepository countriesRepository;
    private final PostalCodeRepository postalCodeRepository;
    private final StateProvinceRepository stateProvinceRepository;

    private static final String TIME_ZONE_UTC = "UTC";

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return kafkaReceiver
                .receive()
                .doOnNext(event -> log.info("Received event: key {}, value {}", event.key(), event.value()))
                .doOnError(error -> log.error("Error receiving Geography record", error))
                .concatMap(this::handleSubscriptionResponseEvent)
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

        final var geoType = geography.getGeoType();

        if (geoType.equals("Business Defined Area")) {
            return businessDefinedAreasRepository.save(mapToBusinessDefinedAreaEntity(geography))
                    .then();
        } else if (geoType.equals("City")) {
            return Mono.zip(saveAlternateCodes(geography.getAlternateCodes()),
                            citiesRepository.save(mapToCityEntity(geography)))
                    .then();
        } else if (geoType.equals("Continent")) {
            return Mono.zip(saveAlternateCodes(geography.getAlternateCodes()),
                            continentRepository.save(mapToContinentEntity(geography)))
                    .then();
        } else if (geoType.equals("Country")) {
            return Mono.zip(saveAlternateCodes(geography.getAlternateCodes()),
                            countriesRepository.save(mapToCountryEntity(geography)))
                    .then();
        } else if (geoType.equals("Postal Code")) {
            return Mono.empty();
//            return postalCodeRepository.save(mapToPostalCodeEntity(geography))
//                    .then();
        } else if (geoType.equals("CitySubArea")) {
            return citySubAreaRepository.save(mapToCitySubAreaEntity(geography))
                    .then();
        } else if (geoType.equals("State/Prov")) {
            return stateProvinceRepository.save(mapToStateProvinceEntity(geography))
                    .then();
        } else {
            log.error(geoType);
            return Mono.empty();
        }
    }

    private Mono<List<AlternateCodeEntity>> saveAlternateCodes(List<alternateCode> alternateCodes) {
        return alternateCodeRepository.saveAll(mapToAlternateCodeEntities(alternateCodes))
                .collectList();
    }

    private List<AlternateCodeEntity> mapToAlternateCodeEntities(List<alternateCode> alternateCodes) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCodeEntity.builder()
                                .id(alternateCode.getCodeType() + alternateCode.getCode())
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .toList();
    }

    private CitySubAreaEntity mapToCitySubAreaEntity(geography geography) {
        return CitySubAreaEntity.builder()
                .geoId(geography.getGeoId())
                .build();
    }

    private PostalCodeEntity mapToPostalCodeEntity(geography geography) {
        return PostalCodeEntity.builder()
                .geoId(geography.getGeoId())
                .name(geography.getName())
                .build();
    }

    private ContinentEntity mapToContinentEntity(geography geography) {
        return ContinentEntity.builder()
                .geoId(geography.getGeoId())
                .name(geography.getName())
                .alternateCodeEntities(mapToAlternateCodeEntities(geography.getAlternateCodes()))
                .build();
    }

    private StateProvinceEntity mapToStateProvinceEntity(geography geography) {
        return StateProvinceEntity.builder()
                .geoId(geography.getGeoId())
                .name(geography.getName())
                .build();
    }

    private BusinessDefinedAreaEntity mapToBusinessDefinedAreaEntity(geography geography) {
        return BusinessDefinedAreaEntity.builder()
                .geoId(geography.getGeoId())
                .name(geography.getName())
                .build();
    }

    private CountryEntity mapToCountryEntity(geography geography) {
        return CountryEntity.builder()
                .geoId(geography.getGeoId())
                .name(geography.getName())
                .alternateCodeEntities(mapToAlternateCodeEntities(geography.getAlternateCodes()))
                .build();
    }

    private CityEntity mapToCityEntity(geography geography) {
        country country = geography.getCountries().get(0);
        Optional<countryAlternateCode> geoId = country.getAlternateCodes().stream().filter(e -> e.getCodeType().equals("GEOID")).findFirst();
        return CityEntity.builder()
                .geoId(geography.getGeoId())
                .name(geography.getName())
                .country(CountryEntity.builder().geoId(geoId.get().getCode()).name(country.getName()).build())
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
                .alternateCodeEntities(mapToAlternateCodeEntities(geography.getAlternateCodes()))
                .build();
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

    private List<AlternateCodePostgres> mapToBDALocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCodePostgres.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCodePostgres> mapToBDAAlternateCodes(List<bdaAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCodePostgres.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCodePostgres> mapToSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCodePostgres.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCodePostgres> mapToParentAlternateCodes(List<parentAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCodePostgres.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCodePostgres> mapToCountryAlternateCodes(List<countryAlternateCode> alternateCodes) {
        return Optional.ofNullable(alternateCodes)
                .orElse(Collections.emptyList()).stream().map(alternateCode ->
                        AlternateCodePostgres.builder()
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCodePostgres> mapToAlternateCodes(List<alternateCode> alternateCodes) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCodePostgres.builder()
                                .rowId(alternateCode.getCodeType() + alternateCode.getCode())
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .toList();
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
}
