package com.maersk.referencedata.locationsconsumer.domains.locations;

import lombok.Builder;
import org.springframework.data.annotation.Id;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
public class BusinessDefinedAreaLocation {

    @Id
    private String id;
    private String name;
    private String type;
    private String status;
}
