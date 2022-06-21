package com.maersk.referencedata.locationsconsumer.domains.locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Data
@Table(value = "POSTAL_CODE")
public class PostalCode implements Persistable<String> {

    @Id
    private String geoId;
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
    private String postalCodeMandatory;
    private String stateProvinceMandatory;
    private String dialingCode;
    private String dialingCodeDescription;
    private boolean portFlag;
    private String olsonTimeZone;
    private String bdaType;
    private String hsudName;
    private String countryId;
    private String countryName;
    private String parentId;
    private String parentName;
    private String parentType;
    private String subCityParentId;
    private String subCityParentName;
    private String subCityParentType;

    @PersistenceCreator
    public PostalCode() {
    }

    @Transient
    private boolean isNew;

    @Override
    public String getId() {
        return this.geoId;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNew || this.geoId == null;
    }
}
