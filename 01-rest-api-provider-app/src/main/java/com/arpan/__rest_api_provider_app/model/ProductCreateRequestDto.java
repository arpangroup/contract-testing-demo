package com.arpan.__rest_api_provider_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductCreateRequestDto {
    private String productName;
    private int price;
}
