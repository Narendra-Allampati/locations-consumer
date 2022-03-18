package com.maersk.referencedata.locationsconsumer.repositories.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.CitySubAreaEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface CitySubAreaRepository extends ReactiveNeo4jRepository<CitySubAreaEntity, String> {
}
