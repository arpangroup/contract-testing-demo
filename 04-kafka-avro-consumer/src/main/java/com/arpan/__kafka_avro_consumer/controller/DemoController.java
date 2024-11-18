package com.arpan.__kafka_avro_consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/ping")
    public String ping() {
        return "ping from kafka-avro-consumer-controller......";
    }
}
