package com.maersk.referencedata.locationsconsumer.services;

import com.maersk.geography.smds.operations.msk.geographyMessage;
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateCode;
import com.maersk.referencedata.locationsconsumer.domains.locations.AlternateName;
import com.maersk.referencedata.locationsconsumer.domains.locations.Geography;
import com.maersk.referencedata.locationsconsumer.repositories.locations.AlternateCodeRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.AlternateNameRepository;
import com.maersk.referencedata.locationsconsumer.repositories.locations.GeographyRepository;
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

import static com.maersk.referencedata.locationsconsumer.utils.TestUtils.getGeographyValueSchema;
import static com.maersk.referencedata.locationsconsumer.utils.TestUtils.getReceiverOffset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class LocationsServiceTest {
    private final String LOCATIONS_TOPIC_NAME = "MSK.geography.gda.topic.internal.any.v3";
    private static final String BASE_FILE_PATH_EVENTS = "events/";
    private LocationsService locationsService;
    private ReceiverOffset receiverOffset;

    @Mock
    private KafkaReceiver<String, geographyMessage> mockKafkaReceiver;
    @Mock
    private AlternateCodeRepository alternateCodeRepository;
    @Mock
    private AlternateNameRepository alternateNameRepository;
    @Mock
    private GeographyRepository geographyRepository;

    @Captor
    private ArgumentCaptor<Publisher<Geography>> geographyCaptor;
    @Captor
    private ArgumentCaptor<List<AlternateName>> alternateNamesCaptor;
    @Captor
    private ArgumentCaptor<List<AlternateCode>> alternateCodesCaptor;

    @BeforeEach
    public void setUp() {
        this.locationsService = new LocationsService(mockKafkaReceiver, alternateCodeRepository, alternateNameRepository, geographyRepository);
    }

    @Test
    void insertNewRecordSuccessfully_GeographyExistsInDBBeforeHand() throws IOException, URISyntaxException {

        // given
        String payload = TestUtils.readFileToString(BASE_FILE_PATH_EVENTS + "Locations.v3_Sample-1.json");
        final geographyMessage avroPayload = getGeographyValueSchema(payload);

        List<ReceiverRecord<String, geographyMessage>> geographyReceiverRecords = new ArrayList<>();
        receiverOffset = getReceiverOffset(LOCATIONS_TOPIC_NAME);

        final ReceiverRecord<String, geographyMessage> geographyReceiverRecord = new ReceiverRecord<>(new ConsumerRecord<>(LOCATIONS_TOPIC_NAME, 0, 0L, "avroKey", avroPayload), receiverOffset);

        geographyReceiverRecords.addAll(0, Collections.singleton(geographyReceiverRecord));
        Flux<ReceiverRecord<String, geographyMessage>> mockGeographyReceiverRecords = Flux.fromIterable(geographyReceiverRecords);

        // when
        when(mockKafkaReceiver.receive()).thenReturn(mockGeographyReceiverRecords);
        when(geographyRepository.findById("ONREE2V5EAPWS")).thenReturn(Mono.just(Geography.builder().build()));
        when(geographyRepository.deleteById("ONREE2V5EAPWS")).thenReturn(Mono.empty());
        when(geographyRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(Geography.builder().build()));
        when(alternateNameRepository.saveAll(any(List.class))).thenReturn(Flux.just(AlternateName.builder().build()));
        when(alternateCodeRepository.saveAll(any(List.class))).thenReturn(Flux.just(AlternateCode.builder().build()));

        // then
        locationsService.startKafkaConsumer();

        verify(geographyRepository, times(1)).findById("ONREE2V5EAPWS");
        verify(geographyRepository, times(1)).deleteById("ONREE2V5EAPWS");
        verify(geographyRepository, times(1)).saveAll(geographyCaptor.capture());
        verify(alternateNameRepository, times(1)).saveAll(alternateNamesCaptor.capture());
        verify(alternateCodeRepository, times(1)).saveAll(alternateCodesCaptor.capture());

        assertGeography(geographyCaptor.getValue());
        assertThat(alternateNamesCaptor.getValue()).isEmpty();
        assertAlternateCodes(alternateCodesCaptor.getValue());
    }

    @Test
    void insertNewRecordSuccessfully_GeographyDoesNotExistInDBBeforeHand() throws IOException, URISyntaxException {

        // given
        String payload = TestUtils.readFileToString(BASE_FILE_PATH_EVENTS + "Locations.v3_Sample-1.json");
        final geographyMessage avroPayload = getGeographyValueSchema(payload);

        List<ReceiverRecord<String, geographyMessage>> geographyReceiverRecords = new ArrayList<>();
        receiverOffset = getReceiverOffset(LOCATIONS_TOPIC_NAME);

        final ReceiverRecord<String, geographyMessage> geographyReceiverRecord = new ReceiverRecord<>(new ConsumerRecord<>(LOCATIONS_TOPIC_NAME, 0, 0L, "avroKey", avroPayload), receiverOffset);

        geographyReceiverRecords.addAll(0, Collections.singleton(geographyReceiverRecord));
        Flux<ReceiverRecord<String, geographyMessage>> mockGeographyReceiverRecords = Flux.fromIterable(geographyReceiverRecords);

        // when
        when(mockKafkaReceiver.receive()).thenReturn(mockGeographyReceiverRecords);
        when(geographyRepository.findById("ONREE2V5EAPWS")).thenReturn(Mono.empty());
        when(geographyRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(Geography.builder().build()));
        when(alternateNameRepository.saveAll(any(List.class))).thenReturn(Flux.just(AlternateName.builder().build()));
        when(alternateCodeRepository.saveAll(any(List.class))).thenReturn(Flux.just(AlternateCode.builder().build()));

        // then
        locationsService.startKafkaConsumer();

        verify(geographyRepository, times(1)).findById("ONREE2V5EAPWS");
        verify(geographyRepository, times(1)).saveAll(geographyCaptor.capture());
        verify(alternateNameRepository, times(1)).saveAll(alternateNamesCaptor.capture());
        verify(alternateCodeRepository, times(1)).saveAll(alternateCodesCaptor.capture());

        assertGeography(geographyCaptor.getValue());
        assertThat(alternateNamesCaptor.getValue()).isEmpty();
        assertAlternateCodes(alternateCodesCaptor.getValue());
    }

    private void assertAlternateCodes(List<AlternateCode> alternateCodesCaptorValue) {
        assertThat(alternateCodesCaptorValue.get(0).getCodeType()).isEqualTo("GEOID");
        assertThat(alternateCodesCaptorValue.get(0).getCode()).isEqualTo("ONREE2V5EAPWS");
        assertThat(alternateCodesCaptorValue.get(1).getCodeType()).isEqualTo("CONTINENT CODE");
        assertThat(alternateCodesCaptorValue.get(1).getCode()).isEqualTo("AUS");
    }

    private void assertAlternateNames(List<AlternateName> alternateNamesCaptorValue) {

    }

    private void assertGeography(Publisher<Geography> geographyCaptorValue) {

    }
}
