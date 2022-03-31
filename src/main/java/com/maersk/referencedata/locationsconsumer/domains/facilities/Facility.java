package com.maersk.referencedata.locationsconsumer.domains.facilities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 18/11/2021.
 */
@Builder
@Table(value = "FACILITIES")
public class Facility {

    @Id
    private String id;
    private String name;
    private String type;
    private boolean extOwned;
    private String status;
    private boolean extExposed;
    private String url;
    private String doDAAC;
}
