package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@AllArgsConstructor
@Builder
@Getter
@Table(value = "FACILITY_DETAILS")
public class FacilityDetail implements Persistable<String> {

    @Id
    private String id;
    private String facilityId;
    private String weightLimitCraneKG;
    private String weightLimitYardKG;
    private String vesselAgent;
    private String gpsFlag;
    private String gsmFlag;
    private String oceanFreightPricing;
    private String brand;
    private String commFacilityType;
    private String exportEnquiriesEmail;
    private String importEnquiriesEmail;
    private String facilityFunction;
    private String facilityFunctionDescription;
    private String internationalDialCode;
    private String telephoneNumber;

    @PersistenceCreator
    public FacilityDetail() {
    }

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNew || this.id == null;
    }
}
