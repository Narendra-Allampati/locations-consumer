package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.Facility;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 10/09/2021.
 */
public interface FacilitiesRepository extends R2dbcRepository<Facility, Long> {
}
