package com.arpan.__kafka_avro_consumer.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.arpan.__kafka_avro_consumer.consumer.StudentConsumer;
import com.arpangroup.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(value = {PactConsumerTestExt.class, MockitoExtension.class})
@PactTestFor(providerName = "student-provider", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
public class StudentConsumerPactTestV1 {
    private static final String AVRO_SCHEMA_PATH = "src/main/resources/avro/student.avsc";

    @InjectMocks
    private StudentConsumer studentConsumer;

    @Pact(consumer = "student-consumer")
    public MessagePact studentDetailsPact(MessagePactBuilder builder) throws Exception {
        String avroMessageBase64 = createAvroMessageBase64();

        return builder
                .expectsToReceive("a student contract in Avro format")
                .withMetadata(Map.of("contentType", "application/avro", "encoding", "base64"))
                .withContent(avroMessageBase64)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "studentDetailsPact", providerType = ProviderType.ASYNCH)
    void testStudentDetailsContract(List<Message> messages) throws Exception {
        Student expectedStudent = new Student("John Doe", "S12345", 30);

        /*String payload = new String(messages.get(0).getContents().getValue());
        Student actualStudent = new ObjectMapper().readValue(payload, Student.class);
        assertThat(actualStudent).usingRecursiveComparison().isEqualTo(expectedStudent);*/

        messages.forEach(message -> {
            try {
                assert message.getContents().getValue() != null;
                String avroPayloadBase64 = new String(message.getContents().getValue());
                byte[] avroPayload = Base64.getDecoder().decode(avroPayloadBase64);
                Student actualStudent = deserializeAvroMessage(avroPayload);
                assertThat(actualStudent).usingRecursiveComparison().isEqualTo(expectedStudent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    private String createAvroMessageBase64() throws Exception {
        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));

        GenericRecord record = new GenericData.Record(schema);
        record.put("studentName", "John Doe");
        record.put("studentId", "S12345");
        record.put("age", 30);

        DatumWriter<GenericRecord> writer = new SpecificDatumWriter<>(schema);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        writer.write(record, encoder);
        encoder.flush();

        byte[] avroMessage = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(avroMessage);
    }


    private Student deserializeAvroMessage(byte[] avroPayload) throws Exception {
        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));

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
