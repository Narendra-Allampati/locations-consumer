package com.maersk.referencedata.locationsconsumer.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
@RequiredArgsConstructor
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    protected final Class<T> targetType;

    @SneakyThrows
    @Override
    public T deserialize(String topic, byte[] bytes) {
        T result = null;

        if (bytes != null) {

            Schema schema = ((Class<? extends SpecificRecordBase>) targetType).getDeclaredConstructor()
                                                                              .newInstance()
                                                                              .getSchema();

            DatumReader<T> datumReader = new SpecificDatumReader<>(schema);

            Decoder decoder = DecoderFactory.get()
                                            .binaryDecoder(bytes, null);
            result = datumReader.read(null, decoder);
            log.info("Deserialized data complete");
        }

        return result;
    }
}