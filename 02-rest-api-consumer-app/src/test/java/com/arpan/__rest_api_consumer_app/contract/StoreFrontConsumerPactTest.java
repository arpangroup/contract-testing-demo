package com.arpan.__rest_api_consumer_app.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.Request;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpan.__rest_api_consumer_app.model.DetailProductDto;
import com.arpan.__rest_api_consumer_app.model.ProductCreateRequestDto;
import com.arpan.__rest_api_consumer_app.model.SimpleProductDto;
import com.arpan.__rest_api_consumer_app.service.ProductServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/* https://docs.pact.io/implementation_guides/jvm/provider/junit5spring */
/* https://docs.pact.io/implementation_guides/jvm/consumer/junit5 */
/* https://github.com/pact-foundation/pact-jvm/blob/master/consumer/junit5/src/test/groovy/au/com/dius/pact/consumer/junit5/MultiTest.groovy */
@PactConsumerTest // Alternatively, you can explicitly declare the JUnit extension: @ExtendWith(PactConsumerTestExt.class)
//@PactTestFor(providerName = "inventory-provider", pactVersion = PactSpecVersion.V4) // Link the mock server with the interactions; either on Test class, or on the Test method
public class StoreFrontConsumerPactTest {
    private final ProductCreateRequestDto productCreateRequest = new ProductCreateRequestDto("Product1", 500);
    private final SimpleProductDto expectedProductCreateResponse = new SimpleProductDto("P1234", "Product1", 500);
    private final DetailProductDto expectedProductDetailsResponse = new DetailProductDto("P101", "Product1", 500, true);
    private final String CONTENT_TYPE = "Content-Type";
    private final String APPLICATION_JSON = "application/json.*";
    private final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";

    @Autowired
    ProductServiceClient productServiceClient;

    @BeforeAll
    public static void setup() {
        System.setProperty("pact.consumer.version", "1.0.0");
        System.setProperty("pact.consumer.branch", "feature/develop");
        System.out.println("Consumer Version: " + System.getProperty("pact.consumer.version"));
        System.out.println("Consumer Branch: " + System.getProperty("pact.consumer.branch"));
    }


    @Pact(provider = "inventory-provider", consumer = "storefront-consumer")
    public V4Pact createProductDetailsPact1(PactDslWithProvider builder) throws IOException {
        // Define the expected response body using PactDslJsonBody
        /*PactDslJsonBody jsonBody = new PactDslJsonBody()
                .stringType("studentName", "John Doe")
                .stringType("studentId", "S12345")
                .integerType("age", 20);*/

        // File file = ResourceUtils.getFile("src/test/resources/ProductDetailsResponse200.json");
        // String jsonBody = new String(Files.readAllBytes(file.toPath()));

        return builder
                // First interaction
                .given("State of a product with ID P101 is available in the inventory") // State
                .uponReceiving("StoreFrontConsumerPactTest interaction to fetch the details of product by ID P101") // Interaction
                    .method("GET")
                    .path("/api/products/P101")
                    //.headers("Accept", "application/json")
                    //.headers(Map.of("Content-Type", "application/json"))
                .willRespondWith()
                    .status(200)
                    //.matchHeader("Location", "http(s)?://\\w+:\\d+//some-service/user/\\w{36}$")
                    //.body(LambdaDsl.newJsonBody((body) -> body.stringType("productId", "P123").stringType("productName", "Samsung Mobile").integerType("", 15000)).build())
                    //.body(jsonStr)
                    //.body(jsonBody, "application/json")
                    .body(new PactDslJsonBody()
                                .stringType("productId", "P101")
                                .stringType("productName", "Samsung Mobile")
                                .integerType("price", 15000)
                    )
                // Second interaction
                .given("State of a newly create order") //State
                .uponReceiving("StoreFrontConsumerPactTest interaction to create a new product") // Interaction
                    .method("POST")
                    .path("/api/products")
                    .matchHeader(CONTENT_TYPE, APPLICATION_JSON, APPLICATION_JSON_CHARSET_UTF_8)
                    .body(new ObjectMapper().writeValueAsString(productCreateRequest))
                .willRespondWith()
                    .status(201)
                    .headers(Map.of("Content-Type", "application/json"))
                    //.body(new ObjectMapper().writeValueAsString(expectedProductCreateResponse))
                    .body(new PactDslJsonBody()
                            .stringMatcher("productId", "^P.*", "P12345678") // Match productId starting with 'P'
                            .stringType("productName", "Product1")
                            .integerType("price", 500)
                    )
                .toPact(V4Pact.class);
    }


    @Test
    @PactTestFor(pactMethod = "createProductDetailsPact1", pactVersion = PactSpecVersion.V4) // either on Test class, or on the Test method
    void testProductDetailsPact__for__StoreFront(MockServer mockServer) throws Exception {
        // Step1.1: or define expectedJson like:
        SimpleProductDto expectedProduct = new SimpleProductDto("P101", "Samsung Mobile", 15000);

        // Step2: define the actualJson response
        // In a consumer test, you need to use the mock server URL (provided by Pact)
        // instead of the actual external endpoint URL like (http://myapi.com/api/products/P123)
        // The purpose of a Pact consumer test is to validate the contract
        // using a simulated server rather than making real API
        ResponseEntity<SimpleProductDto> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products/P101", SimpleProductDto.class);
        SimpleProductDto actualProduct = productResponse.getBody();
        // or by calling the actual service class:
        // productServiceClient.setBaseUrl(mockServer.getUrl());
        // ResponseEntity<DetailProductDto> productResponse = productServiceClient.fetchProductById("P101");



        // Step3: Validate the response
        assertThat(productResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productResponse.getStatusCode().value()).isEqualTo(200);

        // validate Headers
        assertThat(productResponse.getHeaders().getContentType().toString()).contains("application/json");

        // validate ResponseBody
        assertThat(actualProduct).usingRecursiveComparison().isEqualTo(expectedProduct);


        // Simulating the POST request & Validate the response
        ResponseEntity<SimpleProductDto> postResponse = new RestTemplate().postForEntity(mockServer.getUrl() + "/api/products", productCreateRequest, SimpleProductDto.class);
        assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
        assertThat(postResponse.getBody().getProductId()).matches("^P.*");
        //assertThat(postResponse.getBody()).usingRecursiveComparison().isEqualTo(expectedProductCreateResponse);
        assertThat(postResponse.getBody())
                .usingRecursiveComparison()
                .ignoringFields("productId") // Ignore productId in the comparison
                .isEqualTo(expectedProductCreateResponse);
    }

    @Pact(provider = "inventory-provider", consumer = "mobile-app-consumer")
    public V4Pact createProductDetailsPact2(PactDslWithProvider builder) throws IOException {
        return builder
                // Second interaction
                .given("State of a product with ID P101 is available in the MobileApp") // State
                .uponReceiving("StoreFrontConsumerPactTest interaction to fetch the details of product by ID P101 for MobileApp") // Interaction
                    .method("GET")
                    .path("/api/products/P101")
                .willRespondWith()
                    .status(200)
                    .headers(Map.of("Content-Type", "application/json"))
                    .body(new ObjectMapper().writeValueAsString(expectedProductDetailsResponse))
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createProductDetailsPact2", pactVersion = PactSpecVersion.V4)
    void testProductDetailsPact__for__MobileApp(MockServer mockServer) throws Exception {
        ResponseEntity<DetailProductDto> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products/P101", DetailProductDto.class);
        DetailProductDto actualProductDetails = productResponse.getBody();

        // Step3: Validate the response
        assertThat(productResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productResponse.getStatusCode().value()).isEqualTo(200);
        // validate Headers
        //assertThat(productResponse.getHeaders().getContentType().toString()).contains("application/json");
        // validate ResponseBody
        assertThat(actualProductDetails).usingRecursiveComparison().isEqualTo(expectedProductDetailsResponse);
    }
}
