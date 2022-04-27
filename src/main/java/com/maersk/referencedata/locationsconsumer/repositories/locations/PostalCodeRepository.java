package com.maersk.referencedata.locationsconsumer.repositories.locations;

import com.maersk.referencedata.locationsconsumer.domains.locations.PostalCode;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface PostalCodeRepository extends R2dbcRepository<PostalCode, String> {
}