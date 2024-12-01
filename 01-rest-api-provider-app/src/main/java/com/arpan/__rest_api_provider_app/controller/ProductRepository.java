package com.arpan.__rest_api_provider_app.controller;

import com.arpan.__rest_api_provider_app.exception.BadRequestException;
import com.arpan.__rest_api_provider_app.model.Product;
import com.arpan.__rest_api_provider_app.exception.ProductNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class ProductRepository {
    private List<Product> products = new ArrayList<>();

    @PostConstruct
    public void init() {
        products.addAll(Arrays.asList(
                new Product("P101", "Product1", 500),
                new Product("P102", "Product2", 600),
                new Product("P103", "Product3", 700),
                new Product("P104", "Product4", 800),
                new Product("P105", "Product5", 900)
        ));
    }


    public List<Product> findAll() {
        return this.products;
    }

    public Product findById(final String productId) {
        return products.stream().filter(p -> Objects.equals(p.getProductId(), productId)).findAny().orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Product save(Product product) {
        try {
            product.setProductId("P10" + (this.products.size() + 1));
            products.add(product);
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }

    }
}
