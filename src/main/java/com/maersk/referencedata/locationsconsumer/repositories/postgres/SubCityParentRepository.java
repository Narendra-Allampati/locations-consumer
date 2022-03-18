package com.maersk.referencedata.locationsconsumer.repositories.postgres;

import com.maersk.referencedata.locationsconsumer.domains.postgres.SubCityParent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SubCityParentRepository extends R2dbcRepository<SubCityParent, Long> {
}
