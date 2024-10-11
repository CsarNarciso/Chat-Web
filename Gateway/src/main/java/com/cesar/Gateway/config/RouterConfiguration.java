package com.cesar.Gateway.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
public class RouterConfiguration {

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder){

        return builder.routes()

                .route( r -> r
                        .path("/users.api/**")
                        .uri("lb://user-service"))
                .route( r -> r
                        .path("/chat-service/**")
                        .uri("lb://chat-service"))
                .build();
    }
}