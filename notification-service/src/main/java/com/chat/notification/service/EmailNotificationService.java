package com.chat.notification.service;

import com.chat.notification.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Email Notification Service
 * 
 * Handles email notifications using Spring Mail.
 */
@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send email notification
     * 
     * @param notification Notification to send
     */
    public void sendEmailNotification(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(getUserEmail(notification.getUserId()));
            message.setSubject(notification.getTitle());
            message.setText(notification.getBody());

            // Add additional data if available
            if (notification.getData() != null && !notification.getData().isEmpty()) {
                StringBuilder text = new StringBuilder(notification.getBody());
                text.append("\n\nAdditional Information:\n");
                for (Map.Entry<String, Object> entry : notification.getData().entrySet()) {
                    text.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                message.setText(text.toString());
            }

            mailSender.send(message);
            System.out.println("Email sent successfully to user " + notification.getUserId());

            // Mark as delivered
            notification.setDelivered(true);
            notification.setDeliveredAt(java.time.LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("Error sending email notification: " + e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    /**
     * Send welcome email
     * 
     * @param userEmail User email
     * @param userName User name
     */
    public void sendWelcomeEmail(String userEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Welcome to Chat App!");
            message.setText("Hello " + userName + ",\n\n" +
                    "Welcome to our chat application! We're excited to have you on board.\n\n" +
                    "You can now start chatting with your friends and family.\n\n" +
                    "Best regards,\n" +
                    "The Chat App Team");

            mailSender.send(message);
            System.out.println("Welcome email sent successfully to " + userEmail);

        } catch (Exception e) {
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }

    /**
     * Send password reset email
     * 
     * @param userEmail User email
     * @param resetToken Reset token
     */
    public void sendPasswordResetEmail(String userEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Password Reset Request");
            message.setText("Hello,\n\n" +
                    "You requested a password reset for your account.\n\n" +
                    "Please click the following link to reset your password:\n" +
                    "http://localhost:3000/reset-password?token=" + resetToken + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request this password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "The Chat App Team");

            mailSender.send(message);
            System.out.println("Password reset email sent successfully to " + userEmail);

        } catch (Exception e) {
            System.err.println("Error sending password reset email: " + e.getMessage());
        }
    }

    /**
     * Send email verification email
     * 
     * @param userEmail User email
     * @param verificationToken Verification token
     */
    public void sendEmailVerificationEmail(String userEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Verify Your Email Address");
            message.setText("Hello,\n\n" +
                    "Please verify your email address by clicking the following link:\n" +
                    "http://localhost:3000/verify-email?token=" + verificationToken + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you didn't create an account, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "The Chat App Team");

            mailSender.send(message);
            System.out.println("Email verification email sent successfully to " + userEmail);

        } catch (Exception e) {
            System.err.println("Error sending email verification email: " + e.getMessage());
        }
    }

    /**
     * Send friend request email
     * 
     * @param userEmail User email
     * @param requesterName Requester name
     */
    public void sendFriendRequestEmail(String userEmail, String requesterName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("New Friend Request");
            message.setText("Hello,\n\n" +
                    requesterName + " sent you a friend request on Chat App.\n\n" +
                    "You can accept or decline this request by logging into your account.\n\n" +
                    "Best regards,\n" +
                    "The Chat App Team");

            mailSender.send(message);
            System.out.println("Friend request email sent successfully to " + userEmail);

        } catch (Exception e) {
            System.err.println("Error sending friend request email: " + e.getMessage());
        }
    }

    /**
     * Get user email
     * 
     * @param userId User ID
     * @return User email
     */
    private String getUserEmail(String userId) {
        // TODO: Implement user email retrieval from database
        // This should query the user's email from the database
        // For now, return a mock email
        return "user" + userId + "@example.com";
    }
}
