package com.maersk.referencedata.locationsconsumer.repositories.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.neo4j.CountryEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

/**
 * @author Anders Clausen on 10/09/2021.
 */
public interface CountriesRepository extends ReactiveNeo4jRepository<CountryEntity, String> {

}
