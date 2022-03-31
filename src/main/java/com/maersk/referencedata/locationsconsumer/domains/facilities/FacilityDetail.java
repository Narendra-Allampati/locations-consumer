package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@Builder
@Table(value = "FACILITY_DETAILS")
public class FacilityDetail {

    @Id
    private String id;
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
    private String facilityFunctionDesc;
    private String internationalDialCode;
    private String telephoneNumber;
}
