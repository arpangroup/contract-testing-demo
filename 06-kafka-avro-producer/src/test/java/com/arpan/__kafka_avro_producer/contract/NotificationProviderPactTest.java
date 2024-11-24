//package com.arpan.__kafka_avro_producer.contract;
//
//import au.com.dius.pact.provider.PactVerifyProvider;
//import au.com.dius.pact.provider.junit5.HttpTestTarget;
//import au.com.dius.pact.provider.junit5.PactVerificationContext;
//import au.com.dius.pact.provider.junit5.PactVerificationExtension;
//import au.com.dius.pact.provider.junitsupport.Provider;
//import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
//import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
//import au.com.dius.pact.provider.junitsupport.loader.PactUrl;
//import org.apache.avro.Schema;
//import org.apache.avro.generic.GenericData;
//import org.apache.avro.generic.GenericRecord;
//import org.apache.avro.io.BinaryEncoder;
//import org.apache.avro.io.EncoderFactory;
//import org.apache.avro.specific.SpecificDatumWriter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.TestTemplate;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.util.ResourceUtils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.util.Base64;
//
//@Provider("notification-provider")
//@PactTestFor(providerName = "notification-provider")
//@PactFolder("target/pacts") // Alternatively, use @PactBroker to fetch from a broker
//public class NotificationProviderPactTest {
//
//    private static final String AVRO_SCHEMA_PATH = "src/main/resources/avro/notificationEvent.avsc";
//
//    @BeforeEach
//    void setup(PactVerificationContext context) {
//        context.setTarget(new HttpTestTarget("localhost", 8080)); // Replace with your actual provider's URL
//    }
//
//    @ProviderState("a notification event in Avro format")
//    public void notificationEventState() {
//        // Configure mock data or setup your provider for this state
//    }
//
//    @TestTemplate
//    @ExtendWith(PactVerificationExtension.class)
//    void verifyPact(PactVerificationContext context) {
//        context.verifyInteraction();
//    }
//
//    @PactVerifyProvider("a notification event in Avro format")
//    public String generateNotificationEvent() throws Exception {
//        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
//
//        GenericRecord record = new GenericData.Record(schema);
//        record.put("requestContext", getSampleRequestContext());
//        record.put("requestAttributeMap", Map.of("key1", "value1"));
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        SpecificDatumWriter<GenericRecord> writer = new SpecificDatumWriter<>(schema);
//        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
//        writer.write(record, encoder);
//        encoder.flush();
//
//        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
//    }
//
//    private GenericRecord getSampleRequestContext() throws Exception {
//        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
//        Schema requestContextSchema = schema.getField("requestContext").schema();
//
//        GenericRecord requestContext = new GenericData.Record(requestContextSchema);
//        requestContext.put("notificationIdentifier", "notif-12345");
//        requestContext.put("eventName", "Test Event");
//        requestContext.put("sourceName", "SourceName");
//        requestContext.put("recipientList", List.of(getSampleRecipient()));
//
//        return requestContext;
//    }
//
//    private GenericRecord getSampleRecipient() throws Exception {
//        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
//        Schema recipientSchema = schema.getField("requestContext").schema().getField("recipientList").schema().getElementType();
//
//        GenericRecord recipient = new GenericData.Record(recipientSchema);
//        recipient.put("recipientIdentifier", "rec-12345");
//        recipient.put("deliveryMap", getSampleDeliveryMap());
//        recipient.put("prefferedLanguage", "en");
//
//        return recipient;
//    }
//
//    private GenericRecord getSampleDeliveryMap() throws Exception {
//        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
//        Schema deliveryMapSchema = schema.getField("requestContext").schema().getField("recipientList").schema().getElementType().getField("deliveryMap").schema();
//
//        GenericRecord deliveryMap = new GenericData.Record(deliveryMapSchema);
//        deliveryMap.put("email", getSampleEmailDelivery());
//        deliveryMap.put("sms", getSampleSmsDelivery());
//
//        return deliveryMap;
//    }
//
//    private GenericRecord getSampleEmailDelivery() throws Exception {
//        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
//        Schema emailSchema = schema.getField("requestContext").schema().getField("recipientList").schema().getElementType().getField("deliveryMap").schema().getField("email").schema();
//
//        GenericRecord emailDelivery = new GenericData.Record(emailSchema);
//        emailDelivery.put("emailAddressText", "test@example.com");
//
//        return emailDelivery;
//    }
//
//    private GenericRecord getSampleSmsDelivery() throws Exception {
//        Schema schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
//        Schema smsSchema = schema.getField("requestContext").schema().getField("recipientList").schema().getElementType().getField("deliveryMap").schema().getField("sms").schema();
//
//        GenericRecord smsDelivery = new GenericData.Record(smsSchema);
//        smsDelivery.put("smsIdentifier", "sms-12345");
//        smsDelivery.put("phoneNumber", "+1234567890");
//
//        return smsDelivery;
//    }
//}
