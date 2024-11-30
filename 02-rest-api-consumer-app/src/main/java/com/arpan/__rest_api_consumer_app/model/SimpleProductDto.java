package com.arpan.__rest_api_consumer_app.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleProductDto {
    private String productId;
    private String productName;
    private int price;
}
