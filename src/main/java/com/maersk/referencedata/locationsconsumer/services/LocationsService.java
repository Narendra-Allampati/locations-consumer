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
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCodeLink;
import com.maersk.referencedata.locationsconsumer.model.AlternateCodeWrapper;
import com.maersk.referencedata.locationsconsumer.model.BdaLocationWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.BdaWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.repositories.postgres.AlternateCodeLinksRepository;
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
    private final AlternateCodeLinksRepository alternateCodeLinksRepository;
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

        final var alternateCodesWrappers = mapToAlternateCodes(geography.getAlternateCodes(), geoID);
        List<AlternateCodeLink> alternateCodeLinks = alternateCodesWrappers.stream().map(AlternateCodeWrapper::getAlternateCodesLinks).toList();
        List<AlternateCode> alternateCodes = alternateCodesWrappers.stream().map(AlternateCodeWrapper::getAlternateCodes).toList();

        List<BdaWithAlternateCodes> bdaWithAlternateCodes = mapToBdaWithAlternateCodes(geography.getBdas());

        List<BdaLocationWithAlternateCodes> bdaLocationWithAlternateCodes = mapToBdaLocationsWithAlternateCodes(geography.getBdaLocations());

        return Flux.merge(geographyRepository.save(geo).then()
                        , alternateNameRepository.saveAll(alternateNames).then()
                        , alternateCodeRepository.saveAll(alternateCodes).then()
                        , alternateCodeLinksRepository.saveAll(alternateCodeLinks).then())
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
                    return SubCityParent.builder().rowId(geoID).name(scp.getName()).type(scp.getType()).build();
                })
                .toList();
    }

    private Optional<Parent> mapToParent(parent parentAvro) {
        return Optional.ofNullable(parentAvro)
                .map(pa -> {
                    List<parentAlternateCode> alternateCodes = pa.getAlternateCodes();
                    String geoID = GeographyMapper.findCodeFromParentAlternateCodes(alternateCodes, GEO_ID);
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

    private Geography mapGeographyEventToGeography(geography geography) {

        Geography geo = Geography.builder()
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

    private List<AlternateCodeWrapper> mapToAlternateCodes(List<alternateCode> alternateCodes, String geoID) {
        return alternateCodes
                .stream()
                .map(alternateCode -> {
                    AlternateCode ac = AlternateCode.builder()
                            .code(alternateCode.getCode())
                            .codeType(alternateCode.getCodeType())
                            .build();
                    AlternateCodeLink acl = AlternateCodeLink.builder()
                            .geoId(geoID)
                            .alternateCodeId(alternateCode.getCode())
                            .build();
                    return AlternateCodeWrapper.builder()
                            .alternateCodes(ac)
                            .alternateCodesLinks(acl)
                            .build();
                })
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
                .toList();
    }
}
