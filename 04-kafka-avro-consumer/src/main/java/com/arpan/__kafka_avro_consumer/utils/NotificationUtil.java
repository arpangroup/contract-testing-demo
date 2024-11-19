package com.arpan.__kafka_avro_consumer.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class NotificationUtil {

    public static byte[] getSampleAvroMessage(final String avroSchemaPath) throws Exception {
        Schema schema = new Schema.Parser().parse(new File(avroSchemaPath));

        GenericRecord notificationRequest = new GenericData.Record(schema);
        notificationRequest.put("requestContext", createRequestContext(schema));
        notificationRequest.put("requestAttributeMap", Map.of("key1", "value1"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new SpecificDatumWriter<>(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

        writer.write(notificationRequest, encoder);
        encoder.flush();

        return outputStream.toByteArray();
    }

    public static String createAvroMessageBase64(final String avroSchemaPath) throws Exception {
        byte[] avroMessage = getSampleAvroMessage(avroSchemaPath);
        return Base64.getEncoder().encodeToString(avroMessage);
    }

    public static NotificationRequest deserializeAvroMessage(final String avroSchemaPath, byte[] avroPayload) throws Exception {
        Schema schema = new Schema.Parser().parse(new File(avroSchemaPath));

        DatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(avroPayload, null);
        GenericRecord record = reader.read(null, decoder);

        return convertGenericRecordToNotificationRequest(record);
    }

    private static GenericRecord createRequestContext(Schema schema) throws Exception {
        Schema requestContextSchema = schema.getField("requestContext").schema();

        GenericRecord requestContext = new GenericData.Record(requestContextSchema);
        requestContext.put("notificationIdentifier", "notif-12345");
        requestContext.put("eventName", "Test Event");
        requestContext.put("sourceName", "SourceName");
        requestContext.put("recipientList", List.of(createRecipient(requestContextSchema)));

        return requestContext;
    }

    private static GenericRecord createRecipient(Schema schema) throws Exception {
        Schema recipientSchema = schema.getField("recipientList").schema().getElementType();

        GenericRecord recipient = new GenericData.Record(recipientSchema);
        recipient.put("recipientIdentifier", "rec-12345");
        recipient.put("deliveryMap", createDeliveryMap(recipientSchema));
        recipient.put("prefferedLanguage", "en");

        return recipient;
    }

    private static GenericRecord createDeliveryMap(Schema schema) throws Exception {
        Schema deliveryMapSchema = schema.getField("deliveryMap").schema();

        GenericRecord deliveryMap = new GenericData.Record(deliveryMapSchema);
        deliveryMap.put("email", createEmailDelivery(deliveryMapSchema));
        deliveryMap.put("sms", createSmsDelivery(deliveryMapSchema));

        return deliveryMap;
    }

    private static GenericRecord createEmailDelivery(Schema schema) throws Exception {
        Schema emailSchema = schema.getField("email").schema();

        GenericRecord emailDelivery = new GenericData.Record(emailSchema);
        emailDelivery.put("emailAddressText", "test@example.com");

        return emailDelivery;
    }

    private static GenericRecord createSmsDelivery(Schema schema) throws Exception {
        Schema smsSchema = schema.getField("sms").schema();

        GenericRecord smsDelivery = new GenericData.Record(smsSchema);
        smsDelivery.put("smsIdentifier", "sms-12345");
        smsDelivery.put("phoneNumber", "+1234567890");

        return smsDelivery;
    }

    private static NotificationRequest convertGenericRecordToNotificationRequest(GenericRecord record) {
        // You can implement this method to convert `GenericRecord` into your `NotificationRequest` POJO
        // If `NotificationRequest` is generated using Avro, you may directly cast `GenericRecord` to `NotificationRequest`
        return (NotificationRequest) record;
    }
}
