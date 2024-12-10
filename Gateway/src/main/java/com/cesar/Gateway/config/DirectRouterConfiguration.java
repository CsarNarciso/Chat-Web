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
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Forward API requests for the user service
                .route(r -> r
                        .path("/users/**")
                        .uri("http://localhost:8001"))
                .route(r -> r
                        .path("/conversations/**", "/messages/**")
                        .uri("http://localhost:8002"))
                // Route API docs for the user service
                .route("user-service-docs", r -> r
                        .path("/user-service/docs/**")
                        .filters(f -> f.rewritePath("/user-service/docs/(?<segment>.*)", "/docs/${segment}"))
                        .uri("http://localhost:8001"))
                // Route Swagger UI for the user service
                .route("user-service-swagger-ui", r -> r
                        .path("/swagger-ui.html")
                        .and()
                        .query("configUrl", "/user-service/docs/swagger-config")
                        .uri("http://localhost:8001"))
                .build();
    }
}