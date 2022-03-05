package com.maersk.referencedata.locationsconsumer.domains.neo4j;

import lombok.Builder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Builder
@Node("CitySubArea")
public class CitySubAreaEntity {
    @Id
    private final String geoId;
    private final String name;
}
