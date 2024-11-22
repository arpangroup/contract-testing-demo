package com.arpan.__kafka_json_producer.contract;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Consumer;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpangroup.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@ExtendWith(SpringExtension.class)
@Provider("kafka-student-provider")
@Consumer("kafka-student-consumer")
@PactFolder("src/test/resources/pacts")
@IgnoreNoPactsToVerify
public class KafkaProducerContractTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void testTemplate(PactVerificationContext context) {
        if (context != null) {
            System.out.println("testTemplate called.....");
            context.verifyInteraction();
        } else {
            System.out.println("PactVerificationContext is null");
        }
    }

    @BeforeEach
    public void before(PactVerificationContext context) {
        if (context != null) {
            //context.setTarget(new MessageTestTarget(Collections.singletonList(getClass().getPackage().getName())));
            context.setTarget(new MessageTestTarget());
        }
    }

    @PactVerifyProvider("valid Student message from kafka producer") // description
    @State("given a valid Student message from kafka producer")
    public MessageAndMetadata verifyStudentEventMessage() {
        try {
            Student student = new Student(1, "tom");
            Map<String, Object> metaData = Map.of("contentType", "application/json", "eventSource", "kafkaProducer");

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] studentBytes = objectMapper.writeValueAsBytes(student);

            return new MessageAndMetadata(studentBytes, metaData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Student object", e);
        }
    }

    /*@PactVerifyProvider("valid Student message from kafka producer") // description
    @State("given a valid Student message from kafka producer")
    public MessageAndMetadata verifyStudentEventMessage() {
        try {
            Student student = new Student(1, "tom");
            Map<String, Object> metaData = Map.of("contentType", "application/json", "eventSource", "kafkaProducer");

            // Creating an incorrect object
            Map<String, Object> incorrectStudent = Map.of(
                    "studentId", "invalid_id", // Incorrect type: should be an integer
                    "studentName", 123 // Incorrect type: should be a string
            );

            // Serializing the incorrect object into JSON
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] contentBytes = objectMapper.writeValueAsBytes(incorrectStudent);

            return new MessageAndMetadata(contentBytes, metaData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Student object", e);
        }
    }*/
}
