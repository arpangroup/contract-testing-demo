package com.arpan.__rest_api_provider_app.controller;

import com.arpan.__rest_api_provider_app.dto.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/")
public class ProductController {

    @GetMapping("/{productId}")
    public ProductResponse getProductDetails(@PathVariable String productId) {
        return new ProductResponse("P123", "Samsung Mobile", 15000);
    }
}
