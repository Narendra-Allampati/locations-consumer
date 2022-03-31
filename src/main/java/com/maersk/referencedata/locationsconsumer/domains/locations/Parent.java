package com.maersk.referencedata.locationsconsumer.domains.locations;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @author Anders Clausen on 15/11/2021.
 */
@Builder
@Data
public class Parent {

    private String rowId;
    private String name;
    private String type;
    private String bdaType;
}
