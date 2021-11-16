package com.maersk.referencedata.locationsconsumer.domains.elasticsearch;

import com.maersk.Geography.smds.operations.MSK.geographyMessage;
import lombok.Builder;
import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Builder
//@Document(indexName = "geography1", createIndex = false)
public class GeographyDoc {
    String operationType;
    @Id
    String geoRowId;
    String geoType;
    String status;
    String validFrom;
    String validTo;
    geographyMessage payload;
}
