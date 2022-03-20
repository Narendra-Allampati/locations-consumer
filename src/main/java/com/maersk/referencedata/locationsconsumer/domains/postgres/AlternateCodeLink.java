package com.maersk.referencedata.locationsconsumer.domains.postgres;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table(value = "ALTERNATE_CODE_LINKS")
public class AlternateCodeLink {
    private String geoId;
    private String alternateCodeId;
}
