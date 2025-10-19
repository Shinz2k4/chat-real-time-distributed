package com.chat.notification.service;

import com.chat.notification.model.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Push Notification Service
 * 
 * Handles push notifications using Firebase Cloud Messaging (FCM).
 */
@Service
public class PushNotificationService {

    @Autowired(required = false)
    private FirebaseMessaging firebaseMessaging;

    /**
     * Send push notification
     * 
     * @param notification Notification to send
     */
    public void sendPushNotification(Notification notification) {
        if (firebaseMessaging == null) {
            System.out.println("Firebase not configured, skipping push notification");
            return;
        }
        
        try {
            // Create FCM message
            // Convert Map<String, Object> to Map<String, String> for FCM
            Map<String, String> dataMap = new java.util.HashMap<>();
            if (notification.getData() != null) {
                notification.getData().forEach((key, value) -> 
                    dataMap.put(key, value != null ? value.toString() : "")
                );
            }
            
            Message message = Message.builder()
                    .setToken(getUserFCMToken(notification.getUserId()))
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(notification.getTitle())
                            .setBody(notification.getBody())
                            .build())
                    .putAllData(dataMap)
                    .build();

            // Send message
            String response = firebaseMessaging.send(message);
            System.out.println("Successfully sent message: " + response);

            // Mark as delivered
            notification.setDelivered(true);
            notification.setDeliveredAt(java.time.LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("Error sending push notification: " + e.getMessage());
            throw new RuntimeException("Failed to send push notification", e);
        }
    }

    /**
     * Send push notification to multiple users
     * 
     * @param userIds List of user IDs
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data
     */
    public void sendPushNotificationToUsers(java.util.List<String> userIds, String title, String body, Map<String, String> data) {
        for (String userId : userIds) {
            try {
                Message message = Message.builder()
                        .setToken(getUserFCMToken(userId))
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putAllData(data)
                        .build();

                firebaseMessaging.send(message);
            } catch (Exception e) {
                System.err.println("Error sending push notification to user " + userId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Send push notification to topic
     * 
     * @param topic Topic name
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data
     */
    public void sendPushNotificationToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .build();

            String response = firebaseMessaging.send(message);
            System.out.println("Successfully sent message to topic " + topic + ": " + response);

        } catch (Exception e) {
            System.err.println("Error sending push notification to topic " + topic + ": " + e.getMessage());
            throw new RuntimeException("Failed to send push notification to topic", e);
        }
    }

    /**
     * Subscribe user to topic
     * 
     * @param userId User ID
     * @param topic Topic name
     */
    public void subscribeUserToTopic(String userId, String topic) {
        try {
            String fcmToken = getUserFCMToken(userId);
            if (fcmToken != null) {
                firebaseMessaging.subscribeToTopic(java.util.List.of(fcmToken), topic);
                System.out.println("Successfully subscribed user " + userId + " to topic " + topic);
            }
        } catch (Exception e) {
            System.err.println("Error subscribing user " + userId + " to topic " + topic + ": " + e.getMessage());
        }
    }

    /**
     * Unsubscribe user from topic
     * 
     * @param userId User ID
     * @param topic Topic name
     */
    public void unsubscribeUserFromTopic(String userId, String topic) {
        try {
            String fcmToken = getUserFCMToken(userId);
            if (fcmToken != null) {
                firebaseMessaging.unsubscribeFromTopic(java.util.List.of(fcmToken), topic);
                System.out.println("Successfully unsubscribed user " + userId + " from topic " + topic);
            }
        } catch (Exception e) {
            System.err.println("Error unsubscribing user " + userId + " from topic " + topic + ": " + e.getMessage());
        }
    }

    /**
     * Get user FCM token
     * 
     * @param userId User ID
     * @return FCM token
     */
    private String getUserFCMToken(String userId) {
        // TODO: Implement FCM token retrieval from database
        // This should query the user's FCM token from the database
        // For now, return a mock token
        return "mock_fcm_token_" + userId;
    }
}
