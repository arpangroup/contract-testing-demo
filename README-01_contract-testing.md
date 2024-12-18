## Contract Testing
 - [What is Contract Testing]()
 - [PactFlow Broker]()
 - [Consumer Driven Contract Testing (**CDCT**)]()
   - [CDCT REST Consumer]()
   - [CDCT REST Provider]()
   - [CDCT in action]()
   - [CDCT Kafka with Avro Consumer]()
   - [CDCT Kafka with Avro Producer]()
 - [Bidirectional Contract Testing (**BDCT**)]()
   - [BDCT Consumer]()
   - [BDCT Provider]()
   - [BDCT in action]()
 - [Bidirectional Vs Consumer Driven]()
 - [Success Criteria]()
 - Examples
   - [PactFlow Code Demos](https://docs.pactflow.io/docs/examples)
   - [Example Java Provider - RestAssured](https://docs.pactflow.io/docs/examples/bi-directional/provider/restassured)
   - [Pact Junit 5 Extension](https://docs.pact.io/implementation_guides/jvm/provider/junit5)
   - [Pact junit runner](https://docs.pact.io/implementation_guides/jvm/provider/junit)
 - [**FAQs**]()


## [Selecting the Pacts to verify with Consumer Version Selectors](https://docs.pact.io/implementation_guides/jvm/provider/junit5#using-an-annotated-method-with-a-builder)
````java
@au.com.dius.pact.provider.junitsupport.loader.PactBrokerConsumerVersionSelectors
    public static SelectorBuilder consumerVersionSelectors() {
      // Select Pacts for consumers deployed to production with branch 'FEAT-123' 
      return new SelectorBuilder()
        .environment('production')
        .branch('FEAT-123');
}
````
or
````java
@au.com.dius.pact.provider.junitsupport.loader.PactBrokerConsumerVersionSelectors
public static SelectorBuilder consumerVersionSelectors() {
    // Select Pacts for consumers deployed to production with branch from CI build 
    return new SelectorBuilder()
            .environment('production')
            .branch(System.getenv('BRANCH_NAME'));
}
````


# What is Contract Testing
Contract testing is a methodology for ensuring that two separate systems (such as two REST services) are compatible and communicate with one other. It captures the interactions that are exchanged between each service, storing them in a contract, which then can be used to verify that both parties adhere to it.  

# Types of Contract Testing

- #### Bi-Directional Contract Testing (BDCT)
  - [BDC Consumer Project]()
  - [BDC Provider Project]()
  - [See Video]()

- #### Consumer Driven Contract Testing (CDCT)
  - [CDC Consumer Project]()
  - [CDC Provider Project]()
  - [See Video]()


# How it measured?
Contract testing is assessed by examining the extent of contracts in Pactflow with verified pacts between consumer and provider.
### Reported Metrics:
- Successful Tests
- Active Contracts
### Logic:
- VERIFICATION_RESULT = "success" or "BIDIRECTIONAL_CONTRACTS" = "1"
- Month identified by `"VERIFICATION_TIME"`; if not available, then by `"PACT_CREATE_TIME"`
- **Successful Tests:**
  - Count of verified PACTS for both consumer and providers.
  - `"PRODUCT_LINE"` credited with test if either consumer or provider
  - `"PRODUCT_LINE"` only credited with single test if same for both consumer and provider
- **Active Contracts:**
  - Count of unique `INTEGRATION_ID`


# PactFlow Broker
PactFlow provides a centralize repository to manage contracts used during integration testing between consuming and producing service and applications. The Pact Broker is a **permanently running, externally hosted application with an API and UI that allows you exchange the pacts and verification results that are generated by the Pact tools.**



# Prerequisites:
- Java 17+
- Spring Boot 3.x
- Maven (or Gradle)
- Pact-JVM library: PACT JVM Junit 5 for [Consumer ](https://mvnrepository.com/artifact/au.com.dius.pact.consumer/junit5) | PACT JVM Junit 5 for [producer](https://mvnrepository.com/artifact/au.com.dius.pact.provider/junit5)
- Optional: [Pact Broker Configuration]()

````css
provider-service/
├── src/main/java/com/example/provider/
│   ├── ProviderApplication.java
│   └── controller/
│       └── DataController.java
├── src/test/java/com/example/provider/
│   └── ProviderPactTest.java
├── pom.xml

````

## Setup Dependencies (Maven)

#### [Pact Provider Dependency:](https://mvnrepository.com/artifact/au.com.dius.pact.provider/junit5)
````xml
<!-- Pact Provider Dependency -->
<dependency>
  <groupId>au.com.dius.pact.provider</groupId>
  <artifactId>junit5</artifactId>
  <version>4.6.5</version>
  <scope>test</scope>
</dependency>
````
#### [Pact Consumer Dependency:](https://mvnrepository.com/artifact/au.com.dius.pact.consumer/junit5)
````xml
    <!-- Pact Consumer Dependency -->
    <dependency>
      <groupId>au.com.dius.pact.consumer</groupId>
      <artifactId>junit5</artifactId>
      <version>4.6.5</version>
      <scope>test</scope>
    </dependency>

    <!--Optional: Pact Maven Plugin for Pact Provider Verification-->
    <plugin>
      <groupId>au.com.dius.pact.provider</groupId>
      <artifactId>maven</artifactId>
      <version>4.1.11</version>
      <configuration>
        <pactBrokerUrl>https://localhost:9292</pactBrokerUrl>
        <!--<pactBrokerUrl>https://arpangroup.pactflow.io/</pactBrokerUrl>
        <pactBrokerToken>0CSDSGVBGWMSDSDBCG</pactBrokerToken>-->
      </configuration>
    </plugin>
````


**pactBrokerUrl:** By specifying the pactBrokerUrl, this plugin connects to the Pact Broker to fetch the Pact files and verifies the provider against those contracts.

**Typical Workflow with This Plugin:**
1. **Consumer publishes a Pact:** The consumer (client) generates a Pact file that defines the expected interactions with the provider (API requests/responses).
2. **Pact Broker:** The Pact file is pushed to a Pact Broker (specified by pactBrokerUrl).
3. **Provider verification:** The provider uses the Pact Maven plugin to download the Pact files from the Pact Broker, and the plugin will verify that the provider’s API matches the interactions defined in the Pact file.






# FAQs
### Q1. Why pact contract testing is created on consumer side for CDCT?
Pact contract testing is created on the consumer side because of the consumer-driven contract approach. Here's why this design choice is important:
Pact contract testing is created on the **consumer side** because of the **consumer-driven contract** approach. Here's why this design choice is important:
1. **Consumer Expectations First:** The consumer defines the expectations for how the service (provider) should behave. Since the consumer relies on the provider’s API or service, it’s essential to know exactly what format and behavior to expect. By defining the contract on the consumer side, it reflects the actual needs of the consumer, ensuring that the provider's implementation will meet those needs.
2. **Simplifies Consumer Testing:** When consumers define the contract, they can write tests that validate their expected behavior from the provider without waiting for the provider to be ready. The consumer can mock the provider’s behavior based on the contract, enabling early testing of integration even before the provider's service is fully implemented.
3. **Reduces Provider’s Burden:** The provider doesn’t need to know about all possible consumers beforehand. It only needs to verify that it can fulfill the contract defined by the consumer. This reduces the need for the provider to manage tests for every possible consumer.
4. **Encourages Decoupling:** This approach helps decouple the development process. Consumers can evolve their requirements independently, and the provider can implement changes as long as they still honor the contract. This flexibility allows for parallel development of services with minimal coordination.
5. **Improved Collaboration:** By focusing on the consumer’s needs, Pact contract testing fosters a collaboration where the provider and consumer can align on expectations. It ensures that the contract reflects real use cases and can adapt to evolving consumer requirements.

### Q2. How a provider will verify the Contract if there is thousands of consumer? <br/>Isn't it a headache for provider to verify each consumers contract? 
### Q3. What if a particular consumers contract on providers test? How consumer will notify?
### Q3. How a single provider can verify multiple consumer?
A single provider can verify contracts for multiple consumers by configuring the Pact framework to load and verify the Pact files from different consumers. Here's how it works:

### 1. Pact Files for Each Consumer
Each consumer-provider contract is defined in its own Pact file. For instance:
- storefront-consumer-inventory-provider.json
- mobile-app-consumer-inventory-provider.json

### 2. Provider Configuration for Multiple Consumers
#### Option 1: Using a Pact Broker
If all Pacts are stored in a Pact Broker:
1. Configure the provider to pull all relevant Pacts using @PactBroker.
2. Each Pact will be loaded, and the provider will verify each interaction for each consumer
````java
@PactBroker(
    url = "https://your-pact-broker-url",
    authentication = @PactBrokerAuth(token = "your-broker-token")
)
@Provider("inventory-provider")
public class InventoryProviderPactTest {
    // Tests will verify all consumer Pacts linked to "inventory-provider" in the broker.
}
````
#### Option 2: Using Pact Folder
If the Pacts are stored locally:
1. Use `@PactFolder` to specify a directory containing multiple Pact files
````java
@PactFolder("src/test/resources/pacts")
@Provider("inventory-provider")
public class InventoryProviderPactTest {
    // Tests will verify all Pacts in the specified folder.
}
````

#### Option 3: Using Pact URLs
You can specify multiple Pact URLs explicitly:
````java
@PactUrl(urls = {
    "file:src/test/resources/pacts/storefront-consumer-inventory-provider.json",
    "file:src/test/resources/pacts/mobile-app-consumer-inventory-provider.json"
})
@Provider("inventory-provider")
public class InventoryProviderPactTest {
    // Tests will verify Pacts from both URLs.
}
````

### 3. Separate States for Each Consumer
The provider's state setup methods (@State) must cover all interactions for all consumers. For example:
````java
@State("State of a newly create order")
public void setupForStorefrontConsumer() {
    // Set up state for storefront-consumer
}

@State("State of a product with ID P101 is available in the inventory")
public void setupForMobileAppConsumer() {
    // Set up state for mobile-app-consumer
}

````

### 4. Test Execution
During test execution:
1. The Pact framework fetches or loads all relevant Pacts.
2. For each interaction in each Pact:
    - It sets up the required provider state using the `@State` method.
    - Verifies the response matches the contract.


### 5. Publishing Results
If you're using a Pact Broker, the results for each verification can be published back to the broker:
````java
System.setProperty("pact.verifier.publishResults", "true");
System.setProperty("pact.provider.branch", "main");
````

### 6. Best Practices
- Use descriptive `@State` names to avoid conflicts between consumers.
- Regularly clean up unused Pacts in the broker to ensure tests only run for active consumers.
- Use logging to debug issues for specific consumers if tests fail.

### Q4. How Consumer Detects Provider Changes?
Consumers don't automatically detect provider implementation issues. Instead:
- The provider's verification results are published back to the Pact Broker.
- The Pact Broker flags the contract as "not verified" or "failed" if the provider fails the test.
- Consumers can use tools like Pact CLI, Pact Maven plugin, or CI pipelines to check the status of the contract in the Pact Broker.

For example:
- The consumer can query the broker to see if the latest contract has been successfully verified by the provider.

### Q5. Will Consumer Contract Tests Fail If Provider Tests Fail?
**No, not directly.** Consumer tests only verify the contract from the consumer's perspective, not the provider's implementation.

However:
- If the provider fails to adhere to the contract, the consumer may eventually break at runtime due to unexpected provider behavior.
- To prevent this, set up a pipeline or CI integration where consumer builds are blocked if the provider fails to verify the contract.

Consumers can query the Pact Broker for verification status using tools like Pact CLI or HTTP
````bash
pact-broker can-i-deploy \
  --pacticipant consumer-name \
  --version 1.0.0 \
  --to-environment dev
````

### Recommended Setup
1. Consumer Responsibilities:
   - Generate a contract (Pact file).
   - Publish it to the broker.
   - **Ensure CI validates the contract is verified by the provider before proceeding.**
2. Provider Responsibilities:
    - Fetch and verify contracts from the broker during CI.
    - Publish verification results back to the broker.
3. Shared Workflow:
    - Automate the `can-I-deploy` check in CI to ensure all contracts are verified before deployment.


# References
- [Vendor Code Samples](https://docs.pactflow.io/docs/examples)
