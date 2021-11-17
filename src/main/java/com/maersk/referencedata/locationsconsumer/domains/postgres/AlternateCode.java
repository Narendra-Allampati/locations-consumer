package com.maersk.referencedata.locationsconsumer.domains.postgres;

/**
 * @author Anders Clausen on 15/11/2021.
 */

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Table(value = "ALTERNATE_CODES")
public class AlternateCode {

    @Id
    private UUID id;
    private String rowId;
    private String codeType;
    private String code;
}
