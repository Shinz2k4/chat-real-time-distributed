package com.chat.notification.controller;

import com.chat.notification.model.Notification;
import com.chat.notification.service.NotificationService;
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
 * Notification Controller
 * 
 * Handles notification-related REST endpoints for notification management.
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Get notifications by user ID
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of notifications
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getNotificationsByUser(@PathVariable String userId, Pageable pageable) {
        try {
            Page<Notification> notifications = notificationService.getNotificationsByUser(userId, pageable);
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("notifications", notifications.getContent().stream().map(NotificationController.this::mapNotificationToResponse).toList());
                    put("totalElements", notifications.getTotalElements());
                    put("totalPages", notifications.getTotalPages());
                    put("currentPage", notifications.getNumber());
                    put("size", notifications.getSize());
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Get unread notifications by user ID
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of unread notifications
     */
    @GetMapping("/users/{userId}/unread")
    public ResponseEntity<?> getUnreadNotificationsByUser(@PathVariable String userId, Pageable pageable) {
        try {
            Page<Notification> notifications = notificationService.getUnreadNotificationsByUser(userId, pageable);
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("notifications", notifications.getContent().stream().map(NotificationController.this::mapNotificationToResponse).toList());
                    put("totalElements", notifications.getTotalElements());
                    put("totalPages", notifications.getTotalPages());
                    put("currentPage", notifications.getNumber());
                    put("size", notifications.getSize());
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Get notification by ID
     * 
     * @param notificationId Notification ID
     * @return Notification response
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<?> getNotification(@PathVariable String notificationId) {
        try {
            Notification notification = notificationService.getNotificationById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("notification", mapNotificationToResponse(notification));
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Mark notification as read
     * 
     * @param notificationId Notification ID
     * @return Updated notification response
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("message", "Notification marked as read");
                    put("notification", mapNotificationToResponse(notification));
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Mark all notifications as read for user
     * 
     * @param userId User ID
     * @return Mark as read response
     */
    @PutMapping("/users/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
        try {
            long count = notificationService.markAllAsRead(userId);
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("message", "All notifications marked as read");
                    put("count", count);
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Delete notification
     * 
     * @param notificationId Notification ID
     * @return Deletion response
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("message", "Notification deleted successfully");
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Get notification count by user
     * 
     * @param userId User ID
     * @return Notification count
     */
    @GetMapping("/users/{userId}/count")
    public ResponseEntity<?> getNotificationCount(@PathVariable String userId) {
        try {
            long totalCount = notificationService.getNotificationCountByUser(userId);
            long unreadCount = notificationService.getUnreadNotificationCountByUser(userId);
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("totalCount", totalCount);
                    put("unreadCount", unreadCount);
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Get notification statistics
     * 
     * @return Notification statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getNotificationStatistics() {
        try {
            NotificationService.NotificationStatistics stats = notificationService.getNotificationStatistics();
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("totalNotifications", stats.getTotalNotifications());
                    put("unreadNotifications", stats.getUnreadNotifications());
                    put("deliveredNotifications", stats.getDeliveredNotifications());
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Send message notification
     * 
     * @param request Send notification request
     * @return Notification response
     */
    @PostMapping("/send/message")
    public ResponseEntity<?> sendMessageNotification(@Valid @RequestBody SendMessageNotificationRequest request) {
        try {
            Notification notification = notificationService.sendMessageNotification(
                    request.getUserId(),
                    request.getMessageData()
            );
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("message", "Message notification sent successfully");
                    put("notification", mapNotificationToResponse(notification));
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Send friend request notification
     * 
     * @param request Send notification request
     * @return Notification response
     */
    @PostMapping("/send/friend-request")
    public ResponseEntity<?> sendFriendRequestNotification(@Valid @RequestBody SendFriendRequestNotificationRequest request) {
        try {
            Notification notification = notificationService.sendFriendRequestNotification(
                    request.getUserId(),
                    request.getRequesterName()
            );
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("message", "Friend request notification sent successfully");
                    put("notification", mapNotificationToResponse(notification));
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Send system notification
     * 
     * @param request Send notification request
     * @return Notification response
     */
    @PostMapping("/send/system")
    public ResponseEntity<?> sendSystemNotification(@Valid @RequestBody SendSystemNotificationRequest request) {
        try {
            Notification notification = notificationService.sendSystemNotification(
                    request.getUserId(),
                    request.getTitle(),
                    request.getBody()
            );
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("message", "System notification sent successfully");
                    put("notification", mapNotificationToResponse(notification));
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, Object>() {{put("error", e.getMessage());}});
        }
    }

    /**
     * Map notification to response DTO
     * 
     * @param notification Notification entity
     * @return Response DTO
     */
    private Map<String, Object> mapNotificationToResponse(Notification notification) {
        return new HashMap<String, Object>() {{
                put("id", notification.getId());
                put("userId", notification.getUserId());
                put("type", notification.getType().toString());
                put("title", notification.getTitle());
                put("body", notification.getBody());
                put("data", notification.getData() != null ? notification.getData() : new HashMap<String, Object>());
                put("isRead", notification.isRead());
                put("isDelivered", notification.isDelivered());
                put("deliveryMethod", notification.getDeliveryMethod().toString());
                put("priority", notification.getPriority().toString());
                put("expiresAt", notification.getExpiresAt());
                put("createdAt", notification.getCreatedAt());
                put("readAt", notification.getReadAt());
                put("deliveredAt", notification.getDeliveredAt());
        }};
    }

    // Request DTOs
    public static class SendMessageNotificationRequest {
        private String userId;
        private Map<String, Object> messageData;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Map<String, Object> getMessageData() { return messageData; }
        public void setMessageData(Map<String, Object> messageData) { this.messageData = messageData; }
    }

    public static class SendFriendRequestNotificationRequest {
        private String userId;
        private String requesterName;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getRequesterName() { return requesterName; }
        public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    }

    public static class SendSystemNotificationRequest {
        private String userId;
        private String title;
        private String body;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}
