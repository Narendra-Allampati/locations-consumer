package com.maersk.referencedata.locationsconsumer.model;

import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Parent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class ParentWithAlternateCodes {
    private final Parent parent;
    private final List<AlternateCode> alternateCodes;
}
