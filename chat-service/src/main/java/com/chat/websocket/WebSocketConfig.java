package com.chat.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for Chat Service
 * 
 * This configuration enables STOMP over WebSocket for real-time messaging.
 * It sets up message brokers, endpoints, and security configurations
 * for the distributed chat application.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure Message Broker
     * 
     * Sets up the message broker for handling real-time communication:
     * - /topic: For broadcasting messages to multiple subscribers
     * - /queue: For point-to-point messaging
     * - /user: For user-specific messages (requires authentication)
     * 
     * @param config MessageBrokerRegistry for broker configuration
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for topics and queues
        config.enableSimpleBroker("/topic", "/queue", "/user");
        
        // Set application destination prefix
        // Messages sent to destinations prefixed with /app will be routed to @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messaging
        // Messages sent to /user/{username}/... will be routed to specific users
        config.setUserDestinationPrefix("/user");
        
        // Configure heartbeat settings for connection monitoring
        // Note: setHeartbeat method is not available in newer Spring versions
        // Heartbeat is configured at the endpoint level instead
    }

    /**
     * Register STOMP Endpoints
     * 
     * Registers WebSocket endpoints that clients can connect to:
     * - /ws/chat: Main chat WebSocket endpoint
     * - /ws/notifications: Notification WebSocket endpoint
     * 
     * @param registry StompEndpointRegistry for endpoint registration
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Main chat WebSocket endpoint
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("http://localhost:3000") // Only allow frontend origin
                .withSockJS() // Enable SockJS fallback for older browsers
                .setHeartbeatTime(25000) // 25 seconds heartbeat
                .setDisconnectDelay(5000) // 5 seconds disconnect delay
                .setStreamBytesLimit(128 * 1024) // 128KB stream limit
                .setHttpMessageCacheSize(1000); // Cache 1000 messages

        // Notification WebSocket endpoint
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000)
                .setStreamBytesLimit(64 * 1024) // 64KB for notifications
                .setHttpMessageCacheSize(500);

        // Admin WebSocket endpoint for monitoring
        registry.addEndpoint("/ws/admin")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(30000)
                .setDisconnectDelay(10000)
                .setStreamBytesLimit(256 * 1024)
                .setHttpMessageCacheSize(2000);
    }
}
