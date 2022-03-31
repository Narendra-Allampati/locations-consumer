package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.SubCityParent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SubCityParentRepository extends R2dbcRepository<SubCityParent, Long> {
}
