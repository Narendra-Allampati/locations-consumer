package com.maersk.referencedata.locationsconsumer.repositories.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.BusinessDefinedAreaEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface BusinessDefinedAreasRepository extends ReactiveNeo4jRepository<BusinessDefinedAreaEntity, String> {
}
