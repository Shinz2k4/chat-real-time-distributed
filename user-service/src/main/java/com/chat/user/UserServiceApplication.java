package com.chat.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * User Service Application
 * 
 * This application handles user management including:
 * - User authentication and authorization
 * - Profile management
 * - Social features (friends, contacts)
 * - Presence status management
 * - OAuth2 integration
 */
@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@EnableDiscoveryClient
@EnableMongoAuditing
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
