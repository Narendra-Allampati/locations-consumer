package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.geography.smds.operations.msk.*;
import com.maersk.referencedata.locationsconsumer.constants.LocationsConstants;
import com.maersk.referencedata.locationsconsumer.domains.locations.*;
import com.maersk.referencedata.locationsconsumer.model.locations.AlternateCodeWrapper;
import com.maersk.referencedata.locationsconsumer.model.locations.BdaLocationWithAlternateCodes;
import com.maersk.referencedata.locationsconsumer.model.locations.BdaWithAlternateCodes;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;

import static com.maersk.referencedata.locationsconsumer.constants.LocationsConstants.POSTAL_CODE;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@UtilityClass
public class GeographyMapper {

    private static final String GEO_ID = "GEOID";

    public static String findCodeFromBdaAlternateCodes(List<bdaAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> type.equals(value.getCodeType()))
                             .findFirst()
                             .map(bdaAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    public static String findGeoIdFromFacilityParentAlternateCodes(List<com.maersk.facility.smds.operations.msk.parentAlternateCode> alternateCodes) {
        return alternateCodes.stream()
                             .filter(value -> GEO_ID.equals(value.getCodeType()))
                             .findFirst()
                             .map(com.maersk.facility.smds.operations.msk.parentAlternateCode::getCode)
                             .orElseThrow();
    }

    public static List<BdaWithAlternateCodes> mapToBdaWithAlternateCodes(List<bda> bdas) {
        return Optional.ofNullable(bdas)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(bda -> {
                           List<bdaAlternateCode> alternateCodes = bda.getAlternateCodes();
                           String geoId = GeographyMapper.findCodeFromBdaAlternateCodes(alternateCodes, GEO_ID);
                           BusinessDefinedArea businessDefinedArea = mapToBusinessDefinedArea(bda, geoId);
                           return new BdaWithAlternateCodes(businessDefinedArea, mapToBDAAlternateCodes(alternateCodes, geoId));
                       })
                       .toList();
    }

    public static List<BdaLocationWithAlternateCodes> mapToBdaLocationsWithAlternateCodes(List<bdaLocation> bdaLocations) {
        return Optional.ofNullable(bdaLocations)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(bdaLocation -> {
                           List<bdaLocationAlternateCode> alternateCodes = bdaLocation.getAlternateCodes();
                           String geoId = GeographyMapper.findCodeFromBdaLocationAlternateCodes(alternateCodes, GEO_ID);
                           BusinessDefinedAreaLocation businessDefinedAreaLocation = mapToBusinessDefinedLocation(bdaLocation, geoId);
                           return new BdaLocationWithAlternateCodes(businessDefinedAreaLocation,
                                   mapToBDALocationAlternateCodes(alternateCodes, geoId));
                       })
                       .toList();
    }

    public static List<AlternateCode> mapToAlternateCodes(List<alternateCode> alternateCodes, String geoId, String geoType) {
        // TODO temporary hack to see if all other inserts go well
        if (POSTAL_CODE.equals(geoType)) {
            return new ArrayList<>();
        }

        return alternateCodes
                .stream()
                .map(alternateCode -> mapToAlternateCode(alternateCode, geoId))
                .toList();
    }

    public static List<AlternateName> mapToAlternateNames(List<alternateName> alternateNames, String geoId) {
        return Optional.ofNullable(alternateNames)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(alternateName -> mapToAlternateName(alternateName, geoId))
                       .toList();
    }

    public static Optional<PostalCode> mapGeographyEventToPostalCode(geography geography) {

        if (POSTAL_CODE.equals(geography.getGeoType())) {
            var postalCode = buildPostalCode(geography);
            return Optional.of(postalCode);
        }

        return Optional.empty();
    }

    public static Optional<Geography> mapGeographyEventToGeography(geography geography) {

        // Only build the Geography object if it is not of type Postal code
        if (POSTAL_CODE.equals(geography.getGeoType())) {
            return Optional.empty();
        }
        Geography geo = buildGeography(geography);
        return Optional.of(geo);

    }

    private static AlternateName mapToAlternateName(alternateName alternateName, String geoId) {
        return AlternateName.builder()
                            .id(UUID.randomUUID())
                            .geoId(geoId)
                            .name(alternateName.getName())
                            .status(alternateName.getStatus())
                            .description(alternateName.getDescription())
                            .build();
    }

    private String findCodeFromCountryAlternateCodes(List<countryAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> type.equals(value.getCodeType()))
                             .findFirst()
                             .map(countryAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    private String findCodeFromGeoParentAlternateCodes(List<parentAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> type.equals(value.getCodeType()))
                             .findFirst()
                             .map(parentAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    private static String findCodeFromSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> type.equals(value.getCodeType()))
                             .findFirst()
                             .map(subCityParentAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    private static String findCodeFromBdaLocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> type.equals(value.getCodeType()))
                             .findFirst()
                             .map(bdaLocationAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    private static AlternateCode mapToAlternateCode(alternateCode alternateCode, String geoId) {
        return AlternateCode.builder()
                            .id(UUID.randomUUID())
                            .geoId(geoId)
                            .code(alternateCode.getCode())
                            .codeType(alternateCode.getCodeType())
                            .build();
    }

    private static Geography buildGeography(geography geography) {
        Geography geo = Geography.builder()
                                 .geoId(geography.getGeoId())
                                 .geoType(geography.getGeoType()
                                                   .toUpperCase())
                                 .name(geography.getName())
                                 .nameUpperCase(geography.getName()
                                                         .toUpperCase())
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
            geo.setCountryNameUpperCase(country.getName()
                                               .toUpperCase());
        }

        // TODO handle multiple parents
        List<parent> parents = geography.getParents();
        if (null != parents && !parents.isEmpty()) {
            Optional<Parent> parentOptional = mapToParent(parents.get(0));
            if (parentOptional.isPresent()) {
                Parent parent = parentOptional.get();
                geo.setParentId(parent.getRowId());
                geo.setParentName(parent.getName());
                geo.setParentType(parent.getType());
            }
        }

        List<SubCityParent> subCityParents = mapToSubCityParents(geography.getSubCityParents());
        if (!subCityParents.isEmpty()) {
            SubCityParent subCityParent = subCityParents.get(0);
            geo.setSubCityParentId(subCityParent.getRowId());
            geo.setSubCityParentName(geo.getSubCityParentName());
            geo.setSubCityParentType(geo.getSubCityParentType());
        }

        List<alternateCode> alternateCodes = geography.getAlternateCodes();
        for (alternateCode aCode : alternateCodes) {
            String codeType = aCode.getCodeType();
            if ("FIPS".equalsIgnoreCase(codeType)) {
                geo.setFips(aCode.getCode());
            } else if ("RKST".equalsIgnoreCase(codeType)) {
                geo.setRkst(aCode.getCode());
            } else if ("RKTS".equalsIgnoreCase(codeType)) {
                geo.setRkts(aCode.getCode());
            } else if ("UN CODE".equalsIgnoreCase(codeType)) {
                geo.setUnloc(aCode.getCode());
            } else if ("UN CODE(Lookup Only)".equalsIgnoreCase(codeType)) {
                geo.setUnlocLookup(aCode.getCode());
            } else if ("UN CODE(Return Only)".equalsIgnoreCase(codeType)) {
                geo.setUnlocReturn(aCode.getCode());
            } else if ("ISO TERRITORY".equalsIgnoreCase(codeType)) {
                geo.setIsoTerritory(aCode.getCode());
            }
        }

        return geo;
    }

    private static PostalCode buildPostalCode(geography geography) {
        PostalCode postalCode = PostalCode.builder()
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

        // TODO handle multiple parents
        List<parent> parents = geography.getParents();
        if (null != parents && !parents.isEmpty()) {
            Optional<Parent> parentOptional = mapToParent(parents.get(0));
            if (parentOptional.isPresent()) {
                Parent parent = parentOptional.get();
                postalCode.setParentId(parent.getRowId());
                postalCode.setParentName(parent.getName());
                postalCode.setParentType(parent.getType());
            }
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

    private static Optional<Country> mapToCountry(country countryAvro) {
        return Optional.ofNullable(countryAvro)
                       .map(ca -> {
                           List<countryAlternateCode> alternateCodes = ca.getAlternateCodes();
                           String geoID = GeographyMapper.findCodeFromCountryAlternateCodes(alternateCodes, GEO_ID);
                           return Country.builder()
                                         .rowId(geoID)
                                         .name(ca.getName())
                                         .build();
                       });
    }

    private static Optional<Parent> mapToParent(parent parentAvro) {
        return Optional.ofNullable(parentAvro)
                       .map(pa -> {
                           List<parentAlternateCode> alternateCodes = pa.getAlternateCodes();
                           String geoID = GeographyMapper.findCodeFromGeoParentAlternateCodes(alternateCodes, GEO_ID);
                           return Parent.builder()
                                        .rowId(geoID)
                                        .name(pa.getName())
                                        .type(pa.getType())
                                        .build();
                       });
    }

    private static List<SubCityParent> mapToSubCityParents(List<subCityParent> subCityParents) {
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

    private static List<AlternateCode> mapToBDALocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .collect(Collectors.toList());
    }

    private static BusinessDefinedAreaLocation mapToBusinessDefinedLocation(bdaLocation bdaLocation, String geoId) {
        return BusinessDefinedAreaLocation.builder()
                                          .id(geoId)
                                          .name(bdaLocation.getName())
                                          .type(bdaLocation.getType())
                                          .status(bdaLocation.getStatus())
                                          .build();
    }

    private static BusinessDefinedArea mapToBusinessDefinedArea(bda bda, String geoId) {
        return BusinessDefinedArea.builder()
                                  .id(geoId)
                                  .name(bda.getName())
                                  .type(bda.getType())
                                  .bdaType(bda.getBdaType())
                                  .build();
    }

    private static List<AlternateCode> mapToBDAAlternateCodes(List<bdaAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .collect(Collectors.toList());
    }

    private static List<AlternateCode> mapToSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }

    private static List<AlternateCode> mapToParentAlternateCodes(List<parentAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }

    private static List<AlternateCode> mapToCountryAlternateCodes(List<countryAlternateCode> alternateCodes, String geoId) {
        return alternateCodes
                .stream()
                .map(alternateCode ->
                        AlternateCode.builder()
                                     .code(alternateCode.getCode())
                                     .codeType(alternateCode.getCodeType())
                                     .build()
                )
                .toList();
    }
}
