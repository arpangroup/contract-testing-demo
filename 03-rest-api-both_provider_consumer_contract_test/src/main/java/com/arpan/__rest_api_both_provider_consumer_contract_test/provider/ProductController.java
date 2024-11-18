package com.arpan.__rest_api_both_provider_consumer_contract_test.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/")
public class ProductController {

    @GetMapping("/{productId}")
    public ProductResponseDto getProductDetails(@PathVariable String productId) {
        ProductResponseDto productResponse = new ProductResponseDto("P123", "Samsung Mobile", 15000);
        return productResponse;
    }
}
