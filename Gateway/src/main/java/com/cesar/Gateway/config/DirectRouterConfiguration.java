package com.cesar.Gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DirectRouterConfiguration {

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder){

        return builder.routes()

                .route( r -> r
                        .path("/users/**")
                        .uri("http://localhost:8001"))
                .route( r -> r
                        .path("/conversations/**","/messages/**")
                        .uri("http://localhost:8002"))
                .build();
    }
}