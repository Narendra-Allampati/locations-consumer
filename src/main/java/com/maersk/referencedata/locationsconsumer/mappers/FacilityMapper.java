package com.maersk.referencedata.locationsconsumer.mappers;

import com.maersk.facility.smds.operations.msk.address;
import com.maersk.facility.smds.operations.msk.alternateCode;
import com.maersk.facility.smds.operations.msk.contactDetail;
import com.maersk.facility.smds.operations.msk.facility;
import com.maersk.facility.smds.operations.msk.facilityDetail;
import com.maersk.facility.smds.operations.msk.facilityService;
import com.maersk.facility.smds.operations.msk.fence;
import com.maersk.facility.smds.operations.msk.openingHour;
import com.maersk.facility.smds.operations.msk.parent;
import com.maersk.facility.smds.operations.msk.transportMode;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Address;
import com.maersk.referencedata.locationsconsumer.domains.facilities.ContactDetail;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Facility;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityAlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityDetail;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityService;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityType;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityTypeLink;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Fence;
import com.maersk.referencedata.locationsconsumer.domains.facilities.OpeningHour;
import com.maersk.referencedata.locationsconsumer.domains.facilities.TransportMode;
import com.maersk.referencedata.locationsconsumer.domains.locations.Parent;
import com.maersk.referencedata.locationsconsumer.model.facilities.FacilityTypeWrapper;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper.findGeoIdFromFacilityParentAlternateCodes;

@UtilityClass
public class FacilityMapper {

    public static Facility mapToFacility(facility facilityEvent) {
        Facility facility = Facility.builder()
                                    .id(facilityEvent.getFacilityId())
                                    .name(facilityEvent.getName())
                                    .type(facilityEvent.getType())
                                    .extOwned(facilityEvent.getExtOwned())
                                    .status(facilityEvent.getStatus())
                                    .extExposed(facilityEvent.getExtExposed())
                                    .url(facilityEvent.getUrl())
                                    .departmentOfDefenceActivityAddressCode(facilityEvent.getDoDAAC())
                                    .parentId(findGeoIdFromFacilityParentAlternateCodes(facilityEvent.getParent()
                                                                                                     .getAlternateCodes()))
                                    .parentName(facilityEvent.getParent()
                                                             .getName())
                                    .parentType(facilityEvent.getParent()
                                                             .getType())
                                    .build();

        List<alternateCode> alternateCodes = facilityEvent.getAlternateCodes();
        for (alternateCode aCode : alternateCodes) {
            String codeType = aCode.getCodeType();
            if ("RKST".equalsIgnoreCase(codeType)) {
                facility.setRkst(aCode.getCode());
            } else if ("RKTS".equalsIgnoreCase(codeType)) {
                facility.setRkts(aCode.getCode());
            } else if ("UN CODE".equalsIgnoreCase(codeType)) {
                facility.setUnloc(aCode.getCode());
            } else if ("UN CODE(Lookup Only)".equalsIgnoreCase(codeType)) {
                facility.setUnlocLookup(aCode.getCode());
            } else if ("UN CODE(Return Only)".equalsIgnoreCase(codeType)) {
                facility.setUnlocReturn(aCode.getCode());
            } else if ("GEOID".equalsIgnoreCase(codeType)) {
                facility.setGeoId(aCode.getCode());
            } else if ("BIC".equalsIgnoreCase(codeType)) {
                facility.setBic(aCode.getCode());
            } else if ("HSUD CODE".equalsIgnoreCase(codeType)) {
                facility.setHsudCode(aCode.getCode());
            } else if ("HSUD NUMBER".equalsIgnoreCase(codeType)) {
                facility.setHsudNumber(aCode.getCode());
            } else if ("SMDG(City UN Code + Facility)".equalsIgnoreCase(codeType)) {
                facility.setSmdg(aCode.getCode());
            } else if ("CUSTOMSLOC".equalsIgnoreCase(codeType)) {
                facility.setCustomsLoc(aCode.getCode());
            } else if ("SCHEDULE D".equalsIgnoreCase(codeType)) {
                facility.setScheduleD(aCode.getCode());
            } else if ("SCHEDULE K".equalsIgnoreCase(codeType)) {
                facility.setScheduleK(aCode.getCode());
            } else if ("Business Unit ID".equalsIgnoreCase(codeType)) {
                facility.setBusinessUnitId(aCode.getCode());
            } else if ("LNS CODE".equalsIgnoreCase(codeType)) {
                facility.setLnsCode(aCode.getCode());
            } else if ("LNS UN CODE".equalsIgnoreCase(codeType)) {
                facility.setLnsUnCode(aCode.getCode());
            } else if ("IATA".equalsIgnoreCase(codeType)) {
                facility.setIata(aCode.getCode());
            }
        }

        return facility;
    }

    public static List<FacilityAlternateCode> mapToAlternateCodeLinks(List<alternateCode> alternateCodes, String facilityId) {
        return alternateCodes.stream()
                             .map(alternateCode -> FacilityAlternateCode.builder()
                                                                        .id(UUID.randomUUID())
                                                                        .facilityId(facilityId)
                                                                        .code(alternateCode.getCode())
                                                                        .codeType(alternateCode.getCodeType())
                                                                        .build())
                             .toList();
    }

    public static Address mapToAddress(address addressEvent, String facilityId) {
        return
                Address.builder()
                       .id(UUID.randomUUID()
                               .toString())
                       .facilityId(facilityId)
                       .houseNumber(addressEvent.getHouseNumber())
                       .street(addressEvent.getStreet())
                       .city(addressEvent.getCity())
                       .postalCode(addressEvent.getPostalCode())
                       .poBox(addressEvent.getPoBox())
                       .district(addressEvent.getDistrict())
                       .territory(addressEvent.getTerritory())
                       .countryName(addressEvent.getCountryName())
                       .countryCode(addressEvent.getCountryCode())
                       .addressLine2(addressEvent.getAddressLine2())
                       .addressLine3(addressEvent.getAddressLine3())
                       .latitude(addressEvent.getLatitude())
                       .longitude(addressEvent.getLongitude())
                       .addressQualityCheckIndicator(addressEvent.getAddressQualityCheckIndicator())
                       .build();
    }

    public static Optional<FacilityDetail> mapToFacilityDetail(facilityDetail facilityDetail, String facilityId) {
        return Optional.ofNullable(facilityDetail)
                       .map(fd ->
                               FacilityDetail.builder()
                                             .id(UUID.randomUUID()
                                                     .toString())
                                             .facilityId(facilityId)
                                             .weightLimitCraneKG(fd.getWeightLimitCraneKg())
                                             .weightLimitYardKG(fd.getWeightLimitYardKg())
                                             .vesselAgent(fd.getVesselAgent())
                                             .gpsFlag(fd.getGpsFlag())
                                             .gsmFlag(fd.getGsmFlag())
                                             .oceanFreightPricing(fd.getOceanFreightPricing())
                                             .brand(fd.getBrand())
                                             .commFacilityType(fd.getCommFacilityType())
                                             .exportEnquiriesEmail(fd.getExportEnquiriesEmail())
                                             .importEnquiriesEmail(fd.getImportEnquiriesEmail())
                                             .facilityFunction(fd.getFacilityFunction())
                                             .facilityFunctionDescription(fd.getFacilityFunctionDesc())
                                             .internationalDialCode(fd.getInternationalDialCode())
                                             .telephoneNumber(fd.getTelephoneNumber())
                                             .build());
    }

    public static Optional<List<FacilityTypeWrapper>> mapToFacilityTypesWrapper(facilityDetail fd, String facilityId) {
        return Optional.ofNullable(fd)
                       .map(facilityDetail::getFacilityTypes)
                       .map(facilityTypes -> facilityTypes.stream()
                                                          .map(type -> {
                                                              FacilityType facilityType = FacilityType.builder()
                                                                                                      .id(UUID.randomUUID()
                                                                                                              .toString())
                                                                                                      .facilityId(facilityId)
                                                                                                      .code(type.getCode())
                                                                                                      .name(type.getName())
                                                                                                      .masterType(type.getMasterType())
                                                                                                      .validThroughDate(type.getValidThroughDate())
                                                                                                      .build();

                                                              // TODO remove link object and wrapper class. Not needed
                                                              FacilityTypeLink facilityTypeLink = FacilityTypeLink.builder()
                                                                                                                  .facilityId(facilityId)
                                                                                                                  .opsFacilityTypeCode(type.getCode())
                                                                                                                  .build();

                                                              return FacilityTypeWrapper.builder()
                                                                                        .facilityType(facilityType)
                                                                                        .facilityTypeLink(facilityTypeLink)
                                                                                        .build();
                                                          })
                                                          .toList()
                       );
    }

    public static List<FacilityTypeLink> getFacilityTypeLinksAsList(List<FacilityTypeWrapper> facilityTypeWrappers) {
        return facilityTypeWrappers.stream()
                                   .map(FacilityTypeWrapper::getFacilityTypeLink)
                                   .toList();
    }

    public static List<FacilityType> getFacilityTypesAsList(List<FacilityTypeWrapper> facilityTypeWrappers) {
        return facilityTypeWrappers.stream()
                                   .map(FacilityTypeWrapper::getFacilityType)
                                   .toList();
    }

    public static List<OpeningHour> mapToFacilityOpeningHours(List<openingHour> openingHours, String facilityId) {
        return Optional.ofNullable(openingHours)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(oh ->
                               OpeningHour.builder()
                                          .id(UUID.randomUUID()
                                                  .toString())
                                          .facilityId(facilityId)
                                          .day(oh.getDay())
                                          .openTimeHours(oh.getOpenTimeHours())
                                          .openTimeMinutes(oh.getOpenTimeMinutes())
                                          .closeTimeHours(oh.getCloseTimeHours())
                                          .closeTimeMinutes(oh.getCloseTimeMinutes())
                                          .build()
                       )
                       .toList();
    }

    public static List<TransportMode> mapToTransportModes(List<transportMode> transportModes, String facilityId) {
        return Optional.ofNullable(transportModes)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(tm ->
                               TransportMode.builder()
                                            .id(UUID.randomUUID()
                                                    .toString())
                                            .facilityId(facilityId)
                                            .modeOfTransport(tm.getModeOfTransport())
                                            .transportCode(tm.getTransportCode())
                                            .transportDescription(tm.getTransportDescription())
                                            .validThroughDate(tm.getValidThroughDate())
                                            .build()
                       )
                       .toList();
    }

    public static List<FacilityService> mapToFacilityServices(List<facilityService> facilityServices, String facilityId) {
        return Optional.ofNullable(facilityServices)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(fs ->
                               FacilityService.builder()
                                              .id(UUID.randomUUID()
                                                      .toString())
                                              .facilityId(facilityId)
                                              .serviceName(fs.getServiceName())
                                              .serviceCode(fs.getServiceCode())
                                              .serviceDescription(fs.getServiceDescription())
                                              .validThroughDate(fs.getValidThroughDate())
                                              .build()
                       )
                       .toList();
    }

    public static List<Fence> mapToFences(List<fence> fences, String facilityId) {
        return Optional.ofNullable(fences)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(fence ->
                               Fence.builder()
                                    .id(UUID.randomUUID()
                                            .toString())
                                    .facilityId(facilityId)
                                    .name(fence.getName())
                                    .fenceType(fence.getFenceType())
                                    .build()
                       )
                       .toList();
    }

    public static List<ContactDetail> mapToContactDetails(List<contactDetail> contactDetails, String facilityId) {
        return Optional.ofNullable(contactDetails)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(contactDetail ->
                               ContactDetail.builder()
                                            .id(UUID.randomUUID()
                                                    .toString())
                                            .facilityId(facilityId)
                                            .firstName(contactDetail.getFirstName())
                                            .lastName(contactDetail.getLastName())
                                            .jobTitle(contactDetail.getJobTitle())
                                            .department(contactDetail.getDepartment())
                                            .internationalDialingCodePhone(contactDetail.getInternationalDialingCdPhone())
                                            .extension(contactDetail.getExtension())
                                            .phoneNumber(contactDetail.getPhoneNumber())
                                            .internationalDialingCodeMobile(contactDetail.getInternationalDialingCdMobile())
                                            .mobileNumber(contactDetail.getMobileNumber())
                                            .internationalDialingCodeFax(contactDetail.getInternaltionalDialingCodeFax())
                                            .emailAddress(contactDetail.getEmailAddress())
                                            .validThroughDate(contactDetail.getValidThroughDate())
                                            .build()
                       )
                       .toList();
    }

    private Parent mapToParent(parent parent) {
        return Parent.builder()
                     .name(parent.getName())
                     .type(parent.getType())
                     .build();
    }
}
