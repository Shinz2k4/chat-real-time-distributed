package com.chat.websocket.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket Rate Limiting Interceptor
 * 
 * This interceptor implements rate limiting for WebSocket connections
 * to prevent abuse and ensure fair usage of resources.
 */
@Component
public class WebSocketRateLimitInterceptor implements ChannelInterceptor {

    // Rate limiting configuration
    private static final int MAX_MESSAGES_PER_MINUTE = 60;
    private static final int MAX_MESSAGES_PER_HOUR = 1000;
    private static final long RATE_LIMIT_WINDOW_MS = 60000; // 1 minute
    private static final long RATE_LIMIT_HOUR_WINDOW_MS = 3600000; // 1 hour

    // Rate limiting storage
    private final Map<String, RateLimitData> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.SEND.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String userId = getUserIdFromSession(accessor);
            
            if (sessionId != null) {
                if (!isWithinRateLimit(sessionId, userId)) {
                    throw new SecurityException("Rate limit exceeded. Please slow down your message sending.");
                }
            }
        }
        
        return message;
    }

    /**
     * Check if the user is within rate limits
     * 
     * @param sessionId WebSocket session ID
     * @param userId User ID (if available)
     * @return true if within limits, false otherwise
     */
    private boolean isWithinRateLimit(String sessionId, String userId) {
        String key = userId != null ? userId : sessionId;
        long currentTime = System.currentTimeMillis();
        
        RateLimitData data = rateLimitMap.computeIfAbsent(key, k -> new RateLimitData());
        
        // Clean up old entries
        cleanupOldEntries(currentTime);
        
        // Check minute rate limit
        if (data.minuteCount.get() >= MAX_MESSAGES_PER_MINUTE) {
            long timeSinceLastMinuteReset = currentTime - data.lastMinuteReset.get();
            if (timeSinceLastMinuteReset < RATE_LIMIT_WINDOW_MS) {
                return false;
            } else {
                // Reset minute counter
                data.minuteCount.set(0);
                data.lastMinuteReset.set(currentTime);
            }
        }
        
        // Check hour rate limit
        if (data.hourCount.get() >= MAX_MESSAGES_PER_HOUR) {
            long timeSinceLastHourReset = currentTime - data.lastHourReset.get();
            if (timeSinceLastHourReset < RATE_LIMIT_HOUR_WINDOW_MS) {
                return false;
            } else {
                // Reset hour counter
                data.hourCount.set(0);
                data.lastHourReset.set(currentTime);
            }
        }
        
        // Increment counters
        data.minuteCount.incrementAndGet();
        data.hourCount.incrementAndGet();
        
        return true;
    }

    /**
     * Get user ID from session
     * 
     * @param accessor StompHeaderAccessor
     * @return User ID if available, null otherwise
     */
    private String getUserIdFromSession(StompHeaderAccessor accessor) {
        // TODO: Extract user ID from authentication context
        // This should get the user ID from the authenticated user
        return null;
    }

    /**
     * Clean up old rate limit entries
     * 
     * @param currentTime Current timestamp
     */
    private void cleanupOldEntries(long currentTime) {
        rateLimitMap.entrySet().removeIf(entry -> {
            RateLimitData data = entry.getValue();
            long timeSinceLastActivity = currentTime - Math.max(
                data.lastMinuteReset.get(), 
                data.lastHourReset.get()
            );
            return timeSinceLastActivity > RATE_LIMIT_HOUR_WINDOW_MS * 2; // Remove entries older than 2 hours
        });
    }

    /**
     * Rate limit data structure
     */
    private static class RateLimitData {
        private final AtomicInteger minuteCount = new AtomicInteger(0);
        private final AtomicInteger hourCount = new AtomicInteger(0);
        private final AtomicLong lastMinuteReset = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong lastHourReset = new AtomicLong(System.currentTimeMillis());
    }
}
