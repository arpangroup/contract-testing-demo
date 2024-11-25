package com.arpan.__kafka_json_producer.util;

import com.arpangroup.model.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    /**
     * Serializes an Avro object into JSON (use Avro's JSONEncoder).
     *
     * @param avroObject The Avro object to serialize.
     * @param schema     The Avro schema of the object.
     * @return JSON representation of the Avro object as a byte array.
     * @throws RuntimeException if serialization fails.
     */
    public static <T extends SpecificRecord> byte[] serializeToJson(T avroObject, Schema schema) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<T> writer = new SpecificDatumWriter<>(schema);
            Encoder encoder = EncoderFactory.get().jsonEncoder(schema, outputStream);
            writer.write(avroObject, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Avro object to JSON", e);
        }
    }

    public static Schema studentSchema() throws IOException {
        // File schemaFile = new File(AVRO_STUDENT_SCHEMA_LOCATION);
        // Schema schema = new Schema.Parser().parse(schemaFile);
        ClassPathResource resource = new ClassPathResource(AVRO_STUDENT_SCHEMA_LOCATION);
        Schema schema = new Schema.Parser().parse(resource.getInputStream());
        return schema;
    }
}
