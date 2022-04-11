package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * @author Anders Clausen on 17/11/2021.
 */
public interface AlternateCodeRepository extends R2dbcRepository<AlternateCode, String> {
}
