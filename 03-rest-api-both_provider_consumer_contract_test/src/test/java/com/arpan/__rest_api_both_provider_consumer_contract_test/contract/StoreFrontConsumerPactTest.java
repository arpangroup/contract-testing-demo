package com.arpan.__rest_api_both_provider_consumer_contract_test.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpan.__rest_api_both_provider_consumer_contract_test.consumer.ProductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "inventory-provider", pactVersion = PactSpecVersion.V4)
public class StoreFrontConsumerPactTest {

    @Pact(consumer = "storefront-consumer")
    public V4Pact createProductDetailsPact(PactDslWithProvider builder) throws IOException {
         File file = ResourceUtils.getFile("src/test/resources/ProductDetailsResponse200.json");
         String jsonBody = new String(Files.readAllBytes(file.toPath()));

        // Call the actual provider's endpoint to get the real response
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8085/api/products/P123", String.class);
//        String jsonBody = response.getBody();


        return builder
                .given("A product with ID P123 is available in the inventory")
                    .uponReceiving("A GET request to fetch the details of product ID P123")
                    .method("GET")
                    //.headers("Accept", "application/json")
                    //.headers(Map.of("Content-Type", "application/json"))
                    .path("/api/products/P123")
                .willRespondWith()
                    .status(200)
                    //.body(LambdaDsl.newJsonBody((body) -> body.stringType("productId", "P123").stringType("productName", "Samsung Mobile").integerType("", 15000)).build())
                    //.body(jsonStr)
                    .body(jsonBody, "application/json")
                .toPact(V4Pact.class);
    }


    @Test
    @PactTestFor(providerName = "inventory-provider")
    void testProductDetailsPact(MockServer mockServer) throws Exception {
        // Step1.1: or define expectedJson like:
        ProductDto expectedProduct = new ProductDto("P123", "Samsung Mobile", 15000);

        // Step2: define the actualJson response
        // In a consumer test, you need to use the mock server URL (provided by Pact)
        // instead of the actual external endpoint URL like (http://myapi.com/api/products/P123)
        // The purpose of a Pact consumer test is to validate the contract
        // using a simulated server rather than making real API
        ResponseEntity<ProductDto> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products/P123", ProductDto.class);
        ProductDto actualProduct = productResponse.getBody();

        // Step3: Validate the response
        assertThat(productResponse.getStatusCodeValue()).isEqualTo(200);
        assertThat(actualProduct).usingRecursiveComparison().isEqualTo(expectedProduct);
    }
}
