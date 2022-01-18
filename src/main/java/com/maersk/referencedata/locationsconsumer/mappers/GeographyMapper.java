package com.maersk.referencedata.locationsconsumer.mappers;

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

    public static Geography mapAvroToDomainModel(com.maersk.geography.smds.operations.msk.geographyMessage geographyMessage) {
        final var geography = geographyMessage.getGeography();
        return Geography.builder().geoRowId(geography.getGeoId())
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
                .dialingCodeDescription(geography.getDescription())
                .portFlag(geography.getPortFlag())
                .olsonTimeZone(geography.getOlsonTimezone())
                .bdaType(geography.getBdaType())
                .build();
    }
}
