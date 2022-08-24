package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.facility.smds.operations.msk.facilityMessage;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Address;
import com.maersk.referencedata.locationsconsumer.domains.facilities.ContactDetail;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Facility;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityAlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityDetail;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityService;
import com.maersk.referencedata.locationsconsumer.domains.facilities.FacilityType;
import com.maersk.referencedata.locationsconsumer.domains.facilities.Fence;
import com.maersk.referencedata.locationsconsumer.domains.facilities.OpeningHour;
import com.maersk.referencedata.locationsconsumer.domains.facilities.TransportMode;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.AddressesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.ContactDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilitiesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityAlternateCodesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityDetailsRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityServicesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FacilityTypesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.FencesRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.OpeningHoursRepository;
import com.maersk.referencedata.locationsconsumer.repositories.facilities.TransportModesRepository;
import com.maersk.referencedata.locationsconsumer.startup.FacilityTypesMappingsLoad;
import com.maersk.referencedata.locationsconsumer.utils.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.maersk.referencedata.locationsconsumer.utils.TestUtils.getFacilityValueSchema;
import static com.maersk.referencedata.locationsconsumer.utils.TestUtils.getReceiverOffset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilitiesServiceTest {

    private final String FACILITY_TOPIC_NAME = "MSK.geography.facility.topic.internal.any.v3";
    private static final String BASE_FILE_PATH_EVENTS = "events/";
    private FacilitiesService facilitiesService;
    private ReceiverOffset receiverOffset;

    // The week days come in alphabetical order
    private final List<String> openingHoursWeekDays = List.of("Friday", "Monday", "Thursday", "Tuesday", "Wednesday");

    @Mock
    private KafkaReceiver<String, facilityMessage> mockKafkaReceiver;
    @Mock
    private FacilitiesRepository facilitiesRepository;
    @Mock
    private AddressesRepository addressesRepository;
    @Mock
    private ContactDetailsRepository contactDetailsRepository;
    @Mock
    private FacilityAlternateCodesRepository facilityAlternateCodesRepository;
    @Mock
    private FacilityDetailsRepository facilityDetailsRepository;
    @Mock
    private FacilityServicesRepository facilityServicesRepository;
    @Mock
    private FacilityTypesRepository facilityTypesRepository;
    @Mock
    private FencesRepository fencesRepository;
    @Mock
    private OpeningHoursRepository openingHoursRepository;
    @Mock
    private TransportModesRepository transportModesRepository;
    @Mock
    private FacilityTypesMappingsLoad facilityTypesMappingsLoad;

    @Captor
    private ArgumentCaptor<Facility> facilityCaptor;
    @Captor
    private ArgumentCaptor<Publisher<FacilityDetail>> facilityDetailsCaptor;
    @Captor
    private ArgumentCaptor<List<ContactDetail>> contactDetailsCaptor;
    @Captor
    private ArgumentCaptor<List<Fence>> fencesCaptor;
    @Captor
    private ArgumentCaptor<List<FacilityService>> facilityServicesCaptor;
    @Captor
    private ArgumentCaptor<List<TransportMode>> transportModesCaptor;
    @Captor
    private ArgumentCaptor<List<OpeningHour>> openingHoursCaptor;
    @Captor
    private ArgumentCaptor<List<FacilityType>> facilityTypesCaptor;
    @Captor
    private ArgumentCaptor<Address> addressCaptor;
    @Captor
    private ArgumentCaptor<List<FacilityAlternateCode>> alternateCodesCaptor;

    @BeforeEach
    public void setUp() {
        this.facilitiesService = new FacilitiesService(mockKafkaReceiver, addressesRepository, contactDetailsRepository, facilitiesRepository, facilityAlternateCodesRepository, facilityDetailsRepository, facilityServicesRepository, facilityTypesRepository, fencesRepository, openingHoursRepository, transportModesRepository, facilityTypesMappingsLoad);
    }

    @Test
    void insertNewRecordSuccessfully_FacilityExistsInDBBeforeHand() throws IOException, URISyntaxException {

        // given
        String payload = TestUtils.readFileToString(BASE_FILE_PATH_EVENTS + "Facility.v3_Sample-1.json");
        final facilityMessage avroPayload = getFacilityValueSchema(payload);

        List<ReceiverRecord<String, facilityMessage>> facilityReceiverRecords = new ArrayList<>();
        receiverOffset = getReceiverOffset(FACILITY_TOPIC_NAME);

        final ReceiverRecord<String, facilityMessage> facilityReceiverRecord = new ReceiverRecord<>(new ConsumerRecord<>(FACILITY_TOPIC_NAME, 0, 0L, "avroKey", avroPayload), receiverOffset);

        facilityReceiverRecords.addAll(0, Collections.singleton(facilityReceiverRecord));
        Flux<ReceiverRecord<String, facilityMessage>> mockFacilityReceiverRecords = Flux.fromIterable(facilityReceiverRecords);

        // when
        when(mockKafkaReceiver.receive()).thenReturn(mockFacilityReceiverRecords);
        when(facilitiesRepository.findById("8486895257834")).thenReturn(Mono.just(Facility.builder().build()));
        when(facilitiesRepository.deleteById("8486895257834")).thenReturn(Mono.empty());
        when(facilitiesRepository.save(any(Facility.class))).thenReturn(Mono.just(Facility.builder().build()));
        when(addressesRepository.save(any(Address.class))).thenReturn(Mono.just(Address.builder().build()));
        when(facilityAlternateCodesRepository.saveAll(any(List.class))).thenReturn(Flux.just(FacilityAlternateCode.builder().build()));
        when(facilityDetailsRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(FacilityDetail.builder().build()));
        when(facilityTypesRepository.saveAll(any(List.class))).thenReturn(Flux.just(FacilityType.builder().build()));
        when(openingHoursRepository.saveAll(any(List.class))).thenReturn(Flux.just(OpeningHour.builder().build()));
        when(transportModesRepository.saveAll(any(List.class))).thenReturn(Flux.just(TransportMode.builder().build()));
        when(facilityServicesRepository.saveAll(any(List.class))).thenReturn(Flux.just(FacilityService.builder().build()));
        when(fencesRepository.saveAll(any(List.class))).thenReturn(Flux.just(Fence.builder().build()));
        when(contactDetailsRepository.saveAll(any(List.class))).thenReturn(Flux.just(ContactDetail.builder().build()));

        // then
        facilitiesService.startKafkaConsumer();

        verify(facilitiesRepository, times(1)).findById("8486895257834");
        verify(facilitiesRepository, times(1)).deleteById("8486895257834");
        verify(facilitiesRepository, times(1)).save(facilityCaptor.capture());
        verify(addressesRepository, times(1)).save(addressCaptor.capture());
        verify(facilityAlternateCodesRepository, times(1)).saveAll(alternateCodesCaptor.capture());
        verify(facilityDetailsRepository, times(1)).saveAll(facilityDetailsCaptor.capture());
        verify(facilityTypesRepository, times(1)).saveAll(facilityTypesCaptor.capture());
        verify(openingHoursRepository, times(1)).saveAll(openingHoursCaptor.capture());
        verify(transportModesRepository, times(1)).saveAll(transportModesCaptor.capture());
        verify(facilityServicesRepository, times(1)).saveAll(facilityServicesCaptor.capture());
        verify(fencesRepository, times(1)).saveAll(fencesCaptor.capture());
        verify(contactDetailsRepository, times(1)).saveAll(contactDetailsCaptor.capture());

        assertFacility(facilityCaptor.getValue());
        assertAddresses(addressCaptor.getValue());
        assertAlternateCodes(alternateCodesCaptor.getValue());
        assertFacilityDetails(facilityDetailsCaptor.getValue());
        assertFacilityTypes(facilityTypesCaptor.getValue());
        assertOpeningHours(openingHoursCaptor.getValue());
        assertThat(transportModesCaptor.getValue()).isEmpty();
        assertFacilityServices(facilityServicesCaptor.getValue());
        assertThat(fencesCaptor.getValue()).isEmpty();
        assertThat(contactDetailsCaptor.getValue()).isEmpty();
    }

    @Test
    void insertNewRecordSuccessfully_FacilityDoesNotExistInDBBeforeHand() throws IOException, URISyntaxException {

        // given
        String payload = TestUtils.readFileToString(BASE_FILE_PATH_EVENTS + "Facility.v3_Sample-1.json");
        final facilityMessage avroPayload = getFacilityValueSchema(payload);

        List<ReceiverRecord<String, facilityMessage>> facilityReceiverRecords = new ArrayList<>();
        receiverOffset = getReceiverOffset(FACILITY_TOPIC_NAME);

        final ReceiverRecord<String, facilityMessage> facilityReceiverRecord = new ReceiverRecord<>(new ConsumerRecord<>(FACILITY_TOPIC_NAME, 0, 0L, "avroKey", avroPayload), receiverOffset);

        facilityReceiverRecords.addAll(0, Collections.singleton(facilityReceiverRecord));
        Flux<ReceiverRecord<String, facilityMessage>> mockFacilityReceiverRecords = Flux.fromIterable(facilityReceiverRecords);

        // when
        when(mockKafkaReceiver.receive()).thenReturn(mockFacilityReceiverRecords);
        when(facilitiesRepository.findById("8486895257834")).thenReturn(Mono.empty());
        when(facilitiesRepository.save(any(Facility.class))).thenReturn(Mono.just(Facility.builder().build()));
        when(addressesRepository.save(any(Address.class))).thenReturn(Mono.just(Address.builder().build()));
        when(facilityAlternateCodesRepository.saveAll(any(List.class))).thenReturn(Flux.just(FacilityAlternateCode.builder().build()));
        when(facilityDetailsRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(FacilityDetail.builder().build()));
        when(facilityTypesRepository.saveAll(any(List.class))).thenReturn(Flux.just(FacilityType.builder().build()));
        when(openingHoursRepository.saveAll(any(List.class))).thenReturn(Flux.just(OpeningHour.builder().build()));
        when(transportModesRepository.saveAll(any(List.class))).thenReturn(Flux.just(TransportMode.builder().build()));
        when(facilityServicesRepository.saveAll(any(List.class))).thenReturn(Flux.just(FacilityService.builder().build()));
        when(fencesRepository.saveAll(any(List.class))).thenReturn(Flux.just(Fence.builder().build()));
        when(contactDetailsRepository.saveAll(any(List.class))).thenReturn(Flux.just(ContactDetail.builder().build()));

        // then
        facilitiesService.startKafkaConsumer();

        verify(facilitiesRepository, times(1)).findById("8486895257834");
        verify(facilitiesRepository, times(1)).save(facilityCaptor.capture());
        verify(addressesRepository, times(1)).save(addressCaptor.capture());
        verify(facilityAlternateCodesRepository, times(1)).saveAll(alternateCodesCaptor.capture());
        verify(facilityDetailsRepository, times(1)).saveAll(facilityDetailsCaptor.capture());
        verify(facilityTypesRepository, times(1)).saveAll(facilityTypesCaptor.capture());
        verify(openingHoursRepository, times(1)).saveAll(openingHoursCaptor.capture());
        verify(transportModesRepository, times(1)).saveAll(transportModesCaptor.capture());
        verify(facilityServicesRepository, times(1)).saveAll(facilityServicesCaptor.capture());
        verify(fencesRepository, times(1)).saveAll(fencesCaptor.capture());
        verify(contactDetailsRepository, times(1)).saveAll(contactDetailsCaptor.capture());

        assertFacility(facilityCaptor.getValue());
        assertAddresses(addressCaptor.getValue());
        assertAlternateCodes(alternateCodesCaptor.getValue());
        assertFacilityDetails(facilityDetailsCaptor.getValue());
        assertFacilityTypes(facilityTypesCaptor.getValue());
        assertOpeningHours(openingHoursCaptor.getValue());
        assertThat(transportModesCaptor.getValue()).isEmpty();
        assertFacilityServices(facilityServicesCaptor.getValue());
        assertThat(fencesCaptor.getValue()).isEmpty();
        assertThat(contactDetailsCaptor.getValue()).isEmpty();
    }

    private void assertAlternateCodes(List<FacilityAlternateCode> alternateCodesCaptorValue) {

        assertThat(alternateCodesCaptorValue.get(0).getFacilityId()).isEqualTo("8486895257834");
        assertThat(alternateCodesCaptorValue.get(0).getCode()).isEqualTo("ARMEN30P");
        assertThat(alternateCodesCaptorValue.get(0).getCodeType()).isEqualTo("RKST");
        assertThat(alternateCodesCaptorValue.get(1).getFacilityId()).isEqualTo("8486895257834");
        assertThat(alternateCodesCaptorValue.get(1).getCode()).isEqualTo("MEN30");
        assertThat(alternateCodesCaptorValue.get(1).getCodeType()).isEqualTo("RKTS");
        assertThat(alternateCodesCaptorValue.get(2).getFacilityId()).isEqualTo("8486895257834");
        assertThat(alternateCodesCaptorValue.get(2).getCode()).isEqualTo("8486895257834");
        assertThat(alternateCodesCaptorValue.get(2).getCodeType()).isEqualTo("GEOID");
        assertThat(alternateCodesCaptorValue.get(3).getFacilityId()).isEqualTo("8486895257834");
        assertThat(alternateCodesCaptorValue.get(3).getCode()).isEqualTo("100012179");
        assertThat(alternateCodesCaptorValue.get(3).getCodeType()).isEqualTo("HSUD NUMBER");
        assertThat(alternateCodesCaptorValue.get(4).getFacilityId()).isEqualTo("8486895257834");
        assertThat(alternateCodesCaptorValue.get(4).getCode()).isEqualTo("ARMDZMARP");
        assertThat(alternateCodesCaptorValue.get(4).getCodeType()).isEqualTo("HSUD CODE");
    }

    private void assertFacility(Facility facilityCaptorValue) {
        assertThat(facilityCaptorValue.getId()).isEqualTo("8486895257834");
        assertThat(facilityCaptorValue.getName()).isEqualTo("Marpacifico S.A.");
        assertThat(facilityCaptorValue.getType()).isEqualTo("OperationalFacility");
        assertThat(facilityCaptorValue.getStatus()).isEqualTo("Active");
        assertThat(facilityCaptorValue.isExtExposed()).isTrue();
        assertThat(facilityCaptorValue.isExtOwned()).isFalse();
        assertThat(facilityCaptorValue.getUrl()).isEqualTo("http://marpacifico.com/");
        assertThat(facilityCaptorValue.getParentId()).isEqualTo("1TFSQYR9081P5");
        assertThat(facilityCaptorValue.getParentName()).isEqualTo("Mendoza");
        assertThat(facilityCaptorValue.getParentType()).isEqualTo("City");
        assertThat(facilityCaptorValue.getRkst()).isEqualTo("ARMEN30P");
        assertThat(facilityCaptorValue.getRkts()).isEqualTo("MEN30");
        assertThat(facilityCaptorValue.getHsudCode()).isEqualTo("ARMDZMARP");
        assertThat(facilityCaptorValue.getHsudNumber()).isEqualTo("100012179");
        assertThat(facilityCaptorValue.getGeoId()).isEqualTo("8486895257834");
    }

    private void assertFacilityDetails(Publisher<FacilityDetail> facilityDetailsCaptorValue) {
        FacilityDetail facilityDetail = Mono.from(facilityDetailsCaptorValue)
                                   .block();
        assertThat(facilityDetail.getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityDetail.getVesselAgent()).isEqualTo("NO");
        assertThat(facilityDetail.getOceanFreightPricing()).isEqualTo("NO");
        assertThat(facilityDetail.getWeightLimitCraneKG()).isNull();
        assertThat(facilityDetail.getWeightLimitYardKG()).isNull();
        assertThat(facilityDetail.getGpsFlag()).isNull();
        assertThat(facilityDetail.getGsmFlag()).isNull();
        assertThat(facilityDetail.getBrand()).isNull();
        assertThat(facilityDetail.getCommFacilityType()).isNull();
        assertThat(facilityDetail.getExportEnquiriesEmail()).isNull();
        assertThat(facilityDetail.getImportEnquiriesEmail()).isNull();
        assertThat(facilityDetail.getFacilityFunction()).isNull();
        assertThat(facilityDetail.getFacilityFunctionDescription()).isNull();
        assertThat(facilityDetail.getInternationalDialCode()).isNull();
        assertThat(facilityDetail.getTelephoneNumber()).isNull();
    }

    // TODO Might not need method below
    private void assertFences(List<Fence> fencesCaptorValue) {

    }

    private void assertFacilityServices(List<FacilityService> facilityServicesCaptorValue) {
        assertThat(facilityServicesCaptorValue.get(0).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityServicesCaptorValue.get(0).getServiceName()).isEqualTo("Empty Drop Off");
        assertThat(facilityServicesCaptorValue.get(0).getServiceCode()).isEqualTo("CNTH");
        assertThat(facilityServicesCaptorValue.get(0).getServiceDescription()).isEqualTo(" - Empty Drop Off");
        assertThat(facilityServicesCaptorValue.get(0).getValidThroughDate()).isEqualTo("2099-12-31");
        assertThat(facilityServicesCaptorValue.get(1).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityServicesCaptorValue.get(1).getServiceName()).isEqualTo("Empty Pick up");
        assertThat(facilityServicesCaptorValue.get(1).getServiceCode()).isEqualTo("CNTH");
        assertThat(facilityServicesCaptorValue.get(1).getServiceDescription()).isEqualTo(" - Empty Pick up");
        assertThat(facilityServicesCaptorValue.get(1).getValidThroughDate()).isEqualTo("2099-12-31");
        assertThat(facilityServicesCaptorValue.get(2).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityServicesCaptorValue.get(2).getServiceName()).isEqualTo("Intermodal services available: Clearance on wheels");
        assertThat(facilityServicesCaptorValue.get(2).getServiceCode()).isEqualTo("INTM");
        assertThat(facilityServicesCaptorValue.get(2).getServiceDescription()).isEqualTo(" - Intermodal services available: Clearance on wheels");
        assertThat(facilityServicesCaptorValue.get(2).getValidThroughDate()).isEqualTo("2099-12-31");
        assertThat(facilityServicesCaptorValue.get(3).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityServicesCaptorValue.get(3).getServiceName()).isEqualTo("Intermodal services available: Truck");
        assertThat(facilityServicesCaptorValue.get(3).getServiceCode()).isEqualTo("INTM");
        assertThat(facilityServicesCaptorValue.get(3).getServiceDescription()).isEqualTo(" - Intermodal services available: Truck");
        assertThat(facilityServicesCaptorValue.get(3).getValidThroughDate()).isEqualTo("2099-12-31");
        assertThat(facilityServicesCaptorValue.get(4).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityServicesCaptorValue.get(4).getServiceName()).isEqualTo("Lift on/Lift off facilities");
        assertThat(facilityServicesCaptorValue.get(4).getServiceCode()).isEqualTo("CNTH");
        assertThat(facilityServicesCaptorValue.get(4).getServiceDescription()).isEqualTo(" - Lift on/Lift off facilities");
        assertThat(facilityServicesCaptorValue.get(4).getValidThroughDate()).isEqualTo("2099-12-31");
    }

    // TODO might not be needed
    private void assertTransportModes(List<TransportMode> transportModesCaptorValue) {
        assertThat(transportModesCaptorValue.get(0).getFacilityId()).isEqualTo("8486895257834");
        assertThat(transportModesCaptorValue.get(0).getModeOfTransport()).isEqualTo("8486895257834");
        assertThat(transportModesCaptorValue.get(0).getTransportCode()).isEqualTo("8486895257834");
        assertThat(transportModesCaptorValue.get(0).getTransportDescription()).isEqualTo("8486895257834");
        assertThat(transportModesCaptorValue.get(0).getValidThroughDate()).isEqualTo("8486895257834");
    }

    private void assertAddresses(Address addressCaptorValue) {
        assertThat(addressCaptorValue.getFacilityId()).isEqualTo("8486895257834");
        assertThat(addressCaptorValue.getAddressLine2()).isEqualTo("5500, Mendoza, Argentina");
        assertThat(addressCaptorValue.getAddressLine3()).isEqualTo("ARMDZ");
        assertThat(addressCaptorValue.getCountryName()).isEqualTo("Argentina");
        assertThat(addressCaptorValue.getCountryCode()).isEqualTo("AR");
        assertThat(addressCaptorValue.getHouseNumber()).isEqualTo("128");
        assertThat(addressCaptorValue.getStreet()).isEqualTo("RUFINO ORTEGA");
        assertThat(addressCaptorValue.getCity()).isEqualTo("Mendoza");
        assertThat(addressCaptorValue.getPostalCode()).isEqualTo("5500");
        assertThat(addressCaptorValue.getTerritory()).isEqualTo("Mendoza");
        assertThat(addressCaptorValue.getLatitude()).isEqualTo("-32.891901");
        assertThat(addressCaptorValue.getLongitude()).isEqualTo("-68.851868");
        assertThat(addressCaptorValue.getPoBox()).isNull();
        assertThat(addressCaptorValue.getDistrict()).isNull();
    }

    private void assertFacilityTypes(List<FacilityType> facilityTypesCaptorValue) {
        assertThat(facilityTypesCaptorValue.get(0).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityTypesCaptorValue.get(0).getCode()).isEqualTo("FCT_TYPE.MAIN_N_REP");
        assertThat(facilityTypesCaptorValue.get(0).getName()).isEqualTo("Maintenance and Repair");
        assertThat(facilityTypesCaptorValue.get(0).getMasterType()).isEqualTo("Facility type");
        assertThat(facilityTypesCaptorValue.get(0).getValidThroughDate().toString()).isEqualTo("2099-12-31");
        assertThat(facilityTypesCaptorValue.get(1).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityTypesCaptorValue.get(1).getCode()).isEqualTo("FCT_OPS_TYPE.ICD");
        assertThat(facilityTypesCaptorValue.get(1).getName()).isEqualTo("Inland Container Depot");
        assertThat(facilityTypesCaptorValue.get(1).getMasterType()).isEqualTo("Facility type");
        assertThat(facilityTypesCaptorValue.get(1).getValidThroughDate().toString()).isEqualTo("2099-12-31");
        assertThat(facilityTypesCaptorValue.get(2).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityTypesCaptorValue.get(2).getCode()).isEqualTo("FCT_TYPE.REEFER_DEP");
        assertThat(facilityTypesCaptorValue.get(2).getName()).isEqualTo("Reefer Depot");
        assertThat(facilityTypesCaptorValue.get(2).getMasterType()).isEqualTo("Facility type");
        assertThat(facilityTypesCaptorValue.get(2).getValidThroughDate().toString()).isEqualTo("2099-12-31");
        assertThat(facilityTypesCaptorValue.get(3).getFacilityId()).isEqualTo("8486895257834");
        assertThat(facilityTypesCaptorValue.get(3).getCode()).isEqualTo("FCT_TYPE.TRUCKER");
        assertThat(facilityTypesCaptorValue.get(3).getName()).isEqualTo("Trucker");
        assertThat(facilityTypesCaptorValue.get(3).getMasterType()).isEqualTo("Facility type");
        assertThat(facilityTypesCaptorValue.get(3).getValidThroughDate().toString()).isEqualTo("2099-12-31");
    }

    private void assertOpeningHours(List<OpeningHour> openingHoursCaptorValue) {
        for (int i=0; i<openingHoursCaptorValue.size(); i++) {
            OpeningHour openingHour = openingHoursCaptorValue.get(i);
            assertThat(openingHour.getDay()).isEqualTo(openingHoursWeekDays.get(i));
            assertThat(openingHour.getOpenTimeHours()).isEqualTo("07");
            assertThat(openingHour.getOpenTimeMinutes()).isEqualTo("00");
            assertThat(openingHour.getCloseTimeHours()).isEqualTo("19");
            assertThat(openingHour.getCloseTimeMinutes()).isEqualTo("00");
        }
    }

    private static FacilityAlternateCode buildFacilityAlternateCode(String facilityId, String code, String codeType) {
        return FacilityAlternateCode.builder()
                                    .facilityId(facilityId)
                                    .code(code)
                                    .codeType(codeType)
                                    .build();
    }
}