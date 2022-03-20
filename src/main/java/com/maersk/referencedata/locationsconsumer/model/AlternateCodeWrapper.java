package com.maersk.referencedata.locationsconsumer.model;

import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCodeLink;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlternateCodeWrapper {
    private AlternateCode alternateCodes;
    private AlternateCodeLink alternateCodesLinks;
}
