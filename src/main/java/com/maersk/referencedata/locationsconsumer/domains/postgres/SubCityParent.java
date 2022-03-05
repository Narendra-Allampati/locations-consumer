package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;

import java.util.List;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
public class SubCityParent {

    private String name;
    private String type;
    private String bdaType;
    private List<AlternateCodePostgres> alternateCodes;
}
