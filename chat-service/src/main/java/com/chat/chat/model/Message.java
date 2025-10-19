package com.chat.chat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Message Entity
 * 
 * Represents a message in the chat application with all necessary
 * metadata for real-time communication and persistence.
 */
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    @NotNull(message = "Conversation ID is required")
    @Indexed
    private String conversationId;

    @NotNull(message = "Sender ID is required")
    @Indexed
    private String senderId;

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @NotNull(message = "Message type is required")
    @Indexed
    private MessageType type = MessageType.TEXT;

    @Indexed
    private MessageStatus status = MessageStatus.SENT;

    private ReplyTo replyTo;

    private List<Attachment> attachments;

    private List<Reaction> reactions;

    private List<Mention> mentions;

    private boolean isEdited = false;

    private LocalDateTime editedAt;

    @Indexed
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public Message() {}

    public Message(String conversationId, String senderId, String content, MessageType type) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public ReplyTo getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(ReplyTo replyTo) {
        this.replyTo = replyTo;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
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

    /**
     * Message Type Enum
     */
    public enum MessageType {
        TEXT, IMAGE, VIDEO, FILE, SYSTEM
    }

    /**
     * Message Status Enum
     */
    public enum MessageStatus {
        SENT, DELIVERED, SEEN
    }

    /**
     * Reply To Information
     */
    public static class ReplyTo {
        private String messageId;
        private String senderId;
        private String content;

        // Constructors
        public ReplyTo() {}

        public ReplyTo(String messageId, String senderId, String content) {
            this.messageId = messageId;
            this.senderId = senderId;
            this.content = content;
        }

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    /**
     * Attachment Information
     */
    public static class Attachment {
        private String fileId;
        private String fileName;
        private long fileSize;
        private String mimeType;
        private String url;
        private String thumbnailUrl;

        // Constructors
        public Attachment() {}

        public Attachment(String fileId, String fileName, long fileSize, String mimeType, String url) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.mimeType = mimeType;
            this.url = url;
        }

        // Getters and Setters
        public String getFileId() { return fileId; }
        public void setFileId(String fileId) { this.fileId = fileId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    }

    /**
     * Reaction Information
     */
    public static class Reaction {
        private String userId;
        private String emoji;
        private LocalDateTime timestamp;

        // Constructors
        public Reaction() {}

        public Reaction(String userId, String emoji) {
            this.userId = userId;
            this.emoji = emoji;
            this.timestamp = LocalDateTime.now();
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getEmoji() { return emoji; }
        public void setEmoji(String emoji) { this.emoji = emoji; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    /**
     * Mention Information
     */
    public static class Mention {
        private String userId;
        private String username;
        private int position;

        // Constructors
        public Mention() {}

        public Mention(String userId, String username, int position) {
            this.userId = userId;
            this.username = username;
            this.position = position;
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
    }
}
