package com.chat.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Message Filter Interceptor
 * 
 * This interceptor filters outgoing messages based on various criteria:
 * - User permissions
 * - Message content policies
 * - Rate limiting
 * - Privacy settings
 */
@Component
public class WebSocketMessageFilterInterceptor implements ChannelInterceptor {

    // Track user subscriptions for filtering
    private final Set<String> activeSubscriptions = ConcurrentHashMap.newKeySet();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            String sessionId = accessor.getSessionId();
            StompCommand command = accessor.getCommand();
            
            // Track subscriptions for filtering
            if (StompCommand.SUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                if (destination != null) {
                    activeSubscriptions.add(sessionId + ":" + destination);
                }
            } else if (StompCommand.UNSUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                if (destination != null) {
                    activeSubscriptions.remove(sessionId + ":" + destination);
                }
            }
            
            // Filter messages based on destination and user permissions
            if (StompCommand.MESSAGE.equals(command)) {
                if (!shouldDeliverMessage(message, accessor)) {
                    return null; // Filter out the message
                }
            }
        }
        
        return message;
    }

    /**
     * Determine if a message should be delivered
     * 
     * @param message WebSocket message
     * @param accessor StompHeaderAccessor
     * @return true if message should be delivered, false otherwise
     */
    private boolean shouldDeliverMessage(Message<?> message, StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        
        if (destination == null || sessionId == null) {
            return false;
        }
        
        // Check if user is subscribed to this destination
        if (!activeSubscriptions.contains(sessionId + ":" + destination)) {
            return false;
        }
        
        // Apply content filtering
        if (!passesContentFilter(message)) {
            return false;
        }
        
        // Apply user permission filtering
        if (!passesPermissionFilter(destination, sessionId)) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if message passes content filtering
     * 
     * @param message WebSocket message
     * @return true if content is acceptable, false otherwise
     */
    private boolean passesContentFilter(Message<?> message) {
        // TODO: Implement content filtering logic
        // This could include:
        // - Profanity filtering
        // - Spam detection
        // - Content moderation
        // - Privacy policy compliance
        
        return true; // For now, allow all content
    }

    /**
     * Check if user has permission to receive this message
     * 
     * @param destination Message destination
     * @param sessionId User session ID
     * @return true if user has permission, false otherwise
     */
    private boolean passesPermissionFilter(String destination, String sessionId) {
        // TODO: Implement permission filtering logic
        // This could include:
        // - User role-based filtering
        // - Conversation membership checks
        // - Privacy setting enforcement
        // - Blocked user filtering
        
        return true; // For now, allow all messages
    }
}
