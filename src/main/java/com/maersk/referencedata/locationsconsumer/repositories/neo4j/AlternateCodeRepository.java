package com.maersk.referencedata.locationsconsumer.repositories.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.AlternateCodeEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface AlternateCodeRepository extends ReactiveNeo4jRepository<AlternateCodeEntity, String> {
}
