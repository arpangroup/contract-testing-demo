package com.arpan.__rest_api_provider_app.controller;

import com.arpan.__rest_api_provider_app.model.Product;
import com.arpan.__rest_api_provider_app.exception.BadRequestException;
import com.arpan.__rest_api_provider_app.model.ProductCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository repository;

    @GetMapping
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @GetMapping("/{productId}")
    public Product getProductDetails(@PathVariable String productId) {
        if (!StringUtils.hasText(productId)) throw new BadRequestException();
        return repository.findById(productId);
    }

    @PostMapping
    public Product createNewProduct(ProductCreateRequestDto requestDto) {
        Product product = new Product(null, requestDto.getProductName(), requestDto.getPrice());
        return repository.save(product);
    }
}
