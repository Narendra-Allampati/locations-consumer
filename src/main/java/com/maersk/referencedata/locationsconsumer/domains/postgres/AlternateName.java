package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
public class AlternateName {

    private String name;
    private String description;
    private String status;
}
