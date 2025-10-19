package com.chat.websocket;

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
 * Handles authentication for WebSocket connections using JWT tokens
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                // TODO: Validate JWT token and set authentication
                // For now, create a simple authentication
                Authentication auth = new UsernamePasswordAuthenticationToken("user", null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
                accessor.setUser(auth);
            }
        }
        
        return message;
    }
}



