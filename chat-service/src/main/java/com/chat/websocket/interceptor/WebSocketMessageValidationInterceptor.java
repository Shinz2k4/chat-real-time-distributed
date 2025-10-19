package com.chat.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * WebSocket Message Validation Interceptor
 * 
 * This interceptor validates incoming WebSocket messages to ensure:
 * - Message content is valid and safe
 * - Message size is within limits
 * - Message format is correct
 * - No malicious content is being sent
 */
@Component
public class WebSocketMessageValidationInterceptor implements ChannelInterceptor {

    // Message validation constants
    private static final int MAX_MESSAGE_SIZE = 5000; // 5KB max message size
    private static final int MAX_MESSAGE_LENGTH = 10000; // 10,000 characters max
    private static final String[] FORBIDDEN_PATTERNS = {
        "<script", "</script>", "javascript:", "onload=", "onerror=",
        "eval(", "document.cookie", "window.location", "alert("
    };

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            // Validate message content
            if (!validateMessage(message)) {
                throw new SecurityException("Invalid message content detected");
            }
        }
        
        return message;
    }

    /**
     * Validate message content
     * 
     * @param message WebSocket message to validate
     * @return true if valid, false otherwise
     */
    private boolean validateMessage(Message<?> message) {
        try {
            // Check message size
            if (message.getPayload() instanceof byte[]) {
                byte[] payload = (byte[]) message.getPayload();
                if (payload.length > MAX_MESSAGE_SIZE) {
                    return false;
                }
                
                // Check for forbidden patterns in byte content
                String content = new String(payload, StandardCharsets.UTF_8);
                if (!validateContent(content)) {
                    return false;
                }
            } else if (message.getPayload() instanceof String) {
                String content = (String) message.getPayload();
                
                // Check message length
                if (content.length() > MAX_MESSAGE_LENGTH) {
                    return false;
                }
                
                // Check for forbidden patterns
                if (!validateContent(content)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            // If any validation fails, reject the message
            return false;
        }
    }

    /**
     * Validate message content for security
     * 
     * @param content Message content to validate
     * @return true if content is safe, false otherwise
     */
    private boolean validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // Check for forbidden patterns (XSS prevention)
        String lowerContent = content.toLowerCase();
        for (String pattern : FORBIDDEN_PATTERNS) {
            if (lowerContent.contains(pattern.toLowerCase())) {
                return false;
            }
        }
        
        // Check for excessive whitespace (potential DoS)
        if (content.trim().length() < content.length() * 0.1) {
            return false;
        }
        
        // Check for null bytes or control characters
        for (char c : content.toCharArray()) {
            if (c == '\0' || (c < 32 && c != '\t' && c != '\n' && c != '\r')) {
                return false;
            }
        }
        
        return true;
    }
}
