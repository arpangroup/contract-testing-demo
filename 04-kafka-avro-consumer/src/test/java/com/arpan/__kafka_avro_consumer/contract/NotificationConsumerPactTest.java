package com.arpan.__kafka_avro_consumer.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.arpan.__kafka_avro_consumer.utils.NotificationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "notification-provider", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
public class NotificationConsumerPactTest {

    private static final String AVRO_SCHEMA_PATH = "src/main/resources/avro/notificationEvent.avsc";

    @Pact(consumer = "notification-consumer")
    public MessagePact createNotificationEventPact(MessagePactBuilder builder) throws Exception {
        byte[] avroMessage = NotificationUtil.getSampleAvroMessage(AVRO_SCHEMA_PATH);

        return builder
                .expectsToReceive("a notification event in Avro format")
                .withMetadata(Map.of("contentType", "text/plain", "encoding", "base64"))
                .withContent(Base64.getEncoder().encodeToString(avroMessage))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createNotificationEventPact")
    void validateNotificationEvent(List<Message> messages) throws Exception {
        messages.forEach(message -> {
            try {
                byte[] decodedPayload = Base64.getDecoder().decode(message.getContents().getValue());
                NotificationRequest actualEvent = NotificationUtil.deserializeAvroMessage(AVRO_SCHEMA_PATH, decodedPayload);

                assertThat(actualEvent).isNotNull();
                assertThat(actualEvent.getRequestContext().getNotificationIdentifier()).isEqualTo("notif-12345");
                assertThat(actualEvent.getRequestContext().getRecipientList().get(0).getDeliveryMap().getEmail().getEmailAddressText())
                        .isEqualTo("test@example.com");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
