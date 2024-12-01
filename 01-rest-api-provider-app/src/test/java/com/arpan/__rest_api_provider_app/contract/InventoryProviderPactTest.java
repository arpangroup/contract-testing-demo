package com.arpan.__rest_api_provider_app.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.HttpsTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/* https://docs.pact.io/implementation_guides/jvm/provider/spring6 */
/* https://docs.pact.io/implementation_guides/jvm/provider/junit5spring */
/* https://github.com/pact-foundation/pact-plugins?tab=readme-ov-file#background */
/* https://user-images.githubusercontent.com/53900/103729694-1e7e1400-5035-11eb-8d4e-641939791552.png */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("inventory-provider")
@PactBroker(url = "https://arpangroup.pactflow.io", authentication = @PactBrokerAuth(token = "pkqBnpXX3u4o5wErioDeXA"))
//@PactFolder("src/test/resources/pacts/")
//@PactUrl(urls = "file:C:\\temp\\pacts\\consumerservice-providerservice-pact.json")
public class InventoryProviderPactTest {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        /* There are three main test targets you can use: HttpTestTarget, HttpsTestTarget and MessageTestTarget, PluginTestTarget. */
        context.setTarget(new HttpTestTarget("localhost", 8080));
        // context.setTarget(new MessageTestTarget());
    }

    @BeforeAll
    public static void setup() {
        System.setProperty("pact.verifier.publishResults", "true");
        System.setProperty("pact.provider.branch", "main");
    }

    @State("State of a product with ID P101 is available in the inventory")
    public void setupProductP101Inventory() {
        // Set up mocked service or database to return the correct response for P101
    }

    @State("State of a product with ID P101 is available in the Restaurant")
    public void setupProductP101Restaurant() {
        // Return a different structure or additional fields for the Restaurant Pact
    }

    @State("State of a newly create order")
    public void setupNewOrderState() {
        System.out.println("Setting up provider state: State of a newly created order");
    }

//    @PactVerifyProvider("A GET request to fetch the details of product ID P123")
//    public String getProductDetails() throws JsonProcessingException {
//        ProductResponse product = new ProductResponse("P123", "Samsung Mobile", 15000);
//        product.setActive(true);
//        return new ObjectMapper().writeValueAsString(product); // Ensure the provider returns the full response with 'isActive'
//    }


}
