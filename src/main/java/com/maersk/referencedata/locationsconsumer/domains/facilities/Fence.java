package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 20/11/2021.
 */
@Builder
@Table(value = "FENCES")
public class Fence {

    @Id
    private String id;
    private String name;
    private String fenceType;
}
