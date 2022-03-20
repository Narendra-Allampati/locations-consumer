package com.maersk.referencedata.locationsconsumer.repositories.postgres;

import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCodeLink;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface AlternateCodeLinksRepository extends R2dbcRepository<AlternateCodeLink, Long> {
}
