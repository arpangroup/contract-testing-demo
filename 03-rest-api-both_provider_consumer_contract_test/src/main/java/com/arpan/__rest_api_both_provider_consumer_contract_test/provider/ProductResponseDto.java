package com.arpan.__rest_api_both_provider_consumer_contract_test.provider;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductResponseDto {
    private String productId;
    private String productName;
    private int price;
    private boolean isActive = true;
    private String description = "description";

    public ProductResponseDto(String productId, String productName, int price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }
}
