package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityTypeLink;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FacilityTypeLinksRepository extends R2dbcRepository<FacilityTypeLink, Long> {
}
