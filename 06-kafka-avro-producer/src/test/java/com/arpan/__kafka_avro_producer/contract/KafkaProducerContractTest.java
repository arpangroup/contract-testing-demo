package com.arpan.__kafka_avro_producer.contract;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Consumer;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpan.__kafka_avro_producer.dto.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@Provider("kafka-student-provider")
@Consumer("kafka-student-consumer")
//@PactFolder()
@IgnoreNoPactsToVerify
public class KafkaProducerContractTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testTemplate(PactVerificationContext context) {
        if (context != null) {
            System.out.println("testTemplate called.....");
            context.verifyInteraction();
        } else {
            System.out.println("PactVerificationContext is null");
        }
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        if (context != null) {
            context.setTarget(new MessageTestTarget(Collections.singletonList(getClass().getPackage().getName())));
        }
    }

    @PactVerifyProvider("given a valid Student message from kafka producer")
    @State("given a valid Student message from kafka producer")
    public MessageAndMetadata verifyStudentEventMessage() {
        try {
            Student student = new Student(1, "tom");

            // Headers and metadata preparation (if applicable)
            Map<String, Object> headers = Map.of("messageKey", "studentEvent", "correlationId", "12345");
            Map<String, Object> metaData = Map.of("contentType", "application/json", "eventSource", "kafkaProducer");

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] studentBytes = objectMapper.writeValueAsBytes(student);

            return new MessageAndMetadata(studentBytes, metaData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Student object", e);
        }
    }
}
