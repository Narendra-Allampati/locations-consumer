package com.maersk.referencedata.locationsconsumer.startup;

import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityTypeMapping;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityTypesMappingsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FacilityTypesMappingsLoad {

    private final Map<String, Integer> facilityTypeMappingsRankings = new HashMap<>();
    private final Map<Integer, String> facilityTypeMappingsSiteTypeFromRanking = new HashMap<>();

    private final FacilityTypesMappingsRepository facilityTypesMappingsRepository;

    public FacilityTypesMappingsLoad(FacilityTypesMappingsRepository facilityTypesMappingsRepository) {
        this.facilityTypesMappingsRepository = facilityTypesMappingsRepository;
    }

    @PostConstruct
    public void init() {
        List<FacilityTypeMapping> results = facilityTypesMappingsRepository.findAll()
                                                                           .collectList()
                                                                           .block();

        for (FacilityTypeMapping ftm : results) {
            facilityTypeMappingsRankings.put(ftm.getCode(), ftm.getRank());
            facilityTypeMappingsSiteTypeFromRanking.put(ftm.getRank(), ftm.getSiteType());
        }

//        results.stream()
//               .map(ftm -> {
//                   facilityTypeMappingsRankings.put(ftm.getCode(), ftm.getRank());
//                   facilityTypeMappingsSiteTypeFromRanking.put(ftm.getRank(), ftm.getSiteType());
//                   return ftm;
//               });

//        facilityTypesMappingsRepository.findAll()
//                                       .map(ftm -> {
//                                           facilityTypeMappingsRankings.put(ftm.getCode(), ftm.getRank());
//                                           facilityTypeMappingsSiteTypeFromRanking.put(ftm.getRank(), ftm.getSiteType());
//                                           return ftm;
//                                       })
//                                       .subscribe();

        log.info("Finished loading Facility Types Mappings. " + facilityTypeMappingsRankings.size() + " rankings and " + facilityTypeMappingsSiteTypeFromRanking.size() + " site type from ranking.");
    }

    public Integer getRankingFromCode(String code) {
        return facilityTypeMappingsRankings.get(code);
    }

    public String getSiteTypeFromRanking(Integer ranking) {
        return facilityTypeMappingsSiteTypeFromRanking.get(ranking);
    }
}
