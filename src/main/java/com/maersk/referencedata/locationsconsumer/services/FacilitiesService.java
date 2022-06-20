package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.facility.smds.operations.msk.address;
import com.maersk.facility.smds.operations.msk.alternateCode;
import com.maersk.facility.smds.operations.msk.contactDetail;
import com.maersk.facility.smds.operations.msk.facility;
import com.maersk.facility.smds.operations.msk.facilityDetail;
import com.maersk.facility.smds.operations.msk.facilityMessage;
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
import com.maersk.referencedata.locationsconsumer.repositories.facilities.AddressesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.ContactDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilitiesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityAlternateCodesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityServicesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityTypeLinksRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityTypesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FencesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.OpeningHoursRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.ParentsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.TransportModesRepository;
import com.maersk.shared.kafka.serialization.KafkaDeserializerUtils;
import com.maersk.shared.kafka.utilities.ErrorHandlingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;

import static com.maersk.referencedata.locationsconsumer.mappers.GeographyMapper.findGeoIdFromFacilityParentAlternateCodes;

/**
 * @author Anders Clausen on 20/03/2022.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FacilitiesService {

    private final KafkaReceiver<String, facilityMessage> facilityKafkaReceiver;

    private final AddressesRepository addressesRepository;
    private final ContactDetailsRepository contactDetailsRepository;
    private final FacilitiesRepository facilitiesRepository;
    private final FacilityAlternateCodesRepository facilityAlternateCodesRepository;
    private final FacilityDetailsRepository facilityDetailsRepository;
    private final FacilityServicesRepository facilityServicesRepository;
    private final FacilityTypesRepository facilityTypesRepository;
    private final FacilityTypeLinksRepository facilityTypeLinksRepository;
    private final FencesRepository fencesRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final ParentsRepository parentsRepository;
    private final TransportModesRepository transportModesRepository;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return facilityKafkaReceiver
                .receive()
                .name("facility events")
                .tag("source", "kafka")
                .metrics()
                .doOnError(error -> log.warn("Error receiving Facility record, exception -> {}, retry will be attempted",
                        error.getLocalizedMessage(), error))
                .retryWhen(Retry.indefinitely()
                                .filter(ErrorHandlingUtils::isRetriableKafkaError))
                .doOnError(error -> log.warn("Error thrown whilst processing facility records, error isn't a " +
                        "known retriable error, will attempt to retry processing records , exception -> {}", error.getLocalizedMessage(), error))
                .retryWhen(Retry.fixedDelay(100, Duration.ofMinutes(1)))
                .concatMap(this::handleFacilityEvent)
                .subscribe(event -> event.receiverOffset()
                                         .acknowledge());
    }

    private Mono<ReceiverRecord<String, facilityMessage>> handleFacilityEvent(ReceiverRecord<String, facilityMessage> facilityRecord) {
        return Mono.just(facilityRecord)
                   .map(KafkaDeserializerUtils::extractDeserializerError)
                   .<facilityMessage>handle((tuple, sink) -> {
                       if (tuple.getT2()
                                .isEmpty() && Objects.nonNull(tuple.getT1()
                                                                   .value())) {
                           sink.next(tuple.getT1()
                                          .value());
                       } else {
                           log.error("Error while processing Facility " + tuple.getT2()
                                                                               .get());
                       }
                   })
                   .doOnNext(event -> log.info("Received facility event: key {}, facilityId{}, partition number {}", facilityRecord.key()
                           , event.getFacility()
                                  .getFacilityId(), facilityRecord.receiverOffset()
                                                                  .topicPartition()
                                                                  .partition()))
                   .flatMap(facilityMessage -> createOrUpdateFacility(facilityMessage.getFacility()))
                   .doOnError(ex -> log.error("Error processing event after all retries {} and value {}", facilityRecord.key(), facilityRecord.value(), ex))
                   .onErrorResume(ex -> Mono.empty())
                   .then(Mono.just(facilityRecord));
    }

    private Mono<String> createOrUpdateFacility(facility facilityEvent) {
        return facilitiesRepository.findById(facilityEvent.getFacilityId())
                                   .flatMap(facilityFromDB -> updateFacility(facilityEvent))
                                   .switchIfEmpty(saveFacility(facilityEvent));
    }

    private Mono<String> saveFacility(facility facilityEvent) {
        return mapAndSaveFacilityEvent(facilityEvent);
    }

    private Mono<String> updateFacility(facility facilityEvent) {
        return facilitiesRepository.deleteById(facilityEvent.getFacilityId())
                                   .doOnSuccess(event -> log.info("Deleted facility event: key {}", facilityEvent.getFacilityId()))
                                   .then(saveFacility(facilityEvent));
    }

    private Mono<String> mapAndSaveFacilityEvent(facility facilityEvent) {

        String facilityId = facilityEvent.getFacilityId();

        final var facility = mapToFacility(facilityEvent);

        final var address = mapToAddress(facilityEvent.getAddress(), facilityId);

        // TODO This one has a list of alternateCodes but do we need them?
//        final var parent = mapToParent(facilityEvent.getParent());

        List<FacilityAlternateCode> facilityAlternateCodes = mapToAlternateCodeLinks(facilityEvent.getAlternateCodes(), facilityId);

        final var facilityDetail = mapToFacilityDetail(facilityEvent.getFacilityDetail(), facilityId);

        final var facilityTypeWrappers = mapToFacilityTypes(facilityEvent.getFacilityDetail(), facilityId);
        List<FacilityType> facilityTypes = facilityTypeWrappers.map(this::getFacilityTypesAsList)
                                                               .orElse(Collections.emptyList());
        List<FacilityTypeLink> facilityTypeLinks = facilityTypeWrappers.map(this::getFacilityTypeLinksAsList)
                                                                       .orElse(Collections.emptyList());

        final var openingHours = mapToFacilityOpeningHours(facilityEvent.getOpeningHours(), facilityId);

        final var transportModes = mapToTransportModes(facilityEvent.getTransportModes(), facilityId);

        final var facilityServices = mapToFacilityServices(facilityEvent.getFacilityServices(), facilityId);

        final var fences = mapToFences(facilityEvent.getFences(), facilityId);

//        // this one has a list of alternateCodes as optional
//        final var businessDefinedAreas = mapToBusinessDefinedAreas(facilityEvent.getBda());

        final var contactDetails = mapToContactDetails(facilityEvent.getContactDetails(), facilityId);

        return Flux.concat(facilitiesRepository.save(facility)
                                               .then(),
                           addressesRepository.save(address)
                                              .then(),
//                        parentsRepository.save(parent).then(),
                           facilityAlternateCodesRepository.saveAll(facilityAlternateCodes)
                                                           .then(),
                           facilityDetailsRepository.saveAll(Mono.justOrEmpty(facilityDetail))
                                                    .then(),
                           facilityTypesRepository.saveAll(facilityTypes)
                                                  .then(),
//                        facilityTypeLinksRepository.saveAll(facilityTypeLinks).then(),
                           openingHoursRepository.saveAll(openingHours)
                                                 .then(),
                           transportModesRepository.saveAll(transportModes)
                                                   .then(),
                           facilityServicesRepository.saveAll(facilityServices)
                                                     .then(),
                           fencesRepository.saveAll(fences)
                                           .then(),
                           contactDetailsRepository.saveAll(contactDetails)
                                                   .then())
                   .then(Mono.just("1"));
    }

    private Facility mapToFacility(facility facilityEvent) {
        return Facility.builder()
                       .isNew(true)
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
    }

    private List<FacilityAlternateCode> mapToAlternateCodeLinks(List<alternateCode> alternateCodes, String facilityId) {
        return alternateCodes.stream()
                             .map(alternateCode -> FacilityAlternateCode.builder()
                                                                        .isNew(true)
                                                                        .id(UUID.randomUUID())
                                                                        .facilityId(facilityId)
                                                                        .code(alternateCode.getCode())
                                                                        .codeType(alternateCode.getCodeType())
                                                                        .build())
                             .toList();
    }

    private Address mapToAddress(address addressEvent, String facilityId) {
        return
                Address.builder()
                       .isNew(true)
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

    private Parent mapToParent(parent parent) {
        return Parent.builder()
                     .name(parent.getName())
                     .type(parent.getType())
                     .build();
    }

    private Optional<FacilityDetail> mapToFacilityDetail(facilityDetail facilityDetail, String facilityId) {
        return Optional.ofNullable(facilityDetail)
                       .map(fd ->
                               FacilityDetail.builder()
                                             .isNew(true)
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

    private Optional<List<FacilityTypeWrapper>> mapToFacilityTypes(facilityDetail fd, String facilityId) {
        return Optional.ofNullable(fd)
                       .map(facilityDetail::getFacilityTypes)
                       .map(facilityTypes -> facilityTypes.stream()
                                                          .map(type -> {
                                                              FacilityType facilityType = FacilityType.builder()
                                                                                                      .isNew(true)
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

    private List<FacilityTypeLink> getFacilityTypeLinksAsList(List<FacilityTypeWrapper> facilityTypeWrappers) {
        return facilityTypeWrappers.stream()
                                   .map(FacilityTypeWrapper::getFacilityTypeLink)
                                   .toList();
    }

    private List<FacilityType> getFacilityTypesAsList(List<FacilityTypeWrapper> facilityTypeWrappers) {
        return facilityTypeWrappers.stream()
                                   .map(FacilityTypeWrapper::getFacilityType)
                                   .toList();
    }

    private List<OpeningHour> mapToFacilityOpeningHours(List<openingHour> openingHours, String facilityId) {
        return Optional.ofNullable(openingHours)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(oh ->
                               OpeningHour.builder()
                                          .isNew(true)
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

    private List<TransportMode> mapToTransportModes(List<transportMode> transportModes, String facilityId) {
        return Optional.ofNullable(transportModes)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(tm ->
                               TransportMode.builder()
                                            .isNew(true)
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

    private List<FacilityService> mapToFacilityServices(List<facilityService> facilityServices, String facilityId) {
        return Optional.ofNullable(facilityServices)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(fs ->
                               FacilityService.builder()
                                              .isNew(true)
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

    private List<Fence> mapToFences(List<fence> fences, String facilityId) {
        return Optional.ofNullable(fences)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(fence ->
                               Fence.builder()
                                    .isNew(true)
                                    .id(UUID.randomUUID()
                                            .toString())
                                    .facilityId(facilityId)
                                    .name(fence.getName())
                                    .fenceType(fence.getFenceType())
                                    .build()
                       )
                       .toList();
    }

    private List<ContactDetail> mapToContactDetails(List<contactDetail> contactDetails, String facilityId) {
        return Optional.ofNullable(contactDetails)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(contactDetail ->
                               ContactDetail.builder()
                                            .isNew(true)
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
}
