package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.TransportMode;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 20/11/2021.
 */
public interface TransportModesRepository extends R2dbcRepository<TransportMode, Long> {
}
