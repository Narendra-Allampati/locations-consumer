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
@Table(value = "TRANSPORT_MODES")
public class TransportMode {

    @Id
    private String id;
    private String modeOfTransport;
    private String transportCode;
    private String transportDescription;
    private LocalDate validThroughDate;
}
