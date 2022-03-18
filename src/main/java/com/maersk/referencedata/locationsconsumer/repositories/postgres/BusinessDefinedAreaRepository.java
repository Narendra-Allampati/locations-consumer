package com.maersk.referencedata.locationsconsumer.repositories.postgres;

import com.maersk.referencedata.locationsconsumer.domains.postgres.BusinessDefinedArea;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface BusinessDefinedAreaRepository extends R2dbcRepository<BusinessDefinedArea, Long> {
}
