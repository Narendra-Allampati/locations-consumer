package com.maersk.referencedata.locationsconsumer.repositories;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.ContinentEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface ContinentRepository extends ReactiveNeo4jRepository<ContinentEntity, String> {
}
