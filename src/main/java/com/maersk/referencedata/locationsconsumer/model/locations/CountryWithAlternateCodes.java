package com.maersk.referencedata.locationsconsumer.model.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.Country;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class CountryWithAlternateCodes {
        private final Country country;
        private final List<AlternateCode> alternateCodes;
}
