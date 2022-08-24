package com.maersk.referencedata.locationsconsumer.utils;

import com.maersk.facility.smds.operations.msk.facilityMessage;
import com.maersk.geography.smds.operations.msk.geographyMessage;
import org.apache.avro.Schema;
import org.apache.kafka.common.TopicPartition;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TestUtils {

    public static final String AVRO_SCHEMA_FACILITY = "avro/OpsFacility_AvroSchema.avsc";
    public static final String AVRO_SCHEMA_LOCATIONS = "avro/Geography_AvroSchema.avsc";

    public static String readFileToString(String filename) throws IOException {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(filename)), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    public static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = TestUtils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            return new File(resource.toURI());
        }

    }

    public static facilityMessage getFacilityValueSchema(String jsonPayload) throws URISyntaxException, IOException {

        byte[] avroMessageBytes = getAvroBytes(jsonPayload, AVRO_SCHEMA_FACILITY);

        //Use the avroMessageBytes converter to convert to Avro
        AvroDeserializer<facilityMessage> deserializer
                = new AvroDeserializer<>(facilityMessage.class);

        //get the input Object from deserializer
        return deserializer.deserialize("test", avroMessageBytes);
    }

    public static geographyMessage getGeographyValueSchema(String jsonPayload) throws URISyntaxException, IOException {

        byte[] avroMessageBytes = getAvroBytes(jsonPayload, AVRO_SCHEMA_LOCATIONS);

        //Use the avroMessageBytes converter to convert to Avro
        AvroDeserializer<geographyMessage> deserializer
                = new AvroDeserializer<>(geographyMessage.class);

        //get the input Object from deserializer
        return deserializer.deserialize("test", avroMessageBytes);
    }

    public static ReceiverOffset getReceiverOffset(String topicName) {
        return new ReceiverOffset() {
            @Override
            public TopicPartition topicPartition() {
                return new TopicPartition(topicName, 0);
            }

            @Override
            public long offset() {
                return 0;
            }

            @Override
            public void acknowledge() {
                // Nothing to override.
            }

            @Override
            public Mono<Void> commit() {
                return null;
            }
        };
    }

    private static byte[] getAvroBytes(String jsonPayload, String fileName) throws IOException, URISyntaxException {
        //Get and read the avro schema
        Schema schema = new Schema.Parser().parse(getFileFromResource(fileName));

        //Use the json avro converter to convert to Avro
        JsonAvroConverter converter = new JsonAvroConverter();
        return converter.convertToAvro(jsonPayload.getBytes(), schema);

    }
}
