# CDCT Consumer Contract Testing
https://docs.pact.io/implementation_guides/jvm/consumer/junit5
https://docs.pact.io/implementation_guides/jvm/provider/junit5
https://github.com/pact-foundation/pact-jvm/blob/master/consumer/junit5/src/test/groovy/au/com/dius/pact/consumer/junit5/MultiTest.groovy



## Project Structure
````css
cdct-rest-consumer-app/
├── src/main/java/com/arpan/consumer/
│   ├── CdctRestConsumerApplication.java
│   └── controller/
│       └── DataController.java
├── src/test/java/com/arpan/consumer/
│   ├── CdctRestConsumerApplicationTests.java
│   └── contract/
│       └── ConsumerContractTest.java
├── pom.xml
````

## Provider Endpoint & Response
Lets mock a providers API response, we are not consuming the whole response, instead we will consume a partial response
````shell
curl GET http://localhost:8080/api/products/P101 | python -m json.tool
````
````json
{
    "productId": "P101",
    "productName": "Product1",
    "price": 500,
    "productType": "DEFAULT",
    "version": "V-01",
    "active": true
}
````


## Step1. Setup Dependencies (Maven)

### [Pact Consumer Dependency:](https://mvnrepository.com/artifact/au.com.dius.pact.consumer/junit5)
````xml
    <!-- Pact Consumer Dependency -->
    <dependency>
      <groupId>au.com.dius.pact.consumer</groupId>
      <artifactId>junit5</artifactId>
      <version>4.6.5</version>
      <scope>test</scope>
    </dependency>

    <!--Optional: Pact Maven Plugin to upload the PACT to PactFlow-->
    <plugin>
      <groupId>au.com.dius.pact.provider</groupId>
      <artifactId>maven</artifactId>
      <version>4.1.11</version>
      <configuration>
        <pactBrokerUrl>https://arpangroup.pactflow.io/</pactBrokerUrl>
        <pactBrokerToken>0CSDSGVBGWMSDSDBCG</pactBrokerToken>
      </configuration>
    </plugin>
````


## Step2. Consumer Test (StoreFrontConsumerPactTest)
Now in our example (`StoreFrontConsumerPactTest`) we will consume only  `{productId, productName, price}`. We will ignore other fields during `PACT` creation.
````java
/* https://docs.pact.io/implementation_guides/jvm/provider/junit5spring */
/* https://docs.pact.io/implementation_guides/jvm/consumer/junit5 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "inventory-provider")
//@PactTestFor(providerName = "student-provider", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V4, port = 8081)
class StoreFrontConsumerPactTest {

    @Pact(consumer = "ConsumerService")
    public V4Pact createStudentDetailsPact(PactDslWithProvider builder) throws Exception{
        // Define the expected response body using PactDslJsonBody
        /*PactDslJsonBody jsonBody = new PactDslJsonBody()
            .stringType("studentName", "John Doe")
            .stringType("studentId", "S12345")
            .integerType("age", 20);*/
        
        // or, define the expected response from a json file (suitable for big json response)
        File file = ResourceUtils.getFile("src/test/resources/UserResponse200.json");
        String jsonBody = new String(Files.readAllBytes(file.toPath()));       
        
        // Build the Pact interaction
        return builder
              .given("GET /api/getUserDetails")
                .uponReceiving("A GET request for user details")
                .method("GET")
                //.headers("Accept", "application/json")
                //.headers(Map.of("Content-Type", "application/json"))
                .path("/api/getUserDetails")
              .willRespondWith()
                .status(200)
                //.body(LambdaDsl.newJsonBody((body) -> body.stringType("name", "Sample Data")).build())
                //.body(jsonBody)
                .body(jsonBody, "application/json")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(providerName = "createStudentDetailsPact")
    void testProductDetailsPact(MockServer mockServer) throws Exception {
        // Step1: define a expectedJson in string format
        // String expectedJson = "{\"studentId\":\"S123\", \"studentName\":\"tom\", \"age\":30}";
      
        // Step1.1: or define expectedJson like:
        StudentResponse expectedStudent = new StudentResponse("S123", "tom", 30);
      
        
        // Step2: define the actualJson response using an external endpoint
        /*RestTemplate restTemplate = new RestTemplate();        
        ResponseEntity<String> actualJson = restTemplate.getForEntity("http://localhost:8080/api/data", String.class);*/

        // Step2.1: or get response by calling the controller from the same application (when the provider logic is written on same application)
        /*studentController.setBaseUrl(mockServer.getUrl() + "/api/getStudentDetails");
        StudentResponse studentResponse = studentController.getStudentDetails("123");
        String actualJson = new ObjectMapper().writeValueAsString(studentResponse);*/

        // Step2.2: or get response by calling the controller without Autowiring the Controller class
        ResponseEntity<StudentResponse> studentResponse = restTemplate.getForEntity(mockServer.getUrl() + "/api/getUserDetails", StudentResponse.class);
        StudentResponse expectedStudent = studentResponse.getBody();              
      
        
        // Step3: Validate the response
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("Sample Data");
    }
}
````

### Explanation:
1. **@ExtendWith:** ExtendWith is JUnit 5 annotation that is used to register extensions in JUnit tests. It facilitates the Pact features to the test class file to perform the contract testing
2. **@PactTestFor:** annotation is used to specify the provider against which the interactions defined in the test should be verified. The name below “student-provider” has to be mentioned the same as Provider and this binds the Provider and Consumer. <br/> You can either put this annotation on the test class, or on the test method
    1. **providerType = ProviderType.ASYNCH:**
    2. **pactVersion = PactSpecVersion.V4:**
    3. **port = 8081:** Ensure that the provider service is running on the correct port (80801in your case).
3. **V4Pact:** The createPact method returns `V4Pact` (others are `RequestResponsePact`, `RequestResponsePact`, `MessagePact`) as we defined `pactVersion = V4`
4. **.toPact(V4Pact.class):** This explicitly creates a `V4Pact` instance.
5. **@Pact:** annotation is used to mark the method that generates a Pact between the consumer and provider
6. **PactDslWithProvider:** object is automatically injected by Pact and it provides a fluent DSL for constructing a Pact between the consumer and provider.
    1. Other available Builders are: [PactBuilder](), [MessagePactBuilder]() <br/><br/>
7. **PactTestFor:** It indicates that this test is associated with the interactions defined in the above interactions (or Mock Response)
8. **MockServer:** object is automatically injected by Pact and it provides the URL where the mock server is running
    1. **setBaseURL:** Override the host URL so that the API request is redirected to the Pact mock server
9. ssssas
10. sasasa




### Here is an example of create pact using `PactBuilder`
````java
@Pact(provider = "ProviderService", consumer = "ConsumerService")
public V4Pact createPact(PactBuilder builder) {
    return builder
        .usingLegacyMessageDsl(false)
        .expectsToReceive("GET REQUEST")
        .withRequest(request -> request
                .path("/api/pact")
                .method("GET"))
        .willRespondWith(response -> response
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .booleanType("condition", true)
                        .stringType("name", "tom")))
        .toPact();
  }
````

### Here is an example of create pact using `MessagePactBuilder`
````java
@Pact(consumer = "student-consumer")
public MessagePact studentDetailsPact(MessagePactBuilder builder) throws Exception {
  File file = ResourceUtils.getFile("src/test/resources/StudentResponse200.json");
  String content = new String(Files.readAllBytes(file.toPath()));
  
  return builder
          .expectsToReceive("a student contract")
          //.withMetadata(Map.of(JSON_CONTENT_TYPE, KEY_CONTENT_TYPE))
          //.withContent(jsonBody)
          .withContent(content, "application/json")
          .toPact();
}
````

