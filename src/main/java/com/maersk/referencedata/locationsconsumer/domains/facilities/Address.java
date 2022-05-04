package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 18/11/2021.
 */
@AllArgsConstructor
@Builder
@Table(value = "ADDRESSES")
public class Address implements Persistable<String> {

    @Id
    private String id;
    private String facilityId;
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

    @PersistenceConstructor
    public Address() {
    }

    @Transient
    private boolean isNew;

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
