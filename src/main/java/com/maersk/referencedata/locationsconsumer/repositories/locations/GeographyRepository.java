package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.Geography;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 12/10/2021.
 */
public interface GeographyRepository extends R2dbcRepository<Geography, String> {
}
