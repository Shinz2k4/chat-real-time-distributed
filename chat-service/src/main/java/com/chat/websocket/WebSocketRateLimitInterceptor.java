package com.chat.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket Rate Limiting Interceptor
 * 
 * Implements rate limiting for WebSocket connections to prevent abuse
 */
@Component
public class WebSocketRateLimitInterceptor implements ChannelInterceptor {

    private final ConcurrentHashMap<String, AtomicInteger> messageCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTimes = new ConcurrentHashMap<>();
    
    private static final int MAX_MESSAGES_PER_MINUTE = 60;
    private static final long RESET_INTERVAL = 60000; // 1 minute

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            
            if (sessionId != null) {
                long currentTime = System.currentTimeMillis();
                long lastReset = lastResetTimes.getOrDefault(sessionId, 0L);
                
                // Reset counter if interval has passed
                if (currentTime - lastReset > RESET_INTERVAL) {
                    messageCounts.put(sessionId, new AtomicInteger(0));
                    lastResetTimes.put(sessionId, currentTime);
                }
                
                // Check rate limit
                AtomicInteger count = messageCounts.computeIfAbsent(sessionId, k -> new AtomicInteger(0));
                if (count.incrementAndGet() > MAX_MESSAGES_PER_MINUTE) {
                    // Rate limit exceeded - reject message
                    return null;
                }
            }
        }
        
        return message;
    }
}



