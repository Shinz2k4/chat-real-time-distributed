package com.chat.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * WebSocket Message Filter Interceptor
 * 
 * Filters out unwanted or malicious WebSocket messages
 */
@Component
public class WebSocketMessageFilterInterceptor implements ChannelInterceptor {

    private static final List<String> ALLOWED_DESTINATIONS = Arrays.asList(
        "/app/chat.send",
        "/app/chat.typing",
        "/app/chat.read",
        "/app/chat.reaction"
    );

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            
            // Filter by allowed destinations
            if (destination == null || !ALLOWED_DESTINATIONS.contains(destination)) {
                return null; // Reject message
            }
            
            // Additional filtering logic can be added here
            // e.g., check user permissions, content filtering, etc.
        }
        
        return message;
    }
}



