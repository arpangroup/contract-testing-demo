package com.arpan.__kafka_json_consumer.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class DemoController {
    private final Environment environment;;

    @GetMapping("/ping")
    public String ping() {
        return "server is running on port %s  ......".formatted(environment.getProperty("local.server.port"));
    }
}
