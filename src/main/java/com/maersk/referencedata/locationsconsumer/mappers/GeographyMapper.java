package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.Geography.smds.operations.MSK.alternateCodes;
import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

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
                .longitude(Double.parseDouble(geography.getLongitude()))
                .latitude(Double.parseDouble(geography.getLatitude()))
                .timeZone(geography.getTimeZone())
                .daylightSavingTime(geography.getDaylightSavingTime())
                .utcOffsetMinutes(geography.getUtcOffsetMinutes())
                .daylightSavingStart(geography.getDaylightSavingStart())
                .daylightSavingEnd(geography.getDaylightSavingEnd())
                .daylightSavingShiftMinutes(geography.getDaylightSavingShiftMinutes())
                .description(geography.getDescription())
                .workaroundReason(geography.getWorkaroundReason())
                .restricted(geography.getRestricted())
//                .siteType(geography.getSiteType())
//                .gpsFlag(geography.getGPSFlag())
//                .gsmFlag(geography.getGSMFlag())
//                .streetNumber(geography.getStreetNumber())
//                .addressLine1(geography.getAddressLine1())
//                .addressLine2(geography.getAddressLine2())
//                .addressLine3(geography.getAddressLine3())
//                .postalCode(geography.getPostalCode())
                .postalCodeMandatoryFlag(geography.getPostalCodeMandatoryFlag())
                .stateProvinceMandatory(geography.getStateProvienceMandatory())
                .dialingCode(geography.getDialingCode())
                .dialingCodeDescription(geography.getDescription())
                .portFlag(geography.getPortFlag())
                .olsonTimezone(geography.getOlsonTimezone())
                .bdaType(geography.getBdaType())
                .countryRowid(geography.getCountry())
                // TODO We're getting a list for the elements below!!!
                .parentRowId(geography.getParent().get(0).getAlternateCodes())
                .subCityParentRowId(geography.getSubCityParent().get(0).getAlternateCodes())
                .bdaRowId(geography.getBda().get(0).getAlternateCodes())
                .bdaLocRowId(geography.getBdaLocations().get(0).getAlternateCodes())
                .build();

/*      geoType
        name
        status
        validFrom
        validTo
        longitude
        latitude
        timeZone
        daylightSavingTime
        utcOffsetMinutes
        daylightSavingStart
        daylightSavingEnd
        daylightSavingShiftMinutes
        description
        workaroundReason
        restricted
        postalCodeMandatoryFlag
        stateProvidenceMandatory
        dialingCode
        dialingCodeDescription
        portFlag
        olsonTimeZone
        bdaType
        hsudName
        alternateNames
        alternateCodes
        country
        parent
        subCityParent
        bda
        bdaLocations
                 */
    }

    private static String findCode(List<alternateCodes> alternateCodes, String type) {
        return alternateCodes.stream().filter(value -> type.equals(value.getCodeType())
        ).findFirst().map(alternateCodes::getCode).toString();
    }
}
