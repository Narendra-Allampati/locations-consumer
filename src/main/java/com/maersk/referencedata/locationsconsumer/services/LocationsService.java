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
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateName;
import com.maersk.referencedata.locationsconsumer.domains.locations.BusinessDefinedArea;
import com.maersk.referencedata.locationsconsumer.domains.locations.BusinessDefinedAreaLocation;
import com.maersk.referencedata.locationsconsumer.domains.locations.Country;
import com.maersk.referencedata.locationsconsumer.domains.locations.Geography;
import com.maersk.referencedata.locationsconsumer.domains.locations.Parent;
import com.maersk.referencedata.locationsconsumer.domains.locations.PostalCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.SubCityParent;
import com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper;
import com.maersk.referencedata.locationsconsumer.domains.locations.GeoAlternateCodeLink;
import com.maersk.referencedata.locationsconsumer.model.locations.AlternateCodeWrapper;
import com.maersk.referencedata.locationsconsumer.model.locations.BdaLocationWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.locations.BdaWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.repositories.locations.GeoAlternateCodeLinksRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.AlternateCodeRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.AlternateNameRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.BusinessDefinedAreaLocationRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.BusinessDefinedAreaRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.CountryRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.GeographyRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.ParentRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.PostalCodeRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.SubCityParentRepository;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationsService {

    private final KafkaReceiver<String, geographyMessage> locationsKafkaReceiver;

    private final AlternateCodeRepository alternateCodeRepository;
    private final GeoAlternateCodeLinksRepository geoAlternateCodeLinksRepository;
    private final AlternateNameRepository alternateNameRepository;
    private final BusinessDefinedAreaRepository businessDefinedAreaRepository;
    private final BusinessDefinedAreaLocationRepository businessDefinedAreaLocationRepository;
    private final CountryRepository countryRepository;
    private final GeographyRepository geographyRepository;
    private final ParentRepository parentRepository;
    private final PostalCodeRepository postalCodeRepository;
    private final SubCityParentRepository subCityParentRepository;

    private static final String GEO_ID = "GEOID";
    private static final String POSTAL_CODE = "Postal Code";

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return locationsKafkaReceiver
                .receive()
                .name("geo events")
                .tag("source", "kafka")
                .metrics()
                .doOnError(error -> log.warn("Error receiving Geography record, exception -> {}, retry will be attempted",
                        error.getLocalizedMessage(), error))
                .retryWhen(Retry.indefinitely().filter(ErrorHandlingUtils::isRetriableKafkaError))
                .doOnError(error -> log.warn("Error thrown whilst processing geography records, error isn't a " +
                        "known retriable error, will attempt to retry processing records , exception -> {}", error.getLocalizedMessage(), error))
                .retryWhen(Retry.fixedDelay(100, Duration.ofMinutes(1)))
//                .doOnNext(event -> log.debug("Received geo event: key {}, value {}", event.key(), event.value()))
                .doOnNext(event -> log.info("Received geo event: key {}, geoId {}, partition number {}", event.key()
                        , event.value().getGeography().getGeoId()
                        ,event.receiverOffset().topicPartition().partition()))
                .concatMap(this::handleLocationEvent)
                .subscribe(result -> result.receiverOffset().acknowledge());
    }

    private Mono<ReceiverRecord<String, geographyMessage>> handleLocationEvent(ReceiverRecord<String, geographyMessage> geographyRecord) {

        return Mono.just(geographyRecord)
                   .map(KafkaDeserializerUtils::extractDeserializerError)
                   .<com.maersk.geography.smds.operations.msk.geographyMessage>handle((tuple, sink) -> {
                       if (tuple.getT2().isEmpty() && Objects.nonNull(tuple.getT1().value())) {
                           sink.next(tuple.getT1().value());
                       } else {
                           log.error("Error while processing geographyMessage " + tuple.getT2().get());
                       }
                   })
                   .flatMap(geographyMessage -> createOrUpdate(geographyMessage.getGeography()))
                   .doOnError(ex -> log.warn("Error processing event {} and value {}", geographyRecord.key(), geographyRecord.value(), ex))
                   .onErrorResume(ex -> Mono.empty())
                   .then(Mono.just(geographyRecord));
    }

    private Mono<String> createOrUpdate(geography geography) {
        if (POSTAL_CODE.equals(geography.getGeoType())) {
            return postalCodeRepository.findById(geography.getGeoId())
                                       .flatMap(geographyFromDB -> updateGeography(geography))
                                       .switchIfEmpty(Mono.defer(() -> saveGeography(geography)));
        }

        return geographyRepository.findById(geography.getGeoId())
                                  .flatMap(geographyFromDB -> updateGeography(geography))
                                  .switchIfEmpty(Mono.defer(() -> saveGeography(geography)));
    }

    private Mono<String> saveGeography(geography geography) {
        return mapAndSaveGeographyEvent(geography, true);
    }

    private Mono<String> updateGeography(geography geography) {
        if (POSTAL_CODE.equals(geography.getGeoType())) {
            return postalCodeRepository.deleteById(geography.getGeoId())
                                       .then(saveGeography(geography));
        } else {
            return geographyRepository.deleteById(geography.getGeoId())
                                      .then(saveGeography(geography));
        }
    }

    private Mono<String> mapAndSaveGeographyEvent(geography geography, boolean isNew) {

        String geoID = geography.getGeoId();

        Optional<Geography> geo = mapGeographyEventToGeography(geography, isNew);

        Optional<PostalCode> postalCode = mapGeographyEventToPostalCode(geography, isNew);

        final var alternateNames = mapToAlternateNames(geography.getAlternateNames(), geoID, isNew);

//        final var alternateCodesWrappers = mapToAlternateCodesWrapper(geography.getAlternateCodes(), geoID, isNew);
//        List<GeoAlternateCodeLink> geoAlternateCodeLinks = alternateCodesWrappers.stream().map(AlternateCodeWrapper::getAlternateCodesLinks).toList();
//        List<AlternateCode> alternateCodes = alternateCodesWrappers.stream().map(AlternateCodeWrapper::getAlternateCodes).toList();
        List<AlternateCode> alternateCodes = mapToAlternateCodes(geography.getAlternateCodes(), geoID, geography.getGeoType(), isNew);

        List<BdaWithAlternateCodes> bdaWithAlternateCodes = mapToBdaWithAlternateCodes(geography.getBdas());

        List<BdaLocationWithAlternateCodes> bdaLocationWithAlternateCodes = mapToBdaLocationsWithAlternateCodes(geography.getBdaLocations());

        return Flux.concat(geographyRepository.saveAll(Mono.justOrEmpty(geo)).then()
                           , postalCodeRepository.saveAll(Mono.justOrEmpty(postalCode)).then()
                           , alternateNameRepository.saveAll(alternateNames).then()
                           , alternateCodeRepository.saveAll(alternateCodes).doOnError(e ->
                                   log.warn("Geo ID {} with geo name {} and geo type {}",
                                           geoID, geography.getName(), geography.getGeoType(), e)).then())
//                        , geoAlternateCodeLinksRepository.saveAll(geoAlternateCodeLinks).then())
                   .then(Mono.just("1"));
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
                       .toList();
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
                       .toList();
    }

    private List<SubCityParent> mapToSubCityParents(List<subCityParent> subCityParents) {
        return Optional.ofNullable(subCityParents)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(scp -> {
                           List<subCityParentAlternateCode> alternateCodes = scp.getAlternateCodes();
                           String geoID = GeographyMapper.findCodeFromSubCityParentAlternateCodes(alternateCodes, GEO_ID);
                           return SubCityParent.builder()
                                               .rowId(geoID)
                                               .name(scp.getName())
                                               .type(scp.getType())
                                               .build();
                       })
                       .toList();
    }

    private Optional<Parent> mapToParent(parent parentAvro) {
        return Optional.ofNullable(parentAvro)
                       .map(pa -> {
                           List<parentAlternateCode> alternateCodes = pa.getAlternateCodes();
                           String geoID = GeographyMapper.findCodeFromGeoParentAlternateCodes(alternateCodes, GEO_ID);
                           return Parent.builder().rowId(geoID).name(pa.getName()).type(pa.getType()).build();
                       });
    }

    private Optional<Country> mapToCountry(country countryAvro) {
        return Optional.ofNullable(countryAvro)
                       .map(ca -> {
                           List<countryAlternateCode> alternateCodes = ca.getAlternateCodes();
                           String geoID = GeographyMapper.findCodeFromCountryAlternateCodes(alternateCodes, GEO_ID);
                           return Country.builder().rowId(geoID).name(ca.getName()).build();
                       });
    }

    private Optional<PostalCode> mapGeographyEventToPostalCode(geography geography, boolean isNew) {

        if (POSTAL_CODE.equals(geography.getGeoType())) {
            var postalCode = buildPostalCode(geography, isNew);
            return Optional.of(postalCode);
        }

        return Optional.empty();
    }

    private PostalCode buildPostalCode(geography geography, boolean isNew) {
        PostalCode postalCode = PostalCode.builder()
                                          .isNew(isNew)
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

        Optional<Country> countryOptional = mapToCountry(geography.getCountry());
        if (countryOptional.isPresent()) {
            Country country = countryOptional.get();
            postalCode.setCountryId(country.getRowId());
            postalCode.setCountryName(country.getName());
        }

        Optional<Parent> parentOptional = mapToParent(geography.getParent());
        if (parentOptional.isPresent()) {
            Parent parent = parentOptional.get();
            postalCode.setParentId(parent.getRowId());
            postalCode.setParentName(parent.getName());
            postalCode.setParentType(parent.getType());
        }

        List<SubCityParent> subCityParents = mapToSubCityParents(geography.getSubCityParents());
        if (!subCityParents.isEmpty()) {
            SubCityParent subCityParent = subCityParents.get(0);
            postalCode.setSubCityParentId(subCityParent.getRowId());
            postalCode.setSubCityParentName(postalCode.getSubCityParentName());
            postalCode.setSubCityParentType(postalCode.getSubCityParentType());
        }

        return postalCode;
    }

    private Optional<Geography> mapGeographyEventToGeography(geography geography, boolean isNew) {

        // Only build the Geography object if it is not of type Postal code
        if (POSTAL_CODE.equals(geography.getGeoType())) {
            return Optional.empty();
        }
        Geography geo = buildGeography(geography, isNew);
        return Optional.of(geo);

    }

    private Geography buildGeography(geography geography, boolean isNew) {
        Geography geo = Geography.builder()
                                 .isNew(isNew)
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

        Optional<Country> countryOptional = mapToCountry(geography.getCountry());
        if (countryOptional.isPresent()) {
            Country country = countryOptional.get();
            geo.setCountryId(country.getRowId());
            geo.setCountryName(country.getName());
        }

        Optional<Parent> parentOptional = mapToParent(geography.getParent());
        if (parentOptional.isPresent()) {
            Parent parent = parentOptional.get();
            geo.setParentId(parent.getRowId());
            geo.setParentName(parent.getName());
            geo.setParentType(parent.getType());
        }

        List<SubCityParent> subCityParents = mapToSubCityParents(geography.getSubCityParents());
        if (!subCityParents.isEmpty()) {
            SubCityParent subCityParent = subCityParents.get(0);
            geo.setSubCityParentId(subCityParent.getRowId());
            geo.setSubCityParentName(geo.getSubCityParentName());
            geo.setSubCityParentType(geo.getSubCityParentType());
        }

        return geo;
    }

    private List<AlternateCode> mapToBDALocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToBDAAlternateCodes(List<bdaAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .collect(Collectors.toList());
    }

    private List<AlternateCode> mapToSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }

    private List<AlternateCode> mapToParentAlternateCodes(List<parentAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }

    private List<AlternateCode> mapToCountryAlternateCodes(List<countryAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream().map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }

    private List<AlternateCode> mapToAlternateCodes(List<alternateCode> alternateCodes, String geoID, String geoType, boolean isNew) {
        // TODO temporary hack to see if all other inserts go well
        if (POSTAL_CODE.equals(geoType)) {
            return new ArrayList<>();
        }

        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                     .isNew(isNew)
                                     .id(UUID.randomUUID())
                                     .geoId(geoID)
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }

    private List<AlternateCodeWrapper> mapToAlternateCodesWrapper(List<alternateCode> alternateCodes, String geoID, boolean isNew) {
        return alternateCodes
                .stream()
                .map(alternateCode -> {
                    AlternateCode ac = AlternateCode.builder()
                                                    .isNew(isNew)
                                                    .code(alternateCode.getCode())
                                                    .codeType(alternateCode.getCodeType())
                                                    .build();
                    GeoAlternateCodeLink acl = GeoAlternateCodeLink.builder()
                                                                   .isNew(isNew)
                                                                   .id(UUID.randomUUID())
                                                                   .geoId(geoID)
                                                                   .alternateCodeId(alternateCode.getCode())
                                                                   .alternateCodeType(alternateCode.getCodeType())
                                                                   .build();
                    return AlternateCodeWrapper.builder()
                                               .alternateCodes(ac)
                                               .alternateCodesLinks(acl)
                                               .build();
                })
                .toList();
    }

    private List<AlternateName> mapToAlternateNames(List<alternateName> alternateNames, String geoID, boolean isNew) {
        return Optional.ofNullable(alternateNames)
                       .orElse(Collections.emptyList()).stream().map(alternateName ->
                        AlternateName.builder()
                                     .isNew(isNew)
                                     .id(UUID.randomUUID())
                                     .geoId(geoID)
                                     .name(alternateName.getName())
                                     .status(alternateName.getStatus())
                                     .description(alternateName.getDescription())
                                     .build()

                )
                       .toList();
    }
}
