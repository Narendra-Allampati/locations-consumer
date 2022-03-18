package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.geography.smds.operations.msk.alternateCode;
import com.maersk.geography.smds.operations.msk.alternateName;
import com.maersk.geography.smds.operations.msk.bda;
import com.maersk.geography.smds.operations.msk.bdaAlternateCode;
import com.maersk.geography.smds.operations.msk.bdaLocation;
import com.maersk.geography.smds.operations.msk.bdaLocationAlternateCode;
import com.maersk.geography.smds.operations.msk.country;
import com.maersk.geography.smds.operations.msk.countryAlternateCode;
import com.maersk.geography.smds.operations.msk.geography;
import com.maersk.geography.smds.operations.msk.geographyMessage;
import com.maersk.geography.smds.operations.msk.parent;
import com.maersk.geography.smds.operations.msk.parentAlternateCode;
import com.maersk.geography.smds.operations.msk.subCityParent;
import com.maersk.geography.smds.operations.msk.subCityParentAlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateName;
import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedArea;
import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedAreaLocation;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Country;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Parent;
import com.maersk.referencedata.locationsconsumer.domains.postgres.SubCityParent;
import com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper;
import com.maersk.referencedata.locationsconsumer.model.BdaLocationWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.BdaWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.CountryWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.ParentWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.SubCityParentWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.AlternateCodeRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.AlternateNameRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.BusinessDefinedAreaLocationRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.BusinessDefinedAreaRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.CountryRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.GeographyRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.ParentRepository;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.SubCityParentRepository;
import com.maersk.shared.kafka.serialization.KafkaDeserializerUtils;
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
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final AlternateNameRepository alternateNameRepository;
    private final BusinessDefinedAreaRepository businessDefinedAreaRepository;
    private final BusinessDefinedAreaLocationRepository businessDefinedAreaLocationRepository;
    private final CountryRepository countryRepository;
    private final GeographyRepository geographyRepository;
    private final ParentRepository parentRepository;
    private final SubCityParentRepository subCityParentRepository;

    private static final String GEO_ID = "GEOID";
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
        Geography geo = mapGeographyEventToGeography(geography);

        String geoID = geography.getGeoId();

        final var alternateNames = mapToAlternateNames(geography.getAlternateNames(), geoID);

        final var alternateCodes = mapToAlternateCodes(geography.getAlternateCodes(), geoID);

        Optional<CountryWithAlternateCodes> countryWithAlternateCodes = mapToCountryWithAlternateCodes(geography.getCountry());

        Optional<ParentWithAlternateCodes> parentWithAlternateCodes = mapToParentWithAlternateCodes(geography.getParent());

        List<SubCityParentWithAlternateCodes> subCityParentWithAlternateCodes = mapToSubCityParentsWithAlternateCodes(geography.getSubCityParents());

        List<BdaWithAlternateCodes> bdaWithAlternateCodes = mapToBdaWithAlternateCodes(geography.getBdas());

        List<BdaLocationWithAlternateCodes> bdaLocationWithAlternateCodes = mapToBdaLocationsWithAlternateCodes(geography.getBdaLocations());

        List<SubCityParent> subCityParents = subCityParentWithAlternateCodes.stream()
                .map(SubCityParentWithAlternateCodes::getSubCityParent).toList();

        List<BusinessDefinedArea> businessDefinedAreas = bdaWithAlternateCodes.stream()
                .map(BdaWithAlternateCodes::getBusinessDefinedArea).toList();

        List<BusinessDefinedAreaLocation> businessDefinedAreaLocations = bdaLocationWithAlternateCodes.stream()
                .map(BdaLocationWithAlternateCodes::getBusinessDefinedAreaLocation).toList();

        return Flux.merge(geographyRepository.save(geo).then()
                        , alternateNameRepository.saveAll(alternateNames).then()
                        , alternateCodeRepository.saveAll(alternateCodes).then()
                        , countryRepository.saveAll(Mono.justOrEmpty(countryWithAlternateCodes)
                                .map(CountryWithAlternateCodes::getCountry)).then()
                        , parentRepository.saveAll(Mono.justOrEmpty(parentWithAlternateCodes)
                                .map(ParentWithAlternateCodes::getParent)).then()
                        , subCityParentRepository.saveAll(subCityParents).then()
                        , businessDefinedAreaRepository.saveAll(businessDefinedAreas).then()
                        , businessDefinedAreaLocationRepository.saveAll(businessDefinedAreaLocations).then())
                .then();
    }

    private List<BdaWithAlternateCodes> mapToBdaWithAlternateCodes(List<bda> bdas) {
        return Optional.ofNullable(bdas)
                .orElse(Collections.emptyList())
                .stream()
                .map(bda -> {
                    List<bdaAlternateCode> alternateCodes = bda.getAlternateCodes();
                    String geoID = GeographyMapper.findCodeFromBdaAlternateCodes(alternateCodes, GEO_ID);
                    BusinessDefinedArea businessDefinedArea = BusinessDefinedArea.builder()
                            .rowId(geoID)
                            .name(bda.getName()).type(bda.getType()).bdaType(bda.getBdaType())
                            .build();
                    return new BdaWithAlternateCodes(businessDefinedArea, mapToBDAAlternateCodes(alternateCodes, geoID));
                })
                .collect(Collectors.toList());
    }

    private List<BdaLocationWithAlternateCodes> mapToBdaLocationsWithAlternateCodes(List<bdaLocation> bdaLocations) {
        return Optional.ofNullable(bdaLocations)
                .orElse(Collections.emptyList())
                .stream()
                .map(bdaLocation -> {
                    List<bdaLocationAlternateCode> alternateCodes = bdaLocation.getAlternateCodes();
                    String geoID = GeographyMapper.findCodeFromBdaLocationAlternateCodes(alternateCodes, GEO_ID);
                    BusinessDefinedAreaLocation businessDefinedAreaLocation = BusinessDefinedAreaLocation.builder()
                            .rowId(geoID)
                            .name(bdaLocation.getName()).type(bdaLocation.getType()).status(bdaLocation.getStatus())
                            .build();
                    return new BdaLocationWithAlternateCodes(businessDefinedAreaLocation,
                            mapToBDALocationAlternateCodes(alternateCodes, geoID));
                })
                .collect(Collectors.toList());
    }

    private List<SubCityParentWithAlternateCodes> mapToSubCityParentsWithAlternateCodes(List<subCityParent> subCityParents) {
        return Optional.ofNullable(subCityParents)
                .orElse(Collections.emptyList())
                .stream()
                .map(scp -> {
                    SubCityParent subCityParent = SubCityParent.builder().name(scp.getName()).type(scp.getType()).bdaType(scp.getBdaType()).build();
                    List<subCityParentAlternateCode> alternateCodes = scp.getAlternateCodes();
                    String geoID = GeographyMapper.findCodeFromSubCityParentAlternateCodes(alternateCodes, GEO_ID);
                    return new SubCityParentWithAlternateCodes(subCityParent, mapToSubCityParentAlternateCodes(alternateCodes, geoID));
                })
                .collect(Collectors.toList());
    }

    private Optional<ParentWithAlternateCodes> mapToParentWithAlternateCodes(parent parentAvro) {
        return Optional.ofNullable(parentAvro)
                .map(pa -> {
                    Parent parent = Parent.builder().name(pa.getName()).type(pa.getType()).build();
                    List<parentAlternateCode> alternateCodes = pa.getAlternateCodes();
                    String geoID = GeographyMapper.findCodeFromParentAlternateCodes(alternateCodes, GEO_ID);
                    return new ParentWithAlternateCodes(parent, mapToParentAlternateCodes(alternateCodes, geoID));
                });
    }

    private Optional<CountryWithAlternateCodes> mapToCountryWithAlternateCodes(country countryAvro) {
        return Optional.ofNullable(countryAvro)
                .map(ca -> {
                    List<countryAlternateCode> alternateCodes = ca.getAlternateCodes();
                    String geoID = GeographyMapper.findCodeFromCountryAlternateCodes(alternateCodes, GEO_ID);
                    Country country = Country.builder().rowId(geoID).name(ca.getName()).build();
                    return new CountryWithAlternateCodes(country, mapToCountryAlternateCodes(alternateCodes, geoID));
                });
    }

    private Geography mapGeographyEventToGeography(geography geography) {
        return Geography.builder()
                .geoId(geography.getGeoId())
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
                .postalCodeMandatory(geography.getPostalCodeMandatory())
                .stateProvinceMandatory(geography.getStateProvinceMandatory())
                .dialingCode(geography.getDialingCode())
                .dialingCodeDescription(geography.getDialingCodeDescription())
                .portFlag(geography.getPortFlag())
                .olsonTimeZone(geography.getOlsonTimezone())
                .bdaType(geography.getBdaType())
                .hsudName(geography.getHsudName())
                .build();
    }
//
//    private List<BusinessDefinedAreaLocation> mapToBusinessDefinedAreaLocations(List<bdaLocation> bdaLocations) {
//        // TODO refactor to cleaner code
//        if (null == bdaLocations) {
//            return Collections.emptyList();
//        }
//
//        return bdaLocations.stream().map(location ->
//                        BusinessDefinedAreaLocation.builder()
//                                .name(location.getName())
//                                .status(location.getStatus())
//                                .type(location.getType())
//                                .alternateCodes(mapToBDALocationAlternateCodes(location.getAlternateCodes()))
//                                .build()
//                )
//                .collect(Collectors.toList());
//    }
//
//    private List<BusinessDefinedArea> mapToBusinessDefinedAreas(List<bda> bdas) {
//
//        return Optional.ofNullable(bdas)
//                .orElse(Collections.emptyList()).stream().map(bda ->
//                        BusinessDefinedArea.builder()
//                                .name(bda.getName())
//                                .bdaType(bda.getBdaType())
//                                .alternateCodes(mapToBDAAlternateCodes(bda.getAlternateCodes()))
//                                .build()
//                )
//                .collect(Collectors.toList());
//    }
//
//    private List<SubCityParent> mapToSubCityParents(List<subCityParent> subCityParents) {
//        return Optional.ofNullable(subCityParents)
//                .orElse(Collections.emptyList()).stream().map(subCityParent ->
//                        SubCityParent.builder()
//                                .name(subCityParent.getName())
//                                .type(subCityParent.getType())
//                                .alternateCodes(mapToSubCityParentAlternateCodes(subCityParent.getAlternateCodes()))
//                                .build()
//                )
//                .collect(Collectors.toList());
//    }
//
//    private List<Parent> mapToParents(List<parent> parents) {
//        return Optional.ofNullable(parents)
//                .orElse(Collections.emptyList()).stream().map(parent ->
//                        Parent.builder()
//                                .name(parent.getName())
//                                .bdaType(parent.getBdaType())
//                                .alternateCodeList(mapToParentAlternateCodes(parent.getAlternateCodes()))
//                                .build()
//                )
//                .collect(Collectors.toList());
//    }
//
//    private List<Country> mapToCountrys(List<country> countries) {
//        return Optional.ofNullable(countries)
//                .orElse(Collections.emptyList()).stream().map(country ->
//                        Country.builder()
//                                .name(country.getName())
//                                .type(country.getType())
//                                .alternateCodes(mapToCountryAlternateCodes(country.getAlternateCodes()))
//                                .build()
//                )
//                .collect(Collectors.toList());
//    }

    private List<AlternateCode> mapToBDALocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoId)
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToBDAAlternateCodes(List<bdaAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoId)
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoId)
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToParentAlternateCodes(List<parentAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoId)
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToCountryAlternateCodes(List<countryAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoId)
                                .codeType(alternateCode.getCodeType())
                                .code(alternateCode.getCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToAlternateCodes(List<alternateCode> alternateCodes, String geoID) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                .rowId(geoID)
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
//
//    private ZonedDateTime mapLongToZonedDateTime(Long epoch) {
//        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.of(TIME_ZONE_UTC));
//    }
//
//    private Mono<Void> storeEvent(ReceiverRecord<String, geographyMessage> geographyRecord) {
//        getMappedObject(geographyRecord.value());
//        return Mono.empty();
//    }
//
//    private Geography getMappedObject(geographyMessage geographyMessage) {
//        return GeographyMapper.mapAvroToDomainModel(geographyMessage);
//    }


    //    private final AlternateCodeRepository alternateCodeRepository;
//    private final BusinessDefinedAreasRepository businessDefinedAreasRepository;
//    private final CitiesRepository citiesRepository;
//    private final CitySubAreaRepository citySubAreaRepository;
//    private final ContinentRepository continentRepository;
//    private final CountriesRepository countriesRepository;
//    private final PostalCodeRepository postalCodeRepository;
//    private final StateProvinceRepository stateProvinceRepository;

//    private Mono<Void> mapAndSaveGeographyEventNeo4j(geography geography) {
//
//        final var geoType = geography.getGeoType();
//
//        if (geoType.equals("Business Defined Area")) {
//            return businessDefinedAreasRepository.save(mapToBusinessDefinedAreaEntity(geography))
//                    .then();
//        } else if (geoType.equals("City")) {
//            return Mono.zip(saveAlternateCodes(geography.getAlternateCodes()),
//                            citiesRepository.save(mapToCityEntity(geography)))
//                    .then();
//        } else if (geoType.equals("Continent")) {
//            return Mono.zip(saveAlternateCodes(geography.getAlternateCodes()),
//                            continentRepository.save(mapToContinentEntity(geography)))
//                    .then();
//        } else if (geoType.equals("Country")) {
//            return Mono.zip(saveAlternateCodes(geography.getAlternateCodes()),
//                            countriesRepository.save(mapToCountryEntity(geography)))
//                    .then();
//        } else if (geoType.equals("Postal Code")) {
//            return Mono.empty();
////            return postalCodeRepository.save(mapToPostalCodeEntity(geography))
////                    .then();
//        } else if (geoType.equals("CitySubArea")) {
//            return citySubAreaRepository.save(mapToCitySubAreaEntity(geography))
//                    .then();
//        } else if (geoType.equals("State/Prov")) {
//            return stateProvinceRepository.save(mapToStateProvinceEntity(geography))
//                    .then();
//        } else {
//            log.error(geoType);
//            return Mono.empty();
//        }
//    }

//    private Mono<List<AlternateCodeEntity>> saveAlternateCodes(List<alternateCode> alternateCodes) {
//        return alternateCodeRepository.saveAll(mapToAlternateCodeEntities(alternateCodes))
//                .collectList();
//    }
//
//    private List<AlternateCodeEntity> mapToAlternateCodeEntities(List<alternateCode> alternateCodes) {
//        return alternateCodes
//                .stream()
//                .map(alternateCode ->
//                        AlternateCodeEntity.builder()
//                                .id(alternateCode.getCodeType() + alternateCode.getCode())
//                                .codeType(alternateCode.getCodeType())
//                                .code(alternateCode.getCode())
//                                .build()
//                )
//                .toList();
//    }
//
//    private CitySubAreaEntity mapToCitySubAreaEntity(geography geography) {
//        return CitySubAreaEntity.builder()
//                .geoId(geography.getGeoId())
//                .build();
//    }
//
//    private PostalCodeEntity mapToPostalCodeEntity(geography geography) {
//        return PostalCodeEntity.builder()
//                .geoId(geography.getGeoId())
//                .name(geography.getName())
//                .build();
//    }
//
//    private ContinentEntity mapToContinentEntity(geography geography) {
//        return ContinentEntity.builder()
//                .geoId(geography.getGeoId())
//                .name(geography.getName())
//                .alternateCodeEntities(mapToAlternateCodeEntities(geography.getAlternateCodes()))
//                .build();
//    }
//
//    private StateProvinceEntity mapToStateProvinceEntity(geography geography) {
//        return StateProvinceEntity.builder()
//                .geoId(geography.getGeoId())
//                .name(geography.getName())
//                .build();
//    }
//
//    private BusinessDefinedAreaEntity mapToBusinessDefinedAreaEntity(geography geography) {
//        return BusinessDefinedAreaEntity.builder()
//                .geoId(geography.getGeoId())
//                .name(geography.getName())
//                .build();
//    }
//
//    private CountryEntity mapToCountryEntity(geography geography) {
//        return CountryEntity.builder()
//                .geoId(geography.getGeoId())
//                .name(geography.getName())
//                .alternateCodeEntities(mapToAlternateCodeEntities(geography.getAlternateCodes()))
//                .build();
//    }
//
//    private CityEntity mapToCityEntity(geography geography) {
//        country country = geography.getCountries().get(0);
//        Optional<countryAlternateCode> geoId = country.getAlternateCodes().stream().filter(e -> e.getCodeType().equals("GEOID")).findFirst();
//        return CityEntity.builder()
//                .geoId(geography.getGeoId())
//                .name(geography.getName())
//                .country(CountryEntity.builder().geoId(geoId.get().getCode()).name(country.getName()).build())
//                .status(geography.getStatus())
//                .validFrom(geography.getValidFrom())
//                .validTo(geography.getValidTo())
//                .longitude(geography.getLongitude())
//                .latitude(geography.getLatitude())
//                .timeZone(geography.getTimeZone())
//                .daylightSavingTime(geography.getDaylightSavingTime())
//                .utcOffsetMinutes(geography.getUtcOffsetMinutes())
//                .daylightSavingStart(geography.getDaylightSavingStart())
//                .daylightSavingEnd(geography.getDaylightSavingEnd())
//                .daylightSavingShiftMinutes(geography.getDaylightSavingShiftMinutes())
//                .description(geography.getDescription())
//                .workaroundReason(geography.getWorkaroundReason())
//                .restricted(geography.getRestricted())
//                .postalCodeMandatoryFlag(geography.getPostalCodeMandatory())
//                .stateProvinceMandatory(geography.getStateProvinceMandatory())
//                .dialingCode(geography.getDialingCode())
//                .dialingCodeDescription(geography.getDialingCodeDescription())
//                .portFlag(geography.getPortFlag())
//                .olsonTimeZone(geography.getOlsonTimezone())
//                .bdaType(geography.getBdaType())
//                .hsudName(geography.getHsudName())
//                .alternateCodeEntities(mapToAlternateCodeEntities(geography.getAlternateCodes()))
//                .build();
//    }
}
