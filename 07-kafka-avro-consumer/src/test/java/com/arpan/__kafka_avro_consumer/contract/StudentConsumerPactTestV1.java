//package com.arpan.__kafka_avro_consumer.contract;
//
//import au.com.dius.pact.consumer.MessagePactBuilder;
//import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
//import au.com.dius.pact.consumer.junit5.PactTestFor;
//import au.com.dius.pact.consumer.junit5.ProviderType;
//import au.com.dius.pact.core.model.PactSpecVersion;
//import au.com.dius.pact.core.model.V4Interaction;
//import au.com.dius.pact.core.model.annotations.Pact;
//import au.com.dius.pact.core.model.messaging.Message;
//import au.com.dius.pact.core.model.messaging.MessagePact;
//import com.arpan.__kafka_avro_consumer.consumer.StudentConsumer;
//import com.arpan.__kafka_avro_consumer.utils.AvroUtil;
//import com.arpangroup.model.Student;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.avro.Schema;
//import org.apache.avro.generic.GenericData;
//import org.apache.avro.generic.GenericRecord;
//import org.apache.avro.io.*;
//import org.apache.avro.specific.SpecificDatumReader;
//import org.apache.avro.specific.SpecificDatumWriter;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.system.CapturedOutput;
//import org.springframework.util.ResourceUtils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(value = {PactConsumerTestExt.class, MockitoExtension.class})
//@PactTestFor(providerName = "student-provider", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
//public class StudentConsumerPactTestV1 {
//    private static final String AVRO_SCHEMA_PATH = "src/main/resources/avro/student_demo.avsc";
//
//    @Pact(consumer = "student-consumer")
//    public MessagePact studentDetailsPact(MessagePactBuilder builder) throws Exception {
//        //String avroMessageBase64 = AvroUtil.createAvroMessageBase64(AVRO_SCHEMA_PATH);
//        byte[] sampleAvroMessage = AvroUtil.getSampleAvroMessage(AVRO_SCHEMA_PATH);
//        System.out.println("#####SAMPLE_AVRO_MESSAGE: " + new String(sampleAvroMessage));
//
//        /*return builder
//                .expectsToReceive("a student contract in Avro format")
//                .withMetadata(Map.of("contentType", "application/avro", "encoding", "base64"))
//                .withContent(avroMessageBase64)
//                .toPact();*/
//
//        return builder
//                .expectsToReceive("a student contract in Avro format")
//                .withMetadata(Map.of("contentType", "text/plain", "encoding", "base64"))
//                .withContent(Base64.getEncoder().encodeToString(sampleAvroMessage))
//                .toPact();
//    }
//
//    /*@Test
//    @PactTestFor(pactMethod = "studentDetailsPact", providerType = ProviderType.ASYNCH)
//    void testStudentDetailsContract(List<Message> messages) throws Exception {
//        messages.forEach(message -> {
//            try {
//                // Get the byte array from the message content
//                byte[] avroPayload = message.getContents().getValue();
//                byte[] decodedPayload = Base64.getDecoder().decode(avroPayload);
//
//                // Deserialize the Avro message
//                Student actualStudent = AvroUtil.deserializeAvroMessage(AVRO_SCHEMA_PATH, decodedPayload);
//
//                // Validate using field-level comparison
//                assertThat(actualStudent).isNotNull();
//                assertThat(actualStudent.getStudentName()).isEqualTo("John Doe");
//                assertThat(actualStudent.getStudentId()).isEqualTo("S12345");
//                assertThat(actualStudent.getAge()).isEqualTo(30);
//            }  catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }*/
//
//    @Test
//    @PactTestFor(pactMethod = "studentDetailsPact", providerType = ProviderType.ASYNCH)
//    void testStudentDetailsContract(V4Interaction.AsynchronousMessage asynchronousMessage, CapturedOutput output) throws Exception {
//        assertThat(asynchronousMessage).isNotNull();
//        System.out.println(Arrays.toString(asynchronousMessage.getContents().getContents().getValue()));
//
//        ObjectMapper mapper = new ObjectMapper();
//
//
//
//    }
//}
