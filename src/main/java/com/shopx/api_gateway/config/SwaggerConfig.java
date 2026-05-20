package com.shopx.api_gateway.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public CommandLineRunner swaggerGroups(SwaggerUiConfigParameters params) {
        return args -> {
            params.addGroup("order-service", "/order-service/v3/api-docs");
            params.addGroup("user-service", "/user-service/v3/api-docs");
            params.addGroup("product-service", "/product-service/v3/api-docs");
            params.addGroup("payment-service", "/payment-service/v3/api-docs");
        };
    }
}