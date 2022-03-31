package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityAlternateCodeLink;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface FacilityAlternateCodeLinksRepository  extends R2dbcRepository<FacilityAlternateCodeLink, Long> {
}
