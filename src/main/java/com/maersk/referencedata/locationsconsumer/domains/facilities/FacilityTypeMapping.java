package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@AllArgsConstructor
@Data
@Table(value = "FACILITY_TYPES_MAPPINGS")
public class FacilityTypeMapping {

    @Id
    private String id;
    private Integer rank;
    private String code;
    private String siteType;
}

