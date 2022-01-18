package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Anders Clausen on 12/10/2021.
 */
@Builder
@Table(value = "GEOGRAPHY")
public class Geography {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String geoRowId;
    private String geoType;
    private String name;
    private String status;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String longitude;
    private String latitude;
    private String timeZone;
    private String daylightSavingTime;
    private String utcOffsetMinutes;
    private LocalDate daylightSavingStart;
    private LocalDate daylightSavingEnd;
    private String daylightSavingShiftMinutes;
    private String description;
    private String workaroundReason;
    private String restricted;
    private String postalCodeMandatoryFlag;
    private String stateProvinceMandatory;
    private String dialingCode;
    private String dialingCodeDescription;
    private boolean portFlag;
    private String olsonTimeZone;
    private String bdaType;
    private String hsudName;
//    private List<AlternateName> alternateNames;
//    private List<AlternateCode> alternateCodes;
//    private List<Country> countries;
//    private List<Parent> parents;
//    private List<SubCityParent> subCityParents;
//    private List<BusinessDefinedArea> businessDefinedAreas;
//    private List<BusinessDefinedAreaLocation> businessDefinedAreaLocations;
}

