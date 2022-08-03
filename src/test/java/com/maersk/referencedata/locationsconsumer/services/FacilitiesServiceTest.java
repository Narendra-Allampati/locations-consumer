package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityType;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
class FacilitiesServiceTest {
    FacilitiesService facilitiesService;

    @BeforeEach
    public void setUp() {
        this.facilitiesService = new FacilitiesService(null, null, null, null, null, null, null, null, null, null, null, null);
    }

//    @Test
//    public void findMin() {
//        FacilityType fac1 = FacilityType.builder()
//                                                 .facilityId("YWO2JOW4LZHAB")
//                                                 .code("FCT_TYPE.RAILHEAD")
//                                                 .build();
//
//        String facilityTypeWithHighestPrecedence = facilitiesService.getFacilityTypeWithHighestPrecedence(List.of(fac1));
//    }

}