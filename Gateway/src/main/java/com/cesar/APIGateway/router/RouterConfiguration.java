package com.cesar.APIGateway.router;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableDiscoveryClient
public class RouterConfiguration {

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder){

        return builder.routes()

                .route( r -> r
                            .path("/users.api/**")
                            .uri("lb://user-api"))
                .route( r -> r
                            .path("/messages.api/**")
                            .uri("lb://message-api"))
                .route( r -> r
                            .path("/conversations.api/**")
                            .uri("lb://conversation-api"))
                .route( r -> r
                            .path("/chat-service/**")
                            .uri("lb://chat-service"))
                .build();
    }
}