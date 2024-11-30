package com.arpan.__rest_api_provider_app.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@Provider("inventory-provider")
//@PactBroker(url = "http://localhost:9292", authentication = @PactBrokerAuth(token = "", username = "john@doe.com", password = "pkqBnpXX3u4o5wErioDeXA"))
@PactFolder("src/test/resources/pacts/")
//@PactUrl(urls = "file:C:\\temp\\pacts\\consumerservice-providerservice-pact.json")
//@SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryProviderPactTest {

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", 8085));
        // context.setTarget(new MessageTestTarget());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("A product with ID P123 is available in the inventory")
    public void productP123IsAvailable() {
        System.out.println("Setting up provider state: A product with ID P123 is available in the inventory");
    }

//    @PactVerifyProvider("A GET request to fetch the details of product ID P123")
//    public String getProductDetails() throws JsonProcessingException {
//        ProductResponse product = new ProductResponse("P123", "Samsung Mobile", 15000);
//        product.setActive(true);
//        return new ObjectMapper().writeValueAsString(product); // Ensure the provider returns the full response with 'isActive'
//    }

}
