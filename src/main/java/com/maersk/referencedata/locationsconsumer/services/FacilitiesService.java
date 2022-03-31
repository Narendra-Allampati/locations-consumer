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
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityAlternateCodeLink;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityDetail;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityService;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Fence;
import com.maersk.referencedata.locationsconsumer.domains.facilities.OpeningHour;
import com.maersk.referencedata.locationsconsumer.domains.facilities.TransportMode;
import com.maersk.referencedata.locationsconsumer.domains.locations.Parent;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.AddressesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.ContactDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilitiesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityAlternateCodeLinksRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityServicesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FencesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.OpeningHoursRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.ParentsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.TransportModesRepository;
import com.maersk.shared.kafka.serialization.KafkaDeserializerUtils;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final FacilityAlternateCodeLinksRepository facilityAlternateCodeLinksRepository;
    private final FacilityDetailsRepository facilityDetailsRepository;
    private final FacilityServicesRepository facilityServicesRepository;
    private final FencesRepository fencesRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final ParentsRepository parentsRepository;
    private final TransportModesRepository transportModesRepository;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return facilityKafkaReceiver
                .receive()
                .doOnNext(event -> log.info("Received event: key {}, value {}", event.key(), event.value()))
                .doOnError(error -> log.error("Error receiving Facility record", error))
                .flatMap(this::handleFacilityEvent)
                .subscribe(event -> event.receiverOffset().acknowledge());
    }

    private Mono<ReceiverRecord<String, facilityMessage>> handleFacilityEvent(ReceiverRecord<String, facilityMessage> event) {
        return Mono.just(event)
                .map(KafkaDeserializerUtils::extractDeserializerError)
                .<facilityMessage>handle((tuple, sink) -> {
                    if (tuple.getT2().isEmpty() && Objects.nonNull(tuple.getT1().value())) {
                        log.info("Message received for offset {}", tuple.getT1().receiverOffset().offset());
                        sink.next(tuple.getT1().value());
                    } else {
                        log.error("Error while processing Facility " + tuple.getT2().get());
                    }
                })
                .flatMap(facilityMessage -> mapAndSaveFacilityEvent(facilityMessage.getFacility()))
                .then(Mono.just(event));
    }

    private Mono<Void> mapAndSaveFacilityEvent(facility facilityEvent) {

        String facilityId = facilityEvent.getFacilityId();

        final var facility = Facility.builder()
                .id(facilityEvent.getFacilityId())
                .name(facilityEvent.getName())
                .type(facilityEvent.getType())
                .extOwned(facilityEvent.getExtOwned())
                .status(facilityEvent.getStatus())
                .extExposed(facilityEvent.getExtExposed())
                .url(facilityEvent.getUrl())
                .doDAAC(facilityEvent.getDoDAAC())
                .build();

        final var address = mapToAddress(facilityEvent.getAddress(), facilityId);

        // TODO This one has a list of alternateCodes but do we need them?
        final var parent = mapToParent(facilityEvent.getParent());

        List<FacilityAlternateCodeLink> facilityAlternateCodeLinks = mapToAlternateCodeLinks(facilityEvent.getAlternateCodes(), facilityId);

        final var facilityDetail = mapToFacilityDetail(facilityEvent.getFacilityDetail(), facilityId);

        final var facilityTypes = mapToFacilityTypes(facilityEvent.getFacilityDetail());

        final var openingHours = mapToFacilityOpeningHours(facilityEvent.getOpeningHours(), facilityId);

        final var transportModes = mapToTransportModes(facilityEvent.getTransportModes(), facilityId);

        final var facilityServices = mapToFacilityServices(facilityEvent.getFacilityServices(), facilityId);

        final var fences = mapToFences(facilityEvent.getFences(), facilityId);

//        // this one has a list of alternateCodes as optional
//        final var businessDefinedAreas = mapToBusinessDefinedAreas(facilityEvent.getBda());

        final var contactDetails = mapToContactDetails(facilityEvent.getContactDetails(), facilityId);

        return Flux.merge(facilitiesRepository.save(facility),
                        addressesRepository.save(address),
                        parentsRepository.save(parent),
                        facilityAlternateCodeLinksRepository.saveAll(facilityAlternateCodeLinks),
                        facilityDetailsRepository.saveAll(Mono.justOrEmpty(facilityDetail)),
                        openingHoursRepository.saveAll(openingHours).collectList(),
                        transportModesRepository.saveAll(transportModes).collectList(),
                        facilityServicesRepository.saveAll(facilityServices).collectList(),
                        fencesRepository.saveAll(fences).collectList(),
                        contactDetailsRepository.saveAll(contactDetails).collectList())
                .then();
    }

    private List<FacilityAlternateCodeLink> mapToAlternateCodeLinks(List<alternateCode> alternateCodes, String facilityId) {
        return alternateCodes.stream()
                .map(alternateCode -> FacilityAlternateCodeLink.builder()
                        .facilityId(facilityId)
                        .alternateCodeId(alternateCode.getCode())
                        .build())
                .toList();
    }

    private Address mapToAddress(address addressEvent, String facilityId) {
        return
                Address.builder()
                        .id(facilityId)
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
                                .id(facilityId)
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
                                .facilityFunctionDesc(fd.getFacilityFunctionDesc())
                                .internationalDialCode(fd.getInternationalDialCode())
                                .telephoneNumber(fd.getTelephoneNumber())
                                .build());
    }

    private List<FacilityType> mapToFacilityTypes(facilityDetail facilityDetail) {
        return Optional.ofNullable(facilityDetail)
                .orElse(Collections.emptyList())
                .stream()
                .map(x -> {
                    final var opsFacilityTypes = x.getFacilityType();
                    if (opsFacilityTypes == null) {
                        return;
                    } else {
                        FacilityType.builder()
                                .code(opsFacilityTypes.)
                    }
                });
    }

    private List<OpeningHour> mapToFacilityOpeningHours(List<openingHour> openingHours, String facilityId) {
        return Optional.ofNullable(openingHours)
                .orElse(Collections.emptyList()).stream().map(oh ->
                        OpeningHour.builder()
                                .id(facilityId)
                                .day(oh.getDay())
                                .openTimeHours(oh.getOpenTimeHours())
                                .openTimeMinutes(oh.getOpenTimeMinutes())
                                .closeTimeHours(oh.getCloseTimeHours())
                                .closeTimeMinutes(oh.getCloseTimeMinutes())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<TransportMode> mapToTransportModes(List<transportMode> transportModes, String facilityId) {
        return Optional.ofNullable(transportModes)
                .orElse(Collections.emptyList()).stream().map(tm ->
                        TransportMode.builder()
                                .id(facilityId)
                                .modeOfTransport(tm.getModeOfTransport())
                                .transportCode(tm.getTransportCode())
                                .transportDescription(tm.getTransportDescription())
                                .validThroughDate(tm.getValidThroughDate())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<FacilityService> mapToFacilityServices(List<facilityService> facilityServices, String facilityId) {
        return Optional.ofNullable(facilityServices)
                .orElse(Collections.emptyList()).stream().map(fs ->
                        FacilityService.builder()
                                .id(facilityId)
                                .serviceName(fs.getServiceName())
                                .serviceCode(fs.getServiceCode())
                                .serviceDescription(fs.getServiceDescription())
                                .validThroughDate(fs.getValidThroughDate())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<Fence> mapToFences(List<fence> fences, String facilityId) {
        return Optional.ofNullable(fences)
                .orElse(Collections.emptyList()).stream().map(fence ->
                        Fence.builder()
                                .id(facilityId)
                                .name(fence.getName())
                                .fenceType(fence.getFenceType())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private List<ContactDetail> mapToContactDetails(List<contactDetail> contactDetails, String facilityId) {
        return Optional.ofNullable(contactDetails)
                .orElse(Collections.emptyList()).stream().map(contactDetail ->
                        ContactDetail.builder()
                                .id(facilityId)
                                .firstName(contactDetail.getFirstName())
                                .lastName(contactDetail.getLastName())
                                .jobTitle(contactDetail.getJobTitle())
                                .department(contactDetail.getDepartment())
                                .internationalDialingCdPhone(contactDetail.getInternationalDialingCdPhone())
                                .extension(contactDetail.getExtension())
                                .phoneNumber(contactDetail.getPhoneNumber())
                                .internationalDialingCdMobile(contactDetail.getInternationalDialingCdMobile())
                                .mobileNumber(contactDetail.getMobileNumber())
                                .internationalDialingCdFax(contactDetail.getInternaltionalDialingCodeFax())
                                .emailAddress(contactDetail.getEmailAddress())
                                .validThroughDate(contactDetail.getValidThroughDate())
                                .build()
                )
                .collect(Collectors.toList());
    }
}
