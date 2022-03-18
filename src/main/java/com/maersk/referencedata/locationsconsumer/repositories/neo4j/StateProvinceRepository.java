package com.maersk.referencedata.locationsconsumer.repositories.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.StateProvinceEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface StateProvinceRepository extends ReactiveNeo4jRepository<StateProvinceEntity, String> {
}
