package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 18/11/2021.
 */
@Builder
@Table(value = "ADDRESSES")
public class Address {

    @Id
    private String id;
    private String houseNumber;
    private String street;
    private String city;
    private String postalCode;
    private String poBox;
    private String district;
    private String territory;
    private String countryName;
    private String countryCode;
    private String addressLine2;
    private String addressLine3;
    private String latitude;
    private String longitude;
    private String addressQualityCheckIndicator;
}
