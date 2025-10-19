package com.chat.websocket.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket Logging Interceptor
 * 
 * This interceptor logs WebSocket events for monitoring and debugging purposes.
 * It tracks connection events, message flows, and errors.
 */
@Component
public class WebSocketLoggingInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketLoggingInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            String sessionId = accessor.getSessionId();
            StompCommand command = accessor.getCommand();
            
            switch (command) {
                case CONNECT:
                    logger.info("WebSocket connection established - Session: {}", sessionId);
                    break;
                case DISCONNECT:
                    logger.info("WebSocket connection closed - Session: {}", sessionId);
                    break;
                case SUBSCRIBE:
                    String subscription = accessor.getDestination();
                    logger.info("WebSocket subscription - Session: {}, Destination: {}", sessionId, subscription);
                    break;
                case UNSUBSCRIBE:
                    String unsubscription = accessor.getDestination();
                    logger.info("WebSocket unsubscription - Session: {}, Destination: {}", sessionId, unsubscription);
                    break;
                case SEND:
                    String destination = accessor.getDestination();
                    logger.debug("WebSocket message sent - Session: {}, Destination: {}", sessionId, destination);
                    break;
                case ERROR:
                    logger.error("WebSocket error occurred - Session: {}", sessionId);
                    break;
                default:
                    logger.debug("WebSocket command: {} - Session: {}", command, sessionId);
                    break;
            }
        }
        
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if (ex != null) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            String sessionId = accessor != null ? accessor.getSessionId() : "unknown";
            logger.error("WebSocket message send failed - Session: {}, Error: {}", sessionId, ex.getMessage(), ex);
        }
    }
}
