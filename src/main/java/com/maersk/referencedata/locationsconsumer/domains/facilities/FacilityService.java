package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@Builder
@Table(value = "FACILITY_SERVICES")
public class FacilityService {

    @Id
    private String id;
    private String serviceName;
    private String serviceCode;
    private String serviceDescription;
    private LocalDate validThroughDate;
}
