package com.arpan.__rest_api_consumer_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDto {
    private String productName;
    private int price;
}
