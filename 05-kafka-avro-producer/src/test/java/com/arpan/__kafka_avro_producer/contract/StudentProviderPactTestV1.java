package com.arpan.__kafka_avro_producer.contract;

import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpan.__kafka_avro_producer.util.AvroUtils;
import com.arpan.__kafka_avro_producer.utils.AvroUtil;
import com.arpangroup.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@Provider("student-provider")
@PactBroker(url = "http://localhost:9292")
//@PactFolder("src/test/resources/pacts/")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Slf4j
public class StudentProviderPactTestV1 {
    private static final String AVRO_SCHEMA_PATH = "src/main/resources/avro/student.avsc";
    private Schema schema;

    @BeforeEach
    void setup(PactVerificationContext context) throws IOException {
        context.setTarget(new MessageTestTarget());
        schema = new Schema.Parser().parse(new File(AVRO_SCHEMA_PATH));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    // @PactVerifyProvider("a student contract in Avro format") is used to define the provider method
    // that returns the message contents for the specified interaction.
    // The provideStudentMessage() method creates the Avro message, serializes it, and returns the Base64 encoded string.
    // The serializeAvroMessage() method serializes the Student object to Avro binary format.
    @PactVerifyProvider("a student contract in Avro format")
    public String provideStudentMessage() throws IOException {
        byte[] avroMessage = AvroUtil.getSampleAvroMessage(AVRO_SCHEMA_PATH);
        return Base64.getEncoder().encodeToString(avroMessage);
    }
}
