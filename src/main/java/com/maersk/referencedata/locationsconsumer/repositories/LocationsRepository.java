package com.maersk.referencedata.locationsconsumer.repositories;

import com.maersk.referencedata.locationsconsumer.domains.GeographyDoc;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author Anders Clausen on 10/09/2021.
 */
public interface LocationsRepository extends ReactiveCrudRepository<GeographyDoc, String> {
}
