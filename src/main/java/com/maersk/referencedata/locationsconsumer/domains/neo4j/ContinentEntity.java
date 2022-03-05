package com.maersk.referencedata.locationsconsumer.domains.neo4j;

import lombok.Builder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Builder
@Node("Continent")
public class ContinentEntity {
    @Id
    private final String geoId;

    private final String name;

//    @Relationship(type = "RESIDES_IN", direction = Relationship.Direction.INCOMING)
//    private List<CountryEntity> cities = new ArrayList<>();

    @Relationship(type = "HAS_AN")
    private List<AlternateCodeEntity> alternateCodeEntities;
}
