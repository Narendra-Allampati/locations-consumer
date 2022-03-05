package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;

import java.util.List;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
public class BusinessDefinedAreaLocation {
    private String name;
    private String type;
    private String status;
    private List<AlternateCodePostgres> alternateCodes;

}
