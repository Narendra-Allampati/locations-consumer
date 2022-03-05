package com.maersk.referencedata.locationsconsumer.repositories;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.CityEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface CitiesRepository extends ReactiveNeo4jRepository<CityEntity, String> {
}
