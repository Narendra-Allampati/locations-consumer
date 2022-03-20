package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Anders Clausen on 15/11/2021.
 */

@Builder
@Table(value = "ALTERNATE_CODES")
public class AlternateCode {

    @Id
    private String code;
    private String codeType;
}
