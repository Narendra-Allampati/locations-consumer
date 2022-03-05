package com.maersk.referencedata.locationsconsumer.domains.neo4j;

import lombok.Builder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Builder
@Node("AlternateCode")
public class AlternateCodeEntity {
    @Id
    private final String id;
    private final String code;
    private final String codeType;

//    @Relationship(type = "HAS_AN", direction = Relationship.Direction.INCOMING)
//    private CityEntity city;
//
//    @Relationship(type = "HAS_AN", direction = Relationship.Direction.INCOMING)
//    private List<CountryEntity> countries = new ArrayList<>();
//
//    @Relationship(type = "HAS_AN", direction = Relationship.Direction.INCOMING)
//    private List<CityEntity> continents = new ArrayList<>();
}
