package com.chat.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Chat Service Application
 * 
 * This application handles real-time messaging including:
 * - WebSocket communication using STOMP protocol
 * - Message persistence and retrieval
 * - Conversation management
 * - Real-time presence tracking
 * - Message status updates
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableMongoAuditing
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
}
