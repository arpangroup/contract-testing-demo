package com.arpan.__kafka_avro_producer.util;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;

public class AvroSerializer {
    public static String serializeToJson(GenericData.Record record, Schema schema) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericData.Record> writer = new GenericDatumWriter<>(schema);
        Encoder jsonEncoder = EncoderFactory.get().jsonEncoder(schema, outputStream);
        writer.write(record, jsonEncoder);
        jsonEncoder.flush();
        return outputStream.toString();
    }
}
