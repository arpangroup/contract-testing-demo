package com.arpan.__rest_api_provider_app.contract;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.*;
import au.com.dius.pact.provider.junitsupport.loader.*;
import com.arpan.__rest_api_provider_app.controller.ProductRepository;
import com.arpan.__rest_api_provider_app.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

/* https://docs.pact.io/implementation_guides/jvm/provider/spring6 */
/* https://docs.pact.io/implementation_guides/jvm/provider/junit5spring */
/* https://github.com/pact-foundation/pact-plugins?tab=readme-ov-file#background */
/* https://user-images.githubusercontent.com/53900/103729694-1e7e1400-5035-11eb-8d4e-641939791552.png */
/* https://docs.pact.io/implementation_guides/jvm/provider/junit#selecting-the-pacts-to-verify-with-consumer-version-selectors-4314 */
/* https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/ */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("inventory-provider")
@PactBroker(url = "https://arpangroup.pactflow.io",
        authentication = @PactBrokerAuth(token = "pkqBnpXX3u4o5wErioDeXA"),
        consumerVersionSelectors= {
            @VersionSelector(consumer = "storefront-consumer", tag = "main"),
            //@VersionSelector(consumer = "mobile-app-consumer", tag = "STAGING"),
            @VersionSelector(consumer = "mobile-app-consumer", tag = "main")
        },
        providerTags = "DEV"
)
//@PactFolder("src/test/resources/pacts/")
//@PactUrl(urls = "file:C:\\temp\\pacts\\consumerservice-providerservice-pact.json")
//@IgnoreNoPactsToVerify
//@IgnoreMissingStateChange
public class InventoryProviderPactTest {
    @LocalServerPort
    private int port;

    @Autowired
    ProductRepository productRepository;

    @BeforeAll
    public static void setup() {
        System.setProperty("pact.verifier.publishResults", "true");
        System.setProperty("pact.provider.branch", "main");
        System.setProperty("pact.verifier.verbose", "true");
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        /* There are three main test targets you can use: HttpTestTarget, HttpsTestTarget and MessageTestTarget, PluginTestTarget. */
        context.setTarget(new HttpTestTarget("localhost", port));
        // context.setTarget(new MessageTestTarget());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State(value = "State of a product with ID P101 is available in the inventory", action = StateChangeAction.SETUP)
    public void setupProductP101Inventory() {
        // Set up mocked service or database to return the correct response for P101
        productRepository.deleteAll();
        productRepository.saveAll(Arrays.asList(
                new Product("P101", "Test Product 1", 100),
                new Product("P102", "Test Product 2", 200),
                new Product("P103", "Test Product 3", 300),
                new Product("P104", "Test Product 4", 400)
        ));
    }

    @State("State of a product with ID P101 is available in the MobileApp")
    public void setupProductP101MobileApp() {
        // Return a different structure or additional fields for the MobileApp Pact
    }

    @State("State of a newly create order")
    public void setupNewOrderState() {
        System.out.println("Setting up provider state: State of a newly created order");
    }

    /*@PactVerifyProvider("StoreFrontConsumerPactTest interaction to create a new product")
    public String createNewProduct() throws JsonProcessingException {
        // Intentionally returning a response with missing required fields to fail the test
        // e.g., missing "productId" and returning a 400 status instead of the expected 201
        String invalidResponse = "{ \"productName\": \"Product1\", \"price\": \"500\" }";

        System.out.println("Returning invalid product details for failure test: " + invalidResponse); // Debug log

        // This response is missing the price field, which will cause the pact verification to fail
        return invalidResponse;
    }*/
}
