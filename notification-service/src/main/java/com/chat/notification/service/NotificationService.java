package com.chat.notification.service;

import com.chat.notification.model.Notification;
import com.chat.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Notification Service
 * 
 * Service class for notification management operations including
 * creating, sending, and managing notifications.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    /**
     * Create a new notification
     * 
     * @param notification Notification to create
     * @return Created notification
     */
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    /**
     * Send a notification
     * 
     * @param notification Notification to send
     * @return Sent notification
     */
    public Notification sendNotification(Notification notification) {
        // Create notification
        notification = createNotification(notification);

        // Send based on delivery method
        switch (notification.getDeliveryMethod()) {
            case PUSH:
                pushNotificationService.sendPushNotification(notification);
                break;
            case EMAIL:
                emailNotificationService.sendEmailNotification(notification);
                break;
            case IN_APP:
                // In-app notifications are handled by WebSocket
                break;
        }

        return notification;
    }

    /**
     * Send message notification
     * 
     * @param userId User ID
     * @param messageData Message data
     * @return Created notification
     */
    public Notification sendMessageNotification(String userId, Map<String, Object> messageData) {
        Notification notification = new Notification(
                userId,
                Notification.NotificationType.MESSAGE,
                "New Message",
                (String) messageData.get("content"),
                Notification.DeliveryMethod.PUSH
        );

        notification.setData(messageData);
        notification.setPriority(Notification.Priority.NORMAL);

        return sendNotification(notification);
    }

    /**
     * Send friend request notification
     * 
     * @param userId User ID
     * @param requesterName Requester name
     * @return Created notification
     */
    public Notification sendFriendRequestNotification(String userId, String requesterName) {
        Notification notification = new Notification(
                userId,
                Notification.NotificationType.FRIEND_REQUEST,
                "Friend Request",
                requesterName + " sent you a friend request",
                Notification.DeliveryMethod.IN_APP
        );

        notification.setPriority(Notification.Priority.NORMAL);

        return sendNotification(notification);
    }

    /**
     * Send system notification
     * 
     * @param userId User ID
     * @param title Notification title
     * @param body Notification body
     * @return Created notification
     */
    public Notification sendSystemNotification(String userId, String title, String body) {
        Notification notification = new Notification(
                userId,
                Notification.NotificationType.SYSTEM,
                title,
                body,
                Notification.DeliveryMethod.IN_APP
        );

        notification.setPriority(Notification.Priority.HIGH);

        return sendNotification(notification);
    }

    /**
     * Get notifications by user ID
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of notifications
     */
    public Page<Notification> getNotificationsByUser(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get unread notifications by user ID
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of unread notifications
     */
    public Page<Notification> getUnreadNotificationsByUser(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false, pageable);
    }

    /**
     * Get notification by ID
     * 
     * @param notificationId Notification ID
     * @return Optional notification
     */
    public Optional<Notification> getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    /**
     * Mark notification as read
     * 
     * @param notificationId Notification ID
     * @return Updated notification
     */
    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Mark notification as delivered
     * 
     * @param notificationId Notification ID
     * @return Updated notification
     */
    public Notification markAsDelivered(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setDelivered(true);
        notification.setDeliveredAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for user
     * 
     * @param userId User ID
     * @return Number of notifications marked as read
     */
    public long markAllAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });

        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    /**
     * Delete notification
     * 
     * @param notificationId Notification ID
     */
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Delete old notifications
     * 
     * @param olderThan Delete notifications older than this date
     * @return Number of deleted notifications
     */
    public long deleteOldNotifications(LocalDateTime olderThan) {
        List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(olderThan);
        notificationRepository.deleteAll(oldNotifications);
        return oldNotifications.size();
    }

    /**
     * Get notification count by user
     * 
     * @param userId User ID
     * @return Notification count
     */
    public long getNotificationCountByUser(String userId) {
        return notificationRepository.countByUserId(userId);
    }

    /**
     * Get unread notification count by user
     * 
     * @param userId User ID
     * @return Unread notification count
     */
    public long getUnreadNotificationCountByUser(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    /**
     * Get notification statistics
     * 
     * @return Notification statistics
     */
    public NotificationStatistics getNotificationStatistics() {
        long totalNotifications = notificationRepository.count();
        long unreadNotifications = notificationRepository.countByIsRead(false);
        long deliveredNotifications = notificationRepository.countByIsDelivered(true);

        return new NotificationStatistics(totalNotifications, unreadNotifications, deliveredNotifications);
    }

    /**
     * Notification Statistics DTO
     */
    public static class NotificationStatistics {
        private final long totalNotifications;
        private final long unreadNotifications;
        private final long deliveredNotifications;

        public NotificationStatistics(long totalNotifications, long unreadNotifications, long deliveredNotifications) {
            this.totalNotifications = totalNotifications;
            this.unreadNotifications = unreadNotifications;
            this.deliveredNotifications = deliveredNotifications;
        }

        public long getTotalNotifications() {
            return totalNotifications;
        }

        public long getUnreadNotifications() {
            return unreadNotifications;
        }

        public long getDeliveredNotifications() {
            return deliveredNotifications;
        }
    }
}
