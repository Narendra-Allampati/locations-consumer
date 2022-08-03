package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityTypeMapping;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FacilityTypesMappingsRepository extends R2dbcRepository<FacilityTypeMapping, String> {
}
