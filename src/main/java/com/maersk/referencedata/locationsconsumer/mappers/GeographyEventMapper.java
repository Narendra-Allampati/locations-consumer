package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.Geography.smds.operations.MSK.country;
import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Country;
import com.maersk.referencedata.locationsconsumer.domains.postgres.Geography;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Anders Clausen on 15/11/2021.
 */
//@Mapper
public interface GeographyEventMapper {

    GeographyEventMapper INSTANCE = Mappers.getMapper(GeographyEventMapper.class);

    @Mappings({
            @Mapping(source="event.geography.country", target="countries"),
            @Mapping(source="event.geography.parent", target="parents"),
            @Mapping(source="event.geography.subCityParent", target="subCityParents"),
            @Mapping(source="event.geography.bda", target="businessDefinedAreas"),
//            @Mapping(source="event.geography.bda.alternateCode", target="businessDefinedAreas.alternateCodes"),
            @Mapping(source="event.geography.bdaLocations", target="businessDefinedAreaLocations"),
//            @Mapping(source="event.geography.bdaLocations[].alternateCodes", target="businessDefinedAreaLocations.alternateCodes")
    })
    Geography geographyEventToGeography(geographyMessage event);


    Country mapCountry(country c);
}
