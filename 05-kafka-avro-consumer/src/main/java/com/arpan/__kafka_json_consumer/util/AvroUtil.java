package com.arpan.__kafka_json_consumer.util;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroUtil {
    private static final String AVRO_STUDENT_SCHEMA_LOCATION = "avro/student.avsc";

    public static String convertAvroToJson(GenericRecord avroRecord, Schema schema) throws IOException {
        // Step 4: Use Avro's JsonEncoder to serialize to JSON
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(schema, byteArrayOutputStream);

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        datumWriter.write(avroRecord, jsonEncoder);
        jsonEncoder.flush();

        // Return the resulting JSON as a string
        return byteArrayOutputStream.toString("UTF-8");
    }

    public static Schema studentSchema() throws IOException {
        // File schemaFile = new File(AVRO_STUDENT_SCHEMA_LOCATION);
        // Schema schema = new Schema.Parser().parse(schemaFile);
        ClassPathResource resource = new ClassPathResource(AVRO_STUDENT_SCHEMA_LOCATION);
        Schema schema = new Schema.Parser().parse(resource.getInputStream());
        return schema;
    }
}
