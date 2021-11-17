package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.Geography.smds.operations.MSK.alternateCodes;
import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@UtilityClass
public class GeographyMapper {
    private static final String TIME_ZONE_UTC = "UTC";

    public static Geography mapAvroToDomainModel(geographyMessage geographyMessage) {
        final var geography = geographyMessage.getGeography();
        return Geography.builder().geoRowId(findCode(geography.getAlternateCodes(), "GEOID"))
                .geoType(geography.getGeoType())
                .name(geography.getName())
                .status(geography.getStatus())
                .validFrom(ZonedDateTime.ofInstant(Instant.ofEpochMilli(geography.getValidFrom()), ZoneId.of(TIME_ZONE_UTC)))
                .validTo(ZonedDateTime.ofInstant(Instant.ofEpochMilli(geography.getValidTo()), ZoneId.of(TIME_ZONE_UTC)))
                .longitude(geography.getLongitude())
                .latitude(geography.getLatitude())
                .timeZone(geography.getTimeZone())
                .daylightSavingTime(geography.getDaylightSavingTime())
                .utcOffsetMinutes(geography.getUtcOffsetMinutes())
                .daylightSavingStart(ZonedDateTime.ofInstant(Instant.ofEpochMilli(geography.getDaylightSavingStart()), ZoneId.of(TIME_ZONE_UTC)))
                .daylightSavingEnd(ZonedDateTime.ofInstant(Instant.ofEpochMilli(geography.getDaylightSavingEnd()), ZoneId.of(TIME_ZONE_UTC)))
                .daylightSavingShiftMinutes(geography.getDaylightSavingShiftMinutes())
                .description(geography.getDescription())
                .workaroundReason(geography.getWorkaroundReason())
                .restricted(geography.getRestricted())

                .postalCodeMandatoryFlag(geography.getPostalCodeMandatoryFlag())
                .stateProvinceMandatory(geography.getStateProvienceMandatory())
                .dialingCode(geography.getDialingCode())
                .dialingCodeDescription(geography.getDescription())
                .portFlag(geography.getPortFlag())
                .olsonTimeZone(geography.getOlsonTimezone())
                .bdaType(geography.getBdaType())
                .build();
    }

    private static String findCode(List<alternateCodes> alternateCodes, String type) {
        final var alternateCode = alternateCodes.stream().filter(value -> type.equals(value.getCodeType())
        ).findFirst().orElse(null);

        return (null == alternateCode) ? null : alternateCode.getCode();
    }
}
