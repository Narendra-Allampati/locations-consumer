package com.maersk.referencedata.locationsconsumer.repositories.postgres;

import com.maersk.referencedata.locationsconsumer.domains.postgres.Parent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ParentRepository extends R2dbcRepository<Parent, Long> {
}
