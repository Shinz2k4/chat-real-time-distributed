package com.chat.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Notification Entity
 * 
 * Represents a notification in the chat application with all necessary
 * metadata for delivery tracking and user preferences.
 */
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @NotNull(message = "User ID is required")
    @Indexed
    private String userId;

    @NotNull(message = "Notification type is required")
    @Indexed
    private NotificationType type;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Body is required")
    @Size(max = 500, message = "Body must not exceed 500 characters")
    private String body;

    private Map<String, Object> data;

    @Indexed
    private boolean isRead = false;

    @Indexed
    private boolean isDelivered = false;

    @NotNull(message = "Delivery method is required")
    private DeliveryMethod deliveryMethod;

    @NotNull(message = "Priority is required")
    private Priority priority = Priority.NORMAL;

    private LocalDateTime expiresAt;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private LocalDateTime deliveredAt;

    // Constructors
    public Notification() {}

    public Notification(String userId, NotificationType type, String title, String body, DeliveryMethod deliveryMethod) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.deliveryMethod = deliveryMethod;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    /**
     * Notification Type Enum
     */
    public enum NotificationType {
        MESSAGE, FRIEND_REQUEST, SYSTEM, CONVERSATION_INVITE, MESSAGE_REACTION
    }

    /**
     * Delivery Method Enum
     */
    public enum DeliveryMethod {
        IN_APP, PUSH, EMAIL
    }

    /**
     * Priority Enum
     */
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}
