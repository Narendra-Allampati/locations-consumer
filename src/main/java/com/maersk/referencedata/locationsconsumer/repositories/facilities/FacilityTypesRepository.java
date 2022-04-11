package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FacilityTypesRepository extends R2dbcRepository<FacilityType, String> {
}
