package com.arpan.__kafka_avro_producer.util;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class AvroUtils {
    public static <T extends SpecificRecordBase> String convertAvroToJson(T avroObject) throws Exception {
        if (avroObject == null) {
            throw new IllegalArgumentException("Avro object cannot be null");
        }

        // Prepare output stream and JSON encoder
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<T> writer = new SpecificDatumWriter<>(avroObject.getSchema());
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(avroObject.getSchema(), outputStream);

        // Serialize Avro object to JSON
        writer.write(avroObject, jsonEncoder);
        jsonEncoder.flush();

        // Convert output stream to JSON string
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
