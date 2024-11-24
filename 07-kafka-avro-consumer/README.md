# Step1: Kafka-Avro-Pact Consumer Contract Testing

## Add Dependencies: 
````xml
    <!-- kafka dependencies -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
        
    <!--Avro schema related dependencies-->
    <dependency>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro</artifactId>
        <version>1.11.0</version>
    </dependency>		
    <dependency>
        <groupId>io.confluent</groupId>
        <artifactId>kafka-avro-serializer</artifactId>
        <version>7.5.1</version>
    </dependency>

    <!-- Pact Consumer Dependency -->
    <dependency>
        <groupId>au.com.dius.pact.consumer</groupId>
        <artifactId>junit5</artifactId>
        <version>4.6.5</version>
        <scope>test</scope>
    </dependency>

````

````xml
    <plugin>
        <groupId>au.com.dius.pact.provider</groupId>
        <artifactId>maven</artifactId>
        <version>4.1.11</version>
        <configuration>
            <pactBrokerUrl>http://localhost:9292</pactBrokerUrl>
        </configuration>
    </plugin>


    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M7</version>
        <configuration>
            <useSystemClassLoader>false</useSystemClassLoader>
            <systemPropertyVariables>
                <pact.verifier.publishResults>true</pact.verifier.publishResults>
            </systemPropertyVariables>
        </configuration>
    </plugin>


    <plugin>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro-maven-plugin</artifactId>
        <version>1.11.0</version>
        <executions>
            <execution>
                <id>avro</id>
                <phase>generate-sources</phase>
                <goals>
                    <goal>schema</goal>
                </goals>
                <configuration>
                    <sourceDirectory>${project.basedir}/src/main/resources/avro</sourceDirectory>
                    <!--<outputDirectory>${project.build.directory}/generated-sources/avro</outputDirectory>-->
                    <outputDirectory>${project.basedir}/src/main/java</outputDirectory>
                    <stringType>String</stringType>
                    <!--<imports>
                        <import>${project.basedir}/src/main/resources/avro/order.avsc</import>
                    </imports>
                    <sources>
                        <source>${project.build.directory}/generated-sources/java/</source>
                    </sources>-->
                </configuration>
            </execution>
        </executions>
    </plugin>

````


````xml
<repository>
    <id>confluent</id>
    <url>https://packages.confluent.io/maven/</url>
</repository>
````

#### Step2: Create a Avro Schema (`student.avsc`):
````json
{
  "namespace": "com.arpangroup.model",
  "type": "record",
  "name": "Student",
  "fields": [
    {
      "name": "studentName",
      "type": "string"
    },
    {
      "name": "studentId",
      "type": "string"
    },
    {
      "name": "age",
      "type": "int"
    }
  ]
}
````



## Step3: StudentConsumerPactTest
````java
import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.arpangroup.model.Student;
import com.example.kafka_avro_consumer.consumer.StudentConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@ExtendWith(value = {PactConsumerTestExt.class, MockitoExtension.class})
@PactTestFor(providerName = "student-provider", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
public class StudentConsumerPactTest {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String KEY_CONTENT_TYPE = "contentType";

    @InjectMocks
    private StudentConsumer studentConsumer;

    @Pact(consumer = "student-consumer")
    public MessagePact studentDetailsPact(MessagePactBuilder builder) throws Exception {
        File file = ResourceUtils.getFile("src/test/resources/StudentResponse200.json");
        String content = new String(Files.readAllBytes(file.toPath()));

        /*PactDslJsonBody jsonBody = new PactDslJsonBody()
                .stringType("studentName", "John Doe")
                .stringType("studentId", "S12345")
                .integerType("age", 20);*/

        return builder
                .expectsToReceive("a student contract")
                //.withMetadata(Map.of(JSON_CONTENT_TYPE, KEY_CONTENT_TYPE))
                //.withContent(jsonBody)
                .withContent(content, "application/json")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "studentDetailsPact", providerType = ProviderType.ASYNCH)
    void testStudentDetailsContract(List<Message> messages) throws Exception {
        Student expectedStudent = new Student("John Doe", "S12345", 30);

        /*String payload = new String(messages.get(0).getContents().getValue());
        Student actualStudent = new ObjectMapper().readValue(payload, Student.class);
        assertThat(actualStudent).usingRecursiveComparison().isEqualTo(expectedStudent);*/

        messages.forEach(message -> {
            try {
                assert message.getContents().getValue() != null;
                String payload = new String(message.getContents().getValue()); // Get the payload from the message
                Student actualStudent =  new ObjectMapper().readValue(payload, Student.class);;
                assertThat(actualStudent).usingRecursiveComparison().isEqualTo(expectedStudent); // Compare the JSON nodes (this ignores the order of fields)
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

````
