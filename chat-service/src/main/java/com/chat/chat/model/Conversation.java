package com.chat.chat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Conversation Entity
 * 
 * Represents a conversation (chat room) in the chat application
 * supporting both direct messages and group conversations.
 */
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    @NotNull(message = "Conversation type is required")
    @Indexed
    private ConversationType type;

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String avatar;

    private List<Participant> participants;

    @NotNull(message = "Created by is required")
    @Indexed
    private String createdBy;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LastMessage lastMessage;

    private ConversationSettings settings = new ConversationSettings();

    private ConversationMetadata metadata = new ConversationMetadata();

    // Constructors
    public Conversation() {}

    public Conversation(ConversationType type, String createdBy) {
        this.type = type;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ConversationSettings getSettings() {
        return settings;
    }

    public void setSettings(ConversationSettings settings) {
        this.settings = settings;
    }

    public ConversationMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ConversationMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Conversation Type Enum
     */
    public enum ConversationType {
        DIRECT, GROUP
    }

    /**
     * Participant Information
     */
    public static class Participant {
        @NotNull
        private String userId;
        
        @NotNull
        private ParticipantRole role = ParticipantRole.MEMBER;
        
        @NotNull
        private LocalDateTime joinedAt;
        
        private String lastReadMessageId;
        
        private LocalDateTime lastReadAt;

        // Constructors
        public Participant() {}

        public Participant(String userId, ParticipantRole role) {
            this.userId = userId;
            this.role = role;
            this.joinedAt = LocalDateTime.now();
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public ParticipantRole getRole() { return role; }
        public void setRole(ParticipantRole role) { this.role = role; }
        public LocalDateTime getJoinedAt() { return joinedAt; }
        public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
        public String getLastReadMessageId() { return lastReadMessageId; }
        public void setLastReadMessageId(String lastReadMessageId) { this.lastReadMessageId = lastReadMessageId; }
        public LocalDateTime getLastReadAt() { return lastReadAt; }
        public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
    }

    /**
     * Participant Role Enum
     */
    public enum ParticipantRole {
        ADMIN, MEMBER
    }

    /**
     * Last Message Information
     */
    public static class LastMessage {
        private String messageId;
        private String content;
        private String senderId;
        private LocalDateTime timestamp;

        // Constructors
        public LastMessage() {}

        public LastMessage(String messageId, String content, String senderId, LocalDateTime timestamp) {
            this.messageId = messageId;
            this.content = content;
            this.senderId = senderId;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    /**
     * Conversation Settings
     */
    public static class ConversationSettings {
        @Indexed
        private boolean isArchived = false;
        
        private boolean isMuted = false;
        
        private boolean allowMemberInvite = true;
        
        private boolean allowMemberLeave = true;

        // Getters and Setters
        public boolean isArchived() { return isArchived; }
        public void setArchived(boolean archived) { isArchived = archived; }
        public boolean isMuted() { return isMuted; }
        public void setMuted(boolean muted) { isMuted = muted; }
        public boolean isAllowMemberInvite() { return allowMemberInvite; }
        public void setAllowMemberInvite(boolean allowMemberInvite) { this.allowMemberInvite = allowMemberInvite; }
        public boolean isAllowMemberLeave() { return allowMemberLeave; }
        public void setAllowMemberLeave(boolean allowMemberLeave) { this.allowMemberLeave = allowMemberLeave; }
    }

    /**
     * Conversation Metadata
     */
    public static class ConversationMetadata {
        @Indexed
        private int messageCount = 0;
        
        private int unreadCount = 0;

        // Getters and Setters
        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    }
}
