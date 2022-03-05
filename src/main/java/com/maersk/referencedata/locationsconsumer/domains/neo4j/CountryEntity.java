package com.maersk.referencedata.locationsconsumer.domains.neo4j;

import com.maersk.referencedata.locationsconsumer.domains.postgres.AlternateCodePostgres;
import lombok.Builder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Builder
@Node("Country")
public class CountryEntity {

    @Id
    private final String geoId;

    private final String name;

    private List<AlternateCodePostgres> alternateCodes;

//    @Relationship(type = "RESIDES_IN", direction = Relationship.Direction.INCOMING)
//    private List<CityEntity> cities = new ArrayList<>();

    @Relationship(type = "RESIDES_IN")
    private ContinentEntity continent;

    @Relationship(type = "HAS_AN")
    private List<AlternateCodeEntity> alternateCodeEntities;
}
