package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
@Table(value = "ALTERNATE_NAMES")
public class AlternateName {

    @Id
    private UUID id;
    private String rowId;
    private String name;
    private String description;
    private String status;
}
