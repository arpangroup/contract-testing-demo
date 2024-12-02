package com.arpan.__rest_api_consumer_app.service;

import com.arpan.__rest_api_consumer_app.model.DetailProductDto;
import com.arpan.__rest_api_consumer_app.model.ProductCreateRequestDto;
import com.arpan.__rest_api_consumer_app.model.SimpleProductDto;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service

public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${serviceClients.products.baseUrl:http://localhost:8080/api/products}")
    @Setter
    private String baseUrl;

    public List<SimpleProductDto> fetchProducts() {
        SimpleProductDto[] productList = restTemplate.getForObject(baseUrl, SimpleProductDto[].class);
        return Arrays.asList(productList);
    }

    public DetailProductDto fetchProductById(String id) {
        return restTemplate.getForObject(baseUrl + "/" + id, DetailProductDto.class);
    }

    public SimpleProductDto createNewProduct(ProductCreateRequestDto productCreateRequestDto) {
        return restTemplate.postForObject(baseUrl, productCreateRequestDto, SimpleProductDto.class);
    }
}
