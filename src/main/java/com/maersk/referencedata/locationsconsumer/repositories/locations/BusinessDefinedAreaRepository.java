package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.BusinessDefinedArea;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface BusinessDefinedAreaRepository extends R2dbcRepository<BusinessDefinedArea, Long> {
}
