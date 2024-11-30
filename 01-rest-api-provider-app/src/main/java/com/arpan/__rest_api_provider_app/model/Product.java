package com.arpan.__rest_api_provider_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    private String productId;
    private String productName;
    private int price;
    private boolean isActive;
    private String productType;
    private String version;


    public Product(String productId, String productName, int price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.isActive = true;
        this.productType = "DEFAULT";
        this.version = "V-01";
    }
}
