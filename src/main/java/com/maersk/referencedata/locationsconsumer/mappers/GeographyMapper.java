package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.geography.smds.operations.msk.bdaAlternateCode;
import com.maersk.geography.smds.operations.msk.bdaLocationAlternateCode;
import com.maersk.geography.smds.operations.msk.countryAlternateCode;
import com.maersk.geography.smds.operations.msk.parentAlternateCode;
import com.maersk.geography.smds.operations.msk.subCityParentAlternateCode;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.UUID;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@UtilityClass
public class GeographyMapper {

    private static final String GEO_ID = "GEOID";

    public String findCodeFromCountryAlternateCodes(List<countryAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(countryAlternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    public String findCodeFromGeoParentAlternateCodes(List<parentAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(parentAlternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    public static String findCodeFromSubCityParentAlternateCodes(List<subCityParentAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(subCityParentAlternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    public static String findCodeFromBdaLocationAlternateCodes(List<bdaLocationAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(bdaLocationAlternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    public static String findCodeFromBdaAlternateCodes(List<bdaAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(bdaAlternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    public static String findGeoIdFromFacilityParentAlternateCodes(List<com.maersk.facility.smds.operations.msk.parentAlternateCode> alternateCodes) {
        return alternateCodes.stream()
                .filter(value -> GEO_ID.equals(value.getCodeType()))
                .findFirst()
                .map(com.maersk.facility.smds.operations.msk.parentAlternateCode::getCode)
                .orElseThrow();
    }
}
