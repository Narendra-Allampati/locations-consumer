package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityAlternateCode;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FacilityAlternateCodesRepository extends R2dbcRepository<FacilityAlternateCode, Long> {
}
