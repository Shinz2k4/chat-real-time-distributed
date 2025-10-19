package com.chat.chat.controller;

import com.chat.chat.model.Message;
import com.chat.chat.model.Conversation;
import com.chat.chat.service.MessageService;
import com.chat.chat.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat Controller
 * 
 * Handles chat-related REST endpoints for message and conversation management.
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    /**
     * Send a message
     * 
     * @param request Send message request
     * @return Message response
     */
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        try {
            Message message = new Message(
                    request.getConversationId(),
                    request.getSenderId(),
                    request.getContent(),
                    request.getType()
            );
            
            message.setReplyTo(request.getReplyTo());
            message.setAttachments(request.getAttachments());
            message.setMentions(request.getMentions());
            
            Message savedMessage = messageService.sendMessage(message);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Message sent successfully",
                    "data", Map.of(
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
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get messages by conversation
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable String conversationId, Pageable pageable) {
        try {
            Page<Message> messages = messageService.getMessagesByConversation(conversationId, pageable);
            
            return ResponseEntity.ok(Map.of(
                    "messages", messages.getContent().stream().map(this::mapMessageToResponse).toList(),
                    "totalElements", messages.getTotalElements(),
                    "totalPages", messages.getTotalPages(),
                    "currentPage", messages.getNumber(),
                    "size", messages.getSize()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get message by ID
     * 
     * @param messageId Message ID
     * @return Message response
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessage(@PathVariable String messageId) {
        try {
            Message message = messageService.getMessageById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));
            
            return ResponseEntity.ok(Map.of(
                    "message", mapMessageToResponse(message)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Edit a message
     * 
     * @param messageId Message ID
     * @param request Edit message request
     * @return Updated message response
     */
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<?> editMessage(@PathVariable String messageId, @RequestBody EditMessageRequest request) {
        try {
            Message message = messageService.editMessage(messageId, request.getContent());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Message updated successfully",
                    "data", mapMessageToResponse(message)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete a message
     * 
     * @param messageId Message ID
     * @return Deletion response
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId) {
        try {
            messageService.deleteMessage(messageId);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Message deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Add reaction to message
     * 
     * @param messageId Message ID
     * @param request Reaction request
     * @return Updated message response
     */
    @PostMapping("/messages/{messageId}/reactions")
    public ResponseEntity<?> addReaction(@PathVariable String messageId, @RequestBody ReactionRequest request) {
        try {
            Message message = messageService.addReaction(messageId, request.getUserId(), request.getEmoji());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Reaction added successfully",
                    "data", mapMessageToResponse(message)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remove reaction from message
     * 
     * @param messageId Message ID
     * @param userId User ID
     * @return Updated message response
     */
    @DeleteMapping("/messages/{messageId}/reactions/{userId}")
    public ResponseEntity<?> removeReaction(@PathVariable String messageId, @PathVariable String userId) {
        try {
            Message message = messageService.removeReaction(messageId, userId);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Reaction removed successfully",
                    "data", mapMessageToResponse(message)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Search messages
     * 
     * @param conversationId Conversation ID
     * @param query Search query
     * @param pageable Pagination
     * @return Page of messages
     */
    @GetMapping("/conversations/{conversationId}/search")
    public ResponseEntity<?> searchMessages(@PathVariable String conversationId, 
                                          @RequestParam String query, 
                                          Pageable pageable) {
        try {
            Page<Message> messages = messageService.searchMessages(conversationId, query, pageable);
            
            return ResponseEntity.ok(Map.of(
                    "messages", messages.getContent().stream().map(this::mapMessageToResponse).toList(),
                    "totalElements", messages.getTotalElements(),
                    "totalPages", messages.getTotalPages(),
                    "currentPage", messages.getNumber(),
                    "size", messages.getSize()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create a conversation
     * 
     * @param request Create conversation request
     * @return Conversation response
     */
    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(@Valid @RequestBody CreateConversationRequest request) {
        try {
            Conversation conversation;
            
            if (request.getType() == Conversation.ConversationType.DIRECT) {
                conversation = conversationService.createDirectConversation(
                        request.getParticipantIds().get(0),
                        request.getParticipantIds().get(1)
                );
            } else {
                conversation = conversationService.createGroupConversation(
                        request.getName(),
                        request.getDescription(),
                        request.getCreatedBy(),
                        request.getParticipantIds()
                );
            }
            
            return ResponseEntity.ok(Map.of(
                    "message", "Conversation created successfully",
                    "data", mapConversationToResponse(conversation)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get conversations by user
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    @GetMapping("/users/{userId}/conversations")
    public ResponseEntity<?> getConversationsByUser(@PathVariable String userId, Pageable pageable) {
        try {
            Page<Conversation> conversations = conversationService.getConversationsByUser(userId, pageable);
            
            return ResponseEntity.ok(Map.of(
                    "conversations", conversations.getContent().stream().map(this::mapConversationToResponse).toList(),
                    "totalElements", conversations.getTotalElements(),
                    "totalPages", conversations.getTotalPages(),
                    "currentPage", conversations.getNumber(),
                    "size", conversations.getSize()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get conversation by ID
     * 
     * @param conversationId Conversation ID
     * @return Conversation response
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<?> getConversation(@PathVariable String conversationId) {
        try {
            Conversation conversation = conversationService.getConversationById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            
            return ResponseEntity.ok(Map.of(
                    "conversation", mapConversationToResponse(conversation)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Map message to response DTO
     * 
     * @param message Message entity
     * @return Response DTO
     */
    private Map<String, Object> mapMessageToResponse(Message message) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", message.getId());
        response.put("conversationId", message.getConversationId());
        response.put("senderId", message.getSenderId());
        response.put("content", message.getContent());
        response.put("type", message.getType().toString());
        response.put("status", message.getStatus().toString());
        response.put("replyTo", message.getReplyTo());
        response.put("attachments", message.getAttachments() != null ? message.getAttachments() : List.of());
        response.put("reactions", message.getReactions() != null ? message.getReactions() : List.of());
        response.put("mentions", message.getMentions() != null ? message.getMentions() : List.of());
        response.put("isEdited", message.isEdited());
        response.put("editedAt", message.getEditedAt());
        response.put("isDeleted", message.isDeleted());
        response.put("deletedAt", message.getDeletedAt());
        response.put("createdAt", message.getCreatedAt());
        response.put("updatedAt", message.getUpdatedAt());
        return response;
    }

    /**
     * Map conversation to response DTO
     * 
     * @param conversation Conversation entity
     * @return Response DTO
     */
    private Map<String, Object> mapConversationToResponse(Conversation conversation) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", conversation.getId());
        response.put("type", conversation.getType().toString());
        response.put("name", conversation.getName());
        response.put("description", conversation.getDescription());
        response.put("avatar", conversation.getAvatar());
        response.put("participants", conversation.getParticipants());
        response.put("createdBy", conversation.getCreatedBy());
        response.put("lastMessage", conversation.getLastMessage());
        response.put("settings", conversation.getSettings());
        response.put("metadata", conversation.getMetadata());
        response.put("createdAt", conversation.getCreatedAt());
        response.put("updatedAt", conversation.getUpdatedAt());
        return response;
    }

    // Request DTOs
    public static class SendMessageRequest {
        private String conversationId;
        private String senderId;
        private String content;
        private Message.MessageType type = Message.MessageType.TEXT;
        private Message.ReplyTo replyTo;
        private List<Message.Attachment> attachments;
        private List<Message.Mention> mentions;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Message.MessageType getType() { return type; }
        public void setType(Message.MessageType type) { this.type = type; }
        public Message.ReplyTo getReplyTo() { return replyTo; }
        public void setReplyTo(Message.ReplyTo replyTo) { this.replyTo = replyTo; }
        public List<Message.Attachment> getAttachments() { return attachments; }
        public void setAttachments(List<Message.Attachment> attachments) { this.attachments = attachments; }
        public List<Message.Mention> getMentions() { return mentions; }
        public void setMentions(List<Message.Mention> mentions) { this.mentions = mentions; }
    }

    public static class EditMessageRequest {
        private String content;

        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class ReactionRequest {
        private String userId;
        private String emoji;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmoji() { return emoji; }
        public void setEmoji(String emoji) { this.emoji = emoji; }
    }

    public static class CreateConversationRequest {
        private Conversation.ConversationType type;
        private String name;
        private String description;
        private String createdBy;
        private List<String> participantIds;

        // Getters and Setters
        public Conversation.ConversationType getType() { return type; }
        public void setType(Conversation.ConversationType type) { this.type = type; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public List<String> getParticipantIds() { return participantIds; }
        public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }
    }
}
