//package com.arpan.__rest_api_consumer_app.contract;
//
//import au.com.dius.pact.consumer.MockServer;
//import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
//import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
//import au.com.dius.pact.consumer.junit5.PactConsumerTest;
//import au.com.dius.pact.consumer.junit5.PactTestFor;
//import au.com.dius.pact.core.model.PactSpecVersion;
//import au.com.dius.pact.core.model.RequestResponsePact;
//import au.com.dius.pact.core.model.V4Pact;
//import au.com.dius.pact.core.model.annotations.Pact;
//import com.arpan.__rest_api_consumer_app.model.SimpleProductDto;
//import com.arpan.__rest_api_consumer_app.service.ProductServiceClient;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
//
//@PactConsumerTest
//@PactTestFor(providerName = "inventory-provider")
//public class ProductServiceClientPactTest {
//    @Autowired
//    ProductServiceClient productServiceClient;
//
//    @Pact(consumer = "storefront-consumer")
//    public RequestResponsePact allProducts(PactDslWithProvider builder) {
//        return builder
//                .given("products exists")
//                    .uponReceiving("get all products")
//                    .path("/api/products")
//                .willRespondWith()
//                    .status(200)
//                    .body(new PactDslJsonBody()
//                        .minArrayLike("products", 1, 2)
//                            .integerType("id", 9L)
//                            .stringType("name", "Gem Visa")
//                            .stringType("type", "CREDIT_CARD")
//                            .closeObject()
//                        .closeArray()
//                )
//                .toPact();
//    }
//
//    @Test
//    @PactTestFor(pactMethod = "allProducts", pactVersion = PactSpecVersion.V3)
//    void testAllProducts(MockServer mockServer) {
//        productServiceClient.setBaseUrl(mockServer.getUrl());
//        List<SimpleProductDto> products = productServiceClient.fetchProducts();
//        //assertThat(products, hasSize(2));
//        //assertThat(products.get(0), is(equalTo(new Product(9L, "Gem Visa", "CREDIT_CARD", null, null))));
//    }
//
//    @Pact(consumer = "storefront-consumer")
//    public V4Pact singleProduct(PactDslWithProvider builder) {
//        return builder
//                .given("product with ID 10 exists", "id", 10)
//                .uponReceiving("get product with ID 10")
//                    .path("/products/10")
//                .willRespondWith()
//                    .status(200)
//                    .body(new PactDslJsonBody()
//                        .integerType("id", 10L)
//                        .stringType("name", "28 Degrees")
//                        .stringType("type", "CREDIT_CARD")
//                        .stringType("code", "CC_001")
//                        .stringType("version", "v1")
//                    )
//                .toPact(V4Pact.class);
//    }
//
//    @Test
//    @PactTestFor(pactMethod = "singleProduct", pactVersion = PactSpecVersion.V3)
//    void testSingleProduct(MockServer mockServer) {
//        productServiceClient.setBaseUrl(mockServer.getUrl());
//        //SimpleProductDto product = productServiceClient.getProductById(10L);
//        //assertThat(product, is(equalTo(new Product(10L, "28 Degrees", "CREDIT_CARD", "v1", "CC_001"))));
//    }
//}
