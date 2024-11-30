package com.arpan.__rest_api_consumer_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetailProductDto extends SimpleProductDto{
    private boolean isActive;

    public DetailProductDto(String productId, String productName, int price, boolean isActive) {
        super(productId, productName, price);
        this.isActive = isActive;
    }
}
