package com.maersk.referencedata.locationsconsumer.model;

import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.SubCityParent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class SubCityParentWithAlternateCodes {
    private final SubCityParent subCityParent;
    private final List<AlternateCode> alternateCodes;
}
