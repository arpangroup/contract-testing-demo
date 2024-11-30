package com.arpan.__kafka_json_consumer.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpan.__kafka_json_consumer.consumer.StudentConsumer;
import com.arpangroup.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.support.Acknowledgment;


import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.object.HasToString.hasToString;

@ExtendWith({PactConsumerTestExt.class, MockitoExtension.class, OutputCaptureExtension.class})
@PactTestFor(providerName = "kafka-student-provider", providerType = ProviderType.ASYNCH)
@SpringBootTest
public class KafkaConsumerContractTest {
    @Autowired
    StudentConsumer studentEventListener;

    @Pact(provider = "kafka-student-provider", consumer = "kafka-student-consumer")
    public V4Pact createStudentPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody()
                .integerType("studentId", 1)
                .stringType("studentName", "John Doe");

        return builder
                .given("given a valid Student message from kafka producer")
                .expectsToReceive("valid Student message from kafka producer")
                .withContent(body)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createStudentPact")
    public void testValidProductResponseFromKafkaProducer(V4Interaction.AsynchronousMessage asynchronousMessage, CapturedOutput output) throws IOException {
        assertThat(asynchronousMessage).isNotNull();
        ObjectMapper objectMapper = new ObjectMapper();

        // Step1: define a expectedJson in string format
        // String expectedJson = "{\"studentId\":\"S123\", \"studentName\":\"tom\", \"age\":30}";
        // Step2: Get response by calling the controller without Autowiring the Controller class
        // String actualJson = new ObjectMapper().writeValueAsString(studentResponse);
        // Step3: Validate the response
        // assertThat(actualJson).isEqualTo(expectedJson);

        // Deserialize the message content
        byte[] messageValue = asynchronousMessage.getContents().getContents().getValue();
        System.out.println(Arrays.toString(messageValue));
        if (messageValue == null) {
            throw new IllegalArgumentException("Message content is null");
        }

        // Deserialize the byte[] into a Student object
        Student actualStudent = objectMapper.readValue(messageValue, Student.class);

        Student expectedStudent = new Student(1, "John Doe");

        // Assert the fields in the Student object
        assertThat(actualStudent).hasFieldOrPropertyWithValue("studentId", 1);
        assertThat(actualStudent).hasFieldOrPropertyWithValue("studentName", "John Doe");
        assertThat(actualStudent).usingRecursiveComparison().isEqualTo(expectedStudent);


        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };

        //ConsumerRecord<String, Student> consumerRecord = new ConsumerRecord("topic", 1, 1, "key", student);
        //studentEventListener.listen(consumerRecord, acknowledgment);

        assertThat(output.getOut()
                .contains("EVENT=[EVENT_CONSUMER] COMPLETED FOR ECI 12345")
        );
    }
}
