package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.geography.smds.operations.msk.alternateCode;
import com.maersk.geography.smds.operations.msk.bdaAlternateCode;
import com.maersk.geography.smds.operations.msk.bdaLocationAlternateCode;
import com.maersk.geography.smds.operations.msk.countryAlternateCode;
import com.maersk.geography.smds.operations.msk.parentAlternateCode;
import com.maersk.geography.smds.operations.msk.subCityParentAlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@UtilityClass
public class GeographyMapper {

    public String findCodeFromCountryAlternateCodes(List<countryAlternateCode> alternateCodes, String type) {
        // TODO Do I really want to set a random UUID?
        return alternateCodes.stream()
                .filter(value -> type.equals(value.getCodeType()))
                .findFirst()
                .map(countryAlternateCode::getCode)
                .orElse(UUID.randomUUID().toString());
    }

    public String findCodeFromParentAlternateCodes(List<parentAlternateCode> alternateCodes, String type) {
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
}
