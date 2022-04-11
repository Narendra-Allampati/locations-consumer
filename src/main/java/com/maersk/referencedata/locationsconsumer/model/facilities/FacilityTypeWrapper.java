package com.maersk.referencedata.locationsconsumer.model.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityType;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityTypeLink;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FacilityTypeWrapper {

    private final FacilityType facilityType;
    private final FacilityTypeLink facilityTypeLink;
}
