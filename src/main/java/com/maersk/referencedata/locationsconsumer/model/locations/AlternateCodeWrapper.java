package com.maersk.referencedata.locationsconsumer.model.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.GeoAlternateCodeLink;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlternateCodeWrapper {
    private AlternateCode alternateCodes;
    private GeoAlternateCodeLink alternateCodesLinks;
}
