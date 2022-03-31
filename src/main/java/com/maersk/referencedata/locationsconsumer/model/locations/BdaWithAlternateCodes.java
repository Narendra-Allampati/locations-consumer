package com.maersk.referencedata.locationsconsumer.model.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.BusinessDefinedArea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class BdaWithAlternateCodes {
    private final BusinessDefinedArea businessDefinedArea;
    private final List<AlternateCode> alternateCodes;
}
