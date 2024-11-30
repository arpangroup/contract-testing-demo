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
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpan.__kafka_json_producer.util.AvroUtil;
import com.arpangroup.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Provider("kafka-student-provider")
@Consumer("kafka-student-consumer")
@PactBroker(
        url = "${pact.broker.url:https://arpangroup.pactflow.io}",
        authentication = @PactBrokerAuth(token = "${pact.broker.token}")
)
@IgnoreNoPactsToVerify
public class KafkaProducerContractTest {

    @BeforeAll
    public static void setUp() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        }
        System.setProperty("pact.broker.url", properties.getProperty("pact.broker.url"));
        System.setProperty("pact.broker.token", properties.getProperty("pact.broker.token"));
        System.setProperty("pact.verifier.publishResults", "true");
        System.setProperty("pact.provider.branch", "main");
    }

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

    /**
     * This method is used to verify that the Kafka producer sends a valid "Student" message
     * in the expected JSON format, based on the Pact contract.
     *
     * It creates a sample "Student" Avro object, serializes it into JSON, and returns the
     * serialized message along with metadata (such as content type) to be used in Pact verification.
     *
     * The Pact verifier will compare the serialized message and metadata against the expected
     * contract to ensure the producer's behavior matches the contract defined by the consumer.
     *
     * @return MessageAndMetadata The serialized "Student" message and associated metadata for verification.
     * @throws RuntimeException if there is a failure during the serialization of the Student object.
     */
    @PactVerifyProvider("valid Student message from kafka producer") // description
    @State("given a valid Student message from kafka producer")
    public MessageAndMetadata verifyStudentEventMessage() {
        try {
            // Create the Avro `Student` object
            Student student = Student.newBuilder()
                    .setStudentId(2)
                    .setStudentName("John Doe")
                    .build();
            Map<String, Object> metaData = Map.of("contentType", "application/json");

            // Serialize Avro object to JSON
            byte[] jsonBytes = AvroUtil.serializeToJson(student, Student.getClassSchema());
            System.out.println("JSON_BYTES: " + new String(jsonBytes));

            return new MessageAndMetadata(jsonBytes, metaData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Student object", e);
        }
    }

   /* *//**
     * This test ensures that the Avro schema used by the provider matches the expected schema.
     * If the schema of the Student class changes, the test will fail.
     *//*
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void validateStudentSchema() {
        try {
            // Get the schema from the Student class (expected schema)
            Schema expectedSchema = Student.getClassSchema();

            // Get the schema used to serialize the object (actual schema)
            Student student = Student.newBuilder()
                    .setStudentId(1)
                    .setStudentName("John Doe")
                    .build();
            Schema actualSchema = student.getSchema();

            // Assert that the schemas are equal, failing the test if they differ
            if (!expectedSchema.equals(actualSchema)) {
                throw new RuntimeException("Schema mismatch: The actual Avro schema does not match the expected schema.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate Avro schema", e);
        }
    }*/
}
