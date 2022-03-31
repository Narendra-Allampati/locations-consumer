package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@Builder
@Table(value = "FACILITY_TYPES")
public class FacilityType {

    @Id
    private UUID id;
    private String code;
    private String name;
    private String masterType;
    private long validThroughDate;
}
