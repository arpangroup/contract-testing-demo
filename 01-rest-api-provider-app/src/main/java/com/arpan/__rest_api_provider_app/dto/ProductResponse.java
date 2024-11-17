package com.arpan.__rest_api_provider_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponse {
    private String productId;
    private String productName;
    private int price;
    private boolean isActive;

    public ProductResponse(String productId, String productName, int price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }
}
