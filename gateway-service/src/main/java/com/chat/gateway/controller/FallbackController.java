package com.chat.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller
 * 
 * Handles fallback responses when services are unavailable.
 * Provides graceful degradation for circuit breaker scenarios.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * User Service Fallback
     * 
     * @return Fallback response for user service
     */
    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User service is temporarily unavailable");
        response.put("message", "Please try again later");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Chat Service Fallback
     * 
     * @return Fallback response for chat service
     */
    @GetMapping("/chat-service")
    public ResponseEntity<Map<String, Object>> chatServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Chat service is temporarily unavailable");
        response.put("message", "Please try again later");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Notification Service Fallback
     * 
     * @return Fallback response for notification service
     */
    @GetMapping("/notification-service")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Notification service is temporarily unavailable");
        response.put("message", "Please try again later");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * WebSocket Service Fallback
     * 
     * @return Fallback response for websocket service
     */
    @GetMapping("/websocket-service")
    public ResponseEntity<Map<String, Object>> websocketServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "WebSocket service is temporarily unavailable");
        response.put("message", "Please try again later");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
