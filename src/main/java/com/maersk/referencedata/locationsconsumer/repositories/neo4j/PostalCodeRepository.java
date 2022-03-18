package com.maersk.referencedata.locationsconsumer.repositories.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.PostalCodeEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface PostalCodeRepository extends ReactiveNeo4jRepository<PostalCodeEntity, String> {
}
