package com.cesar.Gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@OpenAPIDefinition
@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
          .title("Gateway Service")
          .description("Routing and security for project infrastructure services")
          .version("1.0.0"));
    }
}