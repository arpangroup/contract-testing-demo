package com.arpan.__rest_api_consumer_app;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String productId;
    private String productName;
    private int price;
}
