package com.maersk.referencedata.locationsconsumer.domains;

import com.maersk.Geography.smds.operations.MSK.GeographyMessage;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author Anders Clausen on 10/09/2021.
 */
@Builder
@Document(indexName = "geography1")
public class GeographyDoc {
    String operationType;
    @Id
    String geoRowId;
    String geoType;
    String status;
    String validFrom;
    String validTo;
    GeographyMessage payload;
}
