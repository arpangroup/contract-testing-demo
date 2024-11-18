package com.arpan.__kafka_avro_producer.util;

import com.arpangroup.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Slf4j
public class AvroHelper {

    public static String serializeToJson(Student student) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GenericDatumWriter<Student> writer = new GenericDatumWriter<>(student.getSchema());
        Encoder jsonEncoder = EncoderFactory.get().jsonEncoder(student.getSchema(), outputStream);
        writer.write(student, jsonEncoder);
        jsonEncoder.flush();
        return outputStream.toString();
    }

    /*public static byte[] serializeToAvro(Student student) throws Exception {
        SpecificDatumWriter<Student> datumWriter = new SpecificDatumWriter<>(Student.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        datumWriter.write(student, encoder);
        encoder.flush();
        outputStream.close();

        byte[] avroData = outputStream.toByteArray();
        log.debug("####Serialized Avro data length: {}", avroData.length);
        log.debug("Serialized Avro data: {}", new String(avroData));
        System.out.println("###AVRO_DATA: "+ new String(avroData));
        return outputStream.toByteArray();
    }*/

    public static byte[] serializeToAvro(Student student) throws IOException {
        // Assuming you have an Avro schema for Student defined in student.avsc
        Schema schema = new Schema.Parser().parse(new File("D:\\java-projects\\spring-kafka-avro-docker\\kafka-avro-producer\\src\\main\\resources\\avro\\student.avsc"));

        // Create Avro GenericRecord and populate it with student data
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("studentName", student.getStudentName());
        avroRecord.put("studentId", student.getStudentId());
        avroRecord.put("age", student.getAge());

        // Serialize to Avro binary format
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(byteArrayOutputStream, null);
        writer.write(avroRecord, encoder);
        encoder.flush();

        return byteArrayOutputStream.toByteArray();
    }
}
