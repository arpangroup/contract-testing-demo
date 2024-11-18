package com.arpan.__kafka_avro_producer.utils;

import com.arpangroup.model.Student;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class AvroUtil {
    public static byte[] getSampleAvroMessage(final String avroSchemaPath) throws IOException {
        Schema schema = new Schema.Parser().parse(new File(avroSchemaPath));

        GenericRecord record = new GenericData.Record(schema);
        record.put("studentName", "John Doe");
        record.put("studentId", "S12345");
        record.put("age", 30);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new SpecificDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

        writer.write(record, encoder);
        encoder.flush();

        return outputStream.toByteArray();
    }

    public static String createAvroMessageBase64(final String avroSchemaPath) throws IOException {
        byte[] avroMessage = getSampleAvroMessage(avroSchemaPath);
        return Base64.getEncoder().encodeToString(avroMessage);
    }

    public static Student deserializeAvroMessage(final String avroSchemaPath, byte[] avroPayload) throws Exception {
        Schema schema = new Schema.Parser().parse(new File(avroSchemaPath));

        DatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(avroPayload, null);
        GenericRecord record = reader.read(null, decoder);

        return new Student(
                record.get("studentName").toString(),
                record.get("studentId").toString(),
                (int) record.get("age")
        );
    }
}
