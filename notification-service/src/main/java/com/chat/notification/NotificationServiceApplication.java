package com.chat.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Notification Service Application
 * 
 * This application handles notification management including:
 * - Push notifications (FCM)
 * - Email notifications
 * - In-app notifications
 * - Notification preferences
 * - Notification history
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableMongoAuditing
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
