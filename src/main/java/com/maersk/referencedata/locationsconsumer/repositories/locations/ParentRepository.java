package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.Parent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ParentRepository extends R2dbcRepository<Parent, Long> {
}
