package com.chat.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Cloud Gateway Application
 * 
 * This application serves as the API Gateway for the distributed chat system.
 * It handles routing, load balancing, and cross-cutting concerns like
 * authentication, rate limiting, and request/response transformation.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
