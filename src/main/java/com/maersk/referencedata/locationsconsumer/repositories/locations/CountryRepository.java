package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.Country;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CountryRepository extends R2dbcRepository<Country, Long> {
}
