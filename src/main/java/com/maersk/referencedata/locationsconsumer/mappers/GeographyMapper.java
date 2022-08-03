package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.geography.smds.operations.msk.alternateCode;
import com.maersk.geography.smds.operations.msk.alternateName;
import com.maersk.geography.smds.operations.msk.country;
import com.maersk.geography.smds.operations.msk.countryAlternateCode;
import com.maersk.geography.smds.operations.msk.geography;
import com.maersk.geography.smds.operations.msk.parent;
import com.maersk.geography.smds.operations.msk.parentAlternateCode;
import com.maersk.geography.smds.operations.msk.subCityParent;
import com.maersk.geography.smds.operations.msk.subCityParentAlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateName;
import com.maersk.referencedata.locationsconsumer.domains.locations.Country;
import com.maersk.referencedata.locationsconsumer.domains.locations.Geography;
import com.maersk.referencedata.locationsconsumer.domains.locations.Parent;
import com.maersk.referencedata.locationsconsumer.domains.locations.SubCityParent;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.maersk.referencedata.locationsconsumer.constants.LocationsConstants.POSTAL_CODE;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@UtilityClass
public class GeographyMapper {

    private static final String GEO_ID = "GEOID";

    public static String findGeoIdFromFacilityParentAlternateCodes(List<com.maersk.facility.smds.operations.msk.parentAlternateCode> alternateCodes) {
        return alternateCodes.stream()
                             .filter(value -> GEO_ID.equals(value.getCodeType()))
                             .findFirst()
                             .map(com.maersk.facility.smds.operations.msk.parentAlternateCode::getCode)
                             .orElseThrow();
    }

    public static List<AlternateCode> mapToAlternateCodes(List<alternateCode> alternateCodes, String geoId) {

        return alternateCodes.stream()
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

    private String findCodeFromCountryAlternateCodes(List<countryAlternateCode> alternateCodes) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> GEO_ID.equals(value.getCodeType()))
                             .findFirst()
                             .map(countryAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    private String findCodeFromGeoParentAlternateCodes(List<parentAlternateCode> alternateCodes) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> GEO_ID.equals(value.getCodeType()))
                             .findFirst()
                             .map(parentAlternateCode::getCode)
                             .orElse(UUID.randomUUID()
                                         .toString());
    }

    private static String findCodeFromSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                             .filter(value -> GEO_ID.equals(value.getCodeType()))
                             .findFirst()
                             .map(subCityParentAlternateCode::getCode)
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

    private static Optional<Country> mapToCountry(country countryAvro) {
        return Optional.ofNullable(countryAvro)
                       .map(ca -> {
                           List<countryAlternateCode> alternateCodes = ca.getAlternateCodes();
                           String geoID = GeographyMapper.findCodeFromCountryAlternateCodes(alternateCodes);
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
                           String geoID = GeographyMapper.findCodeFromGeoParentAlternateCodes(alternateCodes);
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
                           String geoID = GeographyMapper.findCodeFromSubCityParentAlternateCodes(alternateCodes);
                           return SubCityParent.builder()
                                               .rowId(geoID)
                                               .name(scp.getName())
                                               .type(scp.getType())
                                               .build();
                       })
                       .toList();
    }
}
