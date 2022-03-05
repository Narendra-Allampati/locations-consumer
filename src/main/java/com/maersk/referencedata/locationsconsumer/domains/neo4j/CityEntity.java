package com.maersk.referencedata.locationsconsumer.domains.neo4j;

import lombok.Builder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Builder
@Node("City")
public class CityEntity {
    @Id
    private final String geoId;
    private final String name;
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

    @Relationship(type = "RESIDES_IN")
    private CountryEntity country;

    @Relationship(type = "HAS_AN")
    private List<AlternateCodeEntity> alternateCodeEntities;
}
