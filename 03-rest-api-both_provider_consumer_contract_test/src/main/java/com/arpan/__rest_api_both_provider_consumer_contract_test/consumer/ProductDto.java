package com.arpan.__rest_api_both_provider_consumer_contract_test.consumer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto {
    private String productId;
    private String productName;
    private int price;

    public ProductDto(String productId, String productName, int price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }
}
