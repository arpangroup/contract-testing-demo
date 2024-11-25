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
import com.arpan.__kafka_json_producer.util.AvroUtil;
import com.arpangroup.model.Student;
import com.example.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Provider("kafka-notification-provider")
@Consumer("kafka-notification-consumer")
@PactBroker(
        url = "${pact.broker.url:https://arpangroup.pactflow.io}",
        authentication = @PactBrokerAuth(token = "${pact.broker.token}")
)
@IgnoreNoPactsToVerify
public class KafkaNotificationContractTest {

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
    @PactVerifyProvider("valid Notification message from kafka producer") // description
    @State("given a valid Notification message from kafka producer")
    public MessageAndMetadata verifyStudentEventMessage() {
        Map<String, Object> metaData = Map.of("contentType", "application/json");
        try {
            // Create the Avro NotificationRequest object
            NotificationRequest notificationRequest = NotificationRequest.newBuilder()
                    .setRequestContext(RequestContext.newBuilder()
                            .setEventName("SampleEvent")
                            .setNotificationIdentifier("123")
                            .setProductCode("SampleProductCode")
                            .setBrandName("SampleBrand")
                            .setSideCode("SampleSideCode")
                            .setRecipientList(List.of(
                                    Recipient.newBuilder()
                                            .setRecipientIdentifier("recipient1")
                                            .setPrefferedLanguage("ENG")
                                            .setDeliveryMap(DeliveryMap.newBuilder()
                                                    // Add Email or SMS as per the test case
                                                    .setEmail(Email.newBuilder()
                                                            .setEmailAddressText("example@example.com")
                                                            .build())  // You can remove this or set SMS instead
                                                    .setSms(SMS.newBuilder()
                                                            .setPhoneNumber("9876543210")
                                                            .setSmsIdentifier("1234567")
                                                            .build())  // You can remove this or set Email instead
                                                    .build())
                                            .build()
                            ))
                            .setRecipientTypeName("ECI")
                            .setSourceName("SampleSource")
                            .setSubProductCode("SampleSubProductCode")
                            .build())
                    .setRequestAttributeMap(Map.of(
                            "attributeKey1", "attributeValue1",
                            "attributeKey2", "attributeValue2"
                    ))
                    .build();



            // Serialize Avro object to JSON
            byte[] jsonBytes = AvroUtil.serializeToJson(notificationRequest, NotificationRequest.getClassSchema());
            System.out.println("Serialized JSON: " + new String(jsonBytes));

            // Transform the JSON to match the Pact contract
            String transformedJson = AvroUtil.transformJson(new String(jsonBytes));

            return new MessageAndMetadata(jsonBytes, metaData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize NotificationRequest object", e);
        }
    }


}
