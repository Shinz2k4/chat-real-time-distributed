package com.chat.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * WebSocket Message Validation Interceptor
 * 
 * Validates WebSocket messages for security and content compliance
 */
@Component
public class WebSocketMessageValidationInterceptor implements ChannelInterceptor {

    private static final Pattern CONTENT_PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\p{P}\\p{Z}]*$");
    private static final int MAX_MESSAGE_LENGTH = 10000;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Object payload = message.getPayload();
            
            // Validate destination
            if (destination == null || !destination.startsWith("/app/")) {
                return null; // Reject invalid destination
            }
            
            // Validate payload
            if (payload instanceof String) {
                String content = (String) payload;
                
                // Check length
                if (content.length() > MAX_MESSAGE_LENGTH) {
                    return null; // Reject too long message
                }
                
                // Check content pattern (basic validation)
                if (!CONTENT_PATTERN.matcher(content).matches()) {
                    return null; // Reject invalid content
                }
            }
        }
        
        return message;
    }
}



