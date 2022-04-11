package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.locations.Parent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 18/11/2021.
 */
public interface ParentsRepository extends R2dbcRepository<Parent, String> {
}
