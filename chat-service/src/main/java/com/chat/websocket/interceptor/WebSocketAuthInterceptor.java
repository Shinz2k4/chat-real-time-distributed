package com.chat.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * WebSocket Authentication Interceptor
 * 
 * This interceptor handles authentication for WebSocket connections.
 * It validates JWT tokens from the Authorization header and sets up
 * the security context for the WebSocket session.
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract JWT token from headers
            String authToken = accessor.getFirstNativeHeader("Authorization");
            
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                
                try {
                    // Validate JWT token (implement your JWT validation logic here)
                    Authentication authentication = validateJwtToken(token);
                    
                    if (authentication != null) {
                        // Set authentication in the accessor
                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        // Invalid token - reject connection
                        throw new SecurityException("Invalid authentication token");
                    }
                } catch (Exception e) {
                    // Token validation failed - reject connection
                    throw new SecurityException("Authentication failed: " + e.getMessage());
                }
            } else {
                // No valid token provided - reject connection
                throw new SecurityException("No valid authentication token provided");
            }
        }
        
        return message;
    }

    /**
     * Validate JWT Token
     * 
     * This method should be implemented to validate the JWT token
     * and return the corresponding Authentication object.
     * 
     * @param token JWT token to validate
     * @return Authentication object if valid, null otherwise
     */
    private Authentication validateJwtToken(String token) {
        // TODO: Implement JWT token validation
        // This should:
        // 1. Parse and validate the JWT token
        // 2. Extract user information from the token
        // 3. Create and return an Authentication object
        
        // For now, return a mock authentication
        // In production, implement proper JWT validation
        if (token != null && !token.isEmpty()) {
            Principal principal = () -> "user123"; // Extract from JWT
            return new UsernamePasswordAuthenticationToken(principal, null, null);
        }
        
        return null;
    }
}
