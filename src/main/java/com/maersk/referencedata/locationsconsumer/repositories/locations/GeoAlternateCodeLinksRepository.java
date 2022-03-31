package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.GeoAlternateCodeLink;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface GeoAlternateCodeLinksRepository extends R2dbcRepository<GeoAlternateCodeLink, Long> {
}
