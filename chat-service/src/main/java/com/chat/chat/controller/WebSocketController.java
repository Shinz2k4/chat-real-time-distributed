package com.chat.chat.controller;

import com.chat.chat.model.Message;
import com.chat.chat.model.Conversation;
import com.chat.chat.service.MessageService;
import com.chat.chat.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket Controller
 * 
 * Handles real-time WebSocket communication for chat functionality
 * using STOMP protocol for message routing and delivery.
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    /**
     * Handle chat message sending
     * 
     * @param messageData Message data from client
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> messageData) {
        try {
            // Extract message data
            String conversationId = (String) messageData.get("conversationId");
            String senderId = (String) messageData.get("senderId");
            String content = (String) messageData.get("content");
            String type = (String) messageData.get("type");
            
            // Create message
            Message message = new Message(
                    conversationId,
                    senderId,
                    content,
                    Message.MessageType.valueOf(type != null ? type : "TEXT")
            );
            
            // Save message
            Message savedMessage = messageService.sendMessage(message);
            
            // Broadcast to conversation participants
            String destination = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "MESSAGE_SENT",
                    "message", Map.of(
                            "id", savedMessage.getId(),
                            "conversationId", savedMessage.getConversationId(),
                            "senderId", savedMessage.getSenderId(),
                            "content", savedMessage.getContent(),
                            "type", savedMessage.getType().toString(),
                            "status", savedMessage.getStatus().toString(),
                            "createdAt", savedMessage.getCreatedAt()
                    )
            ));
            
        } catch (Exception e) {
            // Send error back to sender
            messagingTemplate.convertAndSendToUser(
                    (String) messageData.get("senderId"),
                    "/queue/errors",
                    Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * Handle typing indicator
     * 
     * @param typingData Typing data from client
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> typingData) {
        try {
            String conversationId = (String) typingData.get("conversationId");
            String userId = (String) typingData.get("userId");
            boolean isTyping = (Boolean) typingData.get("isTyping");
            
            // Broadcast typing indicator to conversation participants
            String destination = "/topic/typing." + conversationId;
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "TYPING_INDICATOR",
                    "userId", userId,
                    "isTyping", isTyping,
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error handling typing indicator: " + e.getMessage());
        }
    }

    /**
     * Handle message read receipt
     * 
     * @param readData Read data from client
     */
    @MessageMapping("/chat.read")
    public void handleMessageRead(@Payload Map<String, Object> readData) {
        try {
            String messageId = (String) readData.get("messageId");
            String conversationId = (String) readData.get("conversationId");
            String userId = (String) readData.get("userId");
            
            // Update message status to seen
            messageService.updateMessageStatus(messageId, Message.MessageStatus.SEEN);
            
            // Broadcast read receipt to conversation participants
            String destination = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "MESSAGE_READ",
                    "messageId", messageId,
                    "userId", userId,
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error handling message read: " + e.getMessage());
        }
    }

    /**
     * Handle message reaction
     * 
     * @param reactionData Reaction data from client
     */
    @MessageMapping("/chat.react")
    public void handleReaction(@Payload Map<String, Object> reactionData) {
        try {
            String messageId = (String) reactionData.get("messageId");
            String conversationId = (String) reactionData.get("conversationId");
            String userId = (String) reactionData.get("userId");
            String emoji = (String) reactionData.get("emoji");
            
            // Add reaction to message
            Message message = messageService.addReaction(messageId, userId, emoji);
            
            // Broadcast reaction to conversation participants
            String destination = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "MESSAGE_REACTION",
                    "messageId", messageId,
                    "userId", userId,
                    "emoji", emoji,
                    "reactions", message.getReactions(),
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error handling message reaction: " + e.getMessage());
        }
    }

    /**
     * Handle conversation join
     * 
     * @param joinData Join data from client
     */
    @MessageMapping("/chat.join")
    public void handleConversationJoin(@Payload Map<String, Object> joinData) {
        try {
            String conversationId = (String) joinData.get("conversationId");
            String userId = (String) joinData.get("userId");
            
            // Verify user is participant
            Conversation conversation = conversationService.getConversationById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            
            boolean isParticipant = conversation.getParticipants().stream()
                    .anyMatch(p -> p.getUserId().equals(userId));
            
            if (!isParticipant) {
                throw new RuntimeException("User is not a participant in this conversation");
            }
            
            // Broadcast user joined to conversation participants
            String destination = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "USER_JOINED",
                    "userId", userId,
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            // Send error back to sender
            messagingTemplate.convertAndSendToUser(
                    (String) joinData.get("userId"),
                    "/queue/errors",
                    Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * Handle conversation leave
     * 
     * @param leaveData Leave data from client
     */
    @MessageMapping("/chat.leave")
    public void handleConversationLeave(@Payload Map<String, Object> leaveData) {
        try {
            String conversationId = (String) leaveData.get("conversationId");
            String userId = (String) leaveData.get("userId");
            
            // Broadcast user left to conversation participants
            String destination = "/topic/conversation." + conversationId;
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "USER_LEFT",
                    "userId", userId,
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error handling conversation leave: " + e.getMessage());
        }
    }

    /**
     * Handle presence update
     * 
     * @param presenceData Presence data from client
     */
    @MessageMapping("/presence.update")
    public void handlePresenceUpdate(@Payload Map<String, Object> presenceData) {
        try {
            String userId = (String) presenceData.get("userId");
            String status = (String) presenceData.get("status");
            
            // Broadcast presence update to all users
            String destination = "/topic/presence";
            messagingTemplate.convertAndSend(destination, Map.of(
                    "type", "PRESENCE_UPDATE",
                    "userId", userId,
                    "status", status,
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error handling presence update: " + e.getMessage());
        }
    }
}