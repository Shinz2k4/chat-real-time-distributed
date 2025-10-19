package com.chat.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Gateway Configuration
 * 
 * This configuration defines the routing rules for the API Gateway.
 * It routes requests to appropriate microservices based on URL patterns
 * and provides load balancing, circuit breaker, and retry mechanisms.
 */
@Configuration
public class GatewayConfig {

    /**
     * Route Locator Bean Configuration
     * 
     * Defines routing rules for different services:
     * - /api/users/** -> User Service
     * - /api/chat/** -> Chat Service  
     * - /api/notifications/** -> Notification Service
     * - /ws/** -> WebSocket Service (Chat Service)
     * 
     * @param builder RouteLocatorBuilder for creating routes
     * @return Configured RouteLocator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Service", "user-service")
                                .circuitBreaker(config -> config
                                        .setName("user-service-cb")
                                        .setFallbackUri("forward:/fallback/user-service"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
                        )
                        .uri("http://user-service:8081")
                )
                
                // Chat Service REST API Routes
                .route("chat-service", r -> r
                        .path("/api/chat/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Service", "chat-service")
                                .circuitBreaker(config -> config
                                        .setName("chat-service-cb")
                                        .setFallbackUri("forward:/fallback/chat-service"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
                        )
                        .uri("http://chat-service:8082")
                )
                
                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Service", "notification-service")
                                .circuitBreaker(config -> config
                                        .setName("notification-service-cb")
                                        .setFallbackUri("forward:/fallback/notification-service"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
                        )
                        .uri("http://notification-service:8083")
                )
                
                // WebSocket Routes for Real-time Communication
                .route("websocket-service", r -> r
                        .path("/ws/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Service", "websocket-service")
                                .circuitBreaker(config -> config
                                        .setName("websocket-service-cb")
                                        .setFallbackUri("forward:/fallback/websocket-service"))
                        )
                        .uri("http://chat-service:8082")
                )
                
                // Health Check Routes
                .route("health-check", r -> r
                        .path("/health/**")
                        .filters(f -> f
                                .stripPrefix(1) // Remove /health from path
                        )
                        .uri("http://user-service:8081")
                )
                
                .build();
    }
}
