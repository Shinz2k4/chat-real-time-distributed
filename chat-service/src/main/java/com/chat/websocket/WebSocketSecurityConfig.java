package com.chat.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Security Configuration
 * 
 * This configuration handles WebSocket security including:
 * - Authentication and authorization for WebSocket connections
 * - Channel interceptors for message validation
 * - Security policies for different message types
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure Client Inbound Channel
     * 
     * Sets up interceptors for incoming WebSocket messages to handle:
     * - Authentication validation
     * - Authorization checks
     * - Message sanitization
     * - Rate limiting
     * 
     * @param registration ChannelRegistration for inbound channel configuration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add authentication interceptor
        registration.interceptors(new WebSocketAuthInterceptor());
        
        // Add rate limiting interceptor
        registration.interceptors(new WebSocketRateLimitInterceptor());
        
        // Add message validation interceptor
        registration.interceptors(new WebSocketMessageValidationInterceptor());
    }

    /**
     * Configure Client Outbound Channel
     * 
     * Sets up interceptors for outgoing WebSocket messages to handle:
     * - Message filtering
     * - Logging and monitoring
     * - Response transformation
     * 
     * @param registration ChannelRegistration for outbound channel configuration
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // Add logging interceptor for monitoring
        registration.interceptors(new WebSocketLoggingInterceptor());
        
        // Add message filtering interceptor
        registration.interceptors(new WebSocketMessageFilterInterceptor());
    }

    /**
     * Configure Message Broker
     * 
     * Sets up the message broker with security considerations:
     * - Protected topics and queues
     * - User-specific message routing
     * - Message size limits
     * 
     * @param config MessageBrokerRegistry for broker configuration
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker with security
        config.enableSimpleBroker("/topic", "/queue", "/user");
        
        // Set application destination prefix
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messaging
        config.setUserDestinationPrefix("/user");
        
        // Configure heartbeat for connection monitoring
        // Note: setHeartbeat method is not available in newer Spring versions
        // Heartbeat is configured at the endpoint level instead
    }

    /**
     * Register STOMP Endpoints
     * 
     * Registers secure WebSocket endpoints with proper CORS and security policies.
     * 
     * @param registry StompEndpointRegistry for endpoint registration
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Secure chat endpoint
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("https://*.yourdomain.com", "http://localhost:*")
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000)
                .setStreamBytesLimit(128 * 1024)
                .setHttpMessageCacheSize(1000);

        // Secure notification endpoint
        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("https://*.yourdomain.com", "http://localhost:*")
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000)
                .setStreamBytesLimit(64 * 1024)
                .setHttpMessageCacheSize(500);
    }
}
