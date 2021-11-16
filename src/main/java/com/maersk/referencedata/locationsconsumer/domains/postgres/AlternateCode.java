package com.maersk.referencedata.locationsconsumer.domains.postgres;

/**
 * @author Anders Clausen on 15/11/2021.
 */

import lombok.Builder;

@Builder
public class AlternateCode {

    private String codeType;
    private String code;
}
