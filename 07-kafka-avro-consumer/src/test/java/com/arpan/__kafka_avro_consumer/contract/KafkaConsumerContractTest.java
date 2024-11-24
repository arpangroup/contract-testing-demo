package com.arpan.__kafka_avro_consumer.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpan.__kafka_avro_consumer.consumer.StudentConsumer;
import com.arpan.__kafka_avro_consumer.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.object.HasToString.hasToString;

@ExtendWith({PactConsumerTestExt.class, MockitoExtension.class, OutputCaptureExtension.class})
@PactTestFor(providerName = "kafka-student-provider", providerType = ProviderType.ASYNCH)
@SpringBootTest
public class KafkaConsumerContractTest {

    StudentConsumer studentEventListener;

    @Pact(provider = "kafka-student-provider", consumer = "kafka-student-consumer")
    public V4Pact createStudentPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.integerType("studentId");
        body.stringType("studentName");

        return builder
                .given("given a valid Student message from kafka producer")
                .expectsToReceive("valid Student message from kafka producer")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createStudentPact")
    public void testValidProductResponseFromKafkaProducer(V4Interaction.AsynchronousMessage asynchronousMessage, CapturedOutput output) throws IOException {
        assertThat(asynchronousMessage).isNotNull();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(asynchronousMessage.getContents().getContents().getValue());

        Student student = objectMapper.readValue(asynchronousMessage.getContents().getContents().getValue(), Student.class);

        /*--- Asserting the message body ---*/
        assertThat(student).hasFieldOrPropertyWithValue("productId", 1);
        //assertThat(product, hasProperty("productId", hasToString("123")));

        /*--- Asserting the Custom Event Header details to Pact MetaData ---*/
        Map<String, Object> metaData = asynchronousMessage.getContents().getMetadata();
        //assertThat(asynchronousMessage.getContents().getMetadata(), hasEntry("applicationId", "1234"));


        Acknowledgment acknowledgment = new Acknowledgment() {
            @Override
            public void acknowledge() {

            }
        };

        ConsumerRecord<String, com.arpangroup.model.Student> consumerRecord = new ConsumerRecord("topic", 1, 1, "key", "value");
        studentEventListener.listen(consumerRecord, acknowledgment);

        assertThat(output.getOut()
                .contains("EVENT=[EVENT_CONSUMER] COMPLETED FOR ECI 12345")
        );
    }
}
