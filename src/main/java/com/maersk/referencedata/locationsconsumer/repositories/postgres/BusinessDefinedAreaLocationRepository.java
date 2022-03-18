package com.maersk.referencedata.locationsconsumer.repositories.postgres;

import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedAreaLocation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface BusinessDefinedAreaLocationRepository extends R2dbcRepository<BusinessDefinedAreaLocation, Long> {
}
