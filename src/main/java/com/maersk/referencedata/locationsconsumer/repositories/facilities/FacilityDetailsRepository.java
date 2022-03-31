package com.maersk.referencedata.locationsconsumer.repositories.facilities;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityDetail;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 20/11/2021.
 */
public interface FacilityDetailsRepository  extends R2dbcRepository<FacilityDetail, Long> {
}
