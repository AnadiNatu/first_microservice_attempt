package com.microservices.api_gateway.configuration;


import com.microservices.api_gateway.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("admin-service", r -> r.path("/api/admin/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://admin-service"))
//                .route("user-service", r -> r.path("/api/users/**")
//                        .filters(f -> f.filter(jwtAuthFilter))
//                        .uri("lb://user-service"))
                .route("banker-service", r -> r.path("/api/banker/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://banker-service"))
                .route("identity-service" , r -> r.path("/api/auth/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://identity-service"))
                .build();
//                        .uri("http://localhost:8081"))
    }
}