package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateName;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 17/11/2021.
 */
public interface AlternateNameRepository extends R2dbcRepository<AlternateName, Long> {
}
