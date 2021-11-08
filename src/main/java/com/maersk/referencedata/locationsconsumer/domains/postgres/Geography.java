package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@Builder
@Table
public class Geography {

    @Id
    private String geoRowId;
    private String geoType;
    private String name;
    private String status;
    private ZonedDateTime validFrom;
    private ZonedDateTime validTo;
    private Double longitude;
    private Double latitude;
    private String timeZone;
    private String daylightSavingTime;
    private String utcOffsetMinutes;
    private String daylightSavingStart;
    private String daylightSavingEnd;
    private String daylightSavingShiftMinutes;
    private String description;
    private String workaroundReason;
    private String restricted;
    private String siteType;
    private String gpsFlag;
    private String gsmFlag;
    private String streetNumber;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postalCode;
    private String postalCodeMandatoryFlag;
    private String stateProvinceMandatory;
    private String dialingCode;
    private String dialingCodeDescription;
    private String portFlag;
    private String olsonTimezone;
    private String bdaType;
    private String countryRowid;
    private String parentRowId;
    private String subCityParentRowId;
    private String bdaRowId;
    private String bdaLocRowId;
//    private String geographyAlternateNames     text     [],
//    private String geographyAlternateCodes      text     [],
//    private String geographyFence                        text     []);
}

