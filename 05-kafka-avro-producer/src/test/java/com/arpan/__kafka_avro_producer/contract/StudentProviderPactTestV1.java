package com.arpan.__kafka_avro_producer.contract;

import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpan.__kafka_avro_producer.util.AvroUtils;
import com.arpangroup.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@Provider("student-provider")
@PactBroker(url = "http://localhost:9292")
//@PactFolder("src/test/resources/pacts/")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Slf4j
public class StudentProviderPactTestV1 {
    private static final String AVRO_SCHEMA_PATH = "src/main/resources/avro/student.avsc";
    private Schema schema;

    @BeforeEach
    void setup(PactVerificationContext context) throws IOException {
        System.out.println("Setting up the provider test context...");
        // context.setTarget(new HttpTestTarget("localhost", 8080));
        context.setTarget(new MessageTestTarget());
        schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
    }



    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("a student contract in Avro format")
    public void prepareStudentMessage() {
        String base64Content = "EEpvaG4gRG9lDFMxMjM0NTw="; // from your Pact file
        byte[] avroPayload = Base64.getDecoder().decode(base64Content);

        // Deserialize the Avro message
        Student actualStudent = deserializeAvroMessage(avroPayload);

        // Expected student object
        Student expectedStudent = new Student("John Doe", "S12345", 30);

        // Validate the content
        assertThat(actualStudent).usingRecursiveComparison().isEqualTo(expectedStudent);
    }


    @PactVerifyProvider("a student contract in Avro format")
    public String provideStudentMessage() {
        // Create the expected Avro message
        Student student = new Student("John Doe", "S12345", 30);
        byte[] avroMessage = serializeAvroMessage(student);

        // Return the Base64 encoded message as expected in the contract
        return Base64.getEncoder().encodeToString(avroMessage);
    }

    private Student deserializeAvroMessage(byte[] avroPayload) {
        try {
            DatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(avroPayload, null);
            GenericRecord record = reader.read(null, decoder);

            return new Student(
                    record.get("studentName").toString(),
                    record.get("studentId").toString(),
                    (int) record.get("age")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Avro message", e);
        }
    }

    private byte[] serializeAvroMessage(Student student) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<GenericRecord> writer = new SpecificDatumWriter<>(schema);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            // Create a GenericRecord for serialization
            GenericRecord record = new GenericData.Record(schema);
            record.put("studentName", student.getStudentName());
            record.put("studentId", student.getStudentId());
            record.put("age", student.getAge());

            writer.write(record, encoder);
            encoder.flush();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Avro message", e);
        }
    }

}
