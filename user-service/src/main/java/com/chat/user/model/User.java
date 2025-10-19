package com.chat.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * User Entity
 * 
 * Represents a user in the chat application with all necessary
 * profile information, authentication data, and settings.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Display name is required")
    @Size(min = 1, max = 100, message = "Display name must be between 1 and 100 characters")
    private String displayName;

    private String avatar;
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Indexed
    private UserStatus status = UserStatus.OFFLINE;

    @Indexed
    private LocalDateTime lastSeen = LocalDateTime.now();

    private boolean isActive = true;

    private boolean emailVerified = false;

    private String verificationToken;

    private String passwordResetToken;

    private LocalDateTime passwordResetExpires;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private UserSettings settings = new UserSettings();

    private SocialConnections socialConnections = new SocialConnections();

    // Constructors
    public User() {}

    public User(String email, String username, String password, String displayName) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.displayName = displayName != null ? displayName : username; // Use username as displayName if not provided
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetExpires() {
        return passwordResetExpires;
    }

    public void setPasswordResetExpires(LocalDateTime passwordResetExpires) {
        this.passwordResetExpires = passwordResetExpires;
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

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public SocialConnections getSocialConnections() {
        return socialConnections;
    }

    public void setSocialConnections(SocialConnections socialConnections) {
        this.socialConnections = socialConnections;
    }

    /**
     * User Status Enum
     */
    public enum UserStatus {
        ONLINE, OFFLINE, AWAY, BUSY
    }

    /**
     * User Settings
     */
    public static class UserSettings {
        private NotificationSettings notifications = new NotificationSettings();
        private PrivacySettings privacy = new PrivacySettings();
        private String theme = "light";

        // Getters and Setters
        public NotificationSettings getNotifications() {
            return notifications;
        }

        public void setNotifications(NotificationSettings notifications) {
            this.notifications = notifications;
        }

        public PrivacySettings getPrivacy() {
            return privacy;
        }

        public void setPrivacy(PrivacySettings privacy) {
            this.privacy = privacy;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }
    }

    /**
     * Notification Settings
     */
    public static class NotificationSettings {
        private boolean email = true;
        private boolean push = true;
        private boolean inApp = true;

        // Getters and Setters
        public boolean isEmail() {
            return email;
        }

        public void setEmail(boolean email) {
            this.email = email;
        }

        public boolean isPush() {
            return push;
        }

        public void setPush(boolean push) {
            this.push = push;
        }

        public boolean isInApp() {
            return inApp;
        }

        public void setInApp(boolean inApp) {
            this.inApp = inApp;
        }
    }

    /**
     * Privacy Settings
     */
    public static class PrivacySettings {
        private boolean showOnlineStatus = true;
        private boolean showLastSeen = true;
        private boolean allowFriendRequests = true;

        // Getters and Setters
        public boolean isShowOnlineStatus() {
            return showOnlineStatus;
        }

        public void setShowOnlineStatus(boolean showOnlineStatus) {
            this.showOnlineStatus = showOnlineStatus;
        }

        public boolean isShowLastSeen() {
            return showLastSeen;
        }

        public void setShowLastSeen(boolean showLastSeen) {
            this.showLastSeen = showLastSeen;
        }

        public boolean isAllowFriendRequests() {
            return allowFriendRequests;
        }

        public void setAllowFriendRequests(boolean allowFriendRequests) {
            this.allowFriendRequests = allowFriendRequests;
        }
    }

    /**
     * Social Connections
     */
    public static class SocialConnections {
        private String googleId;
        private String facebookId;

        // Getters and Setters
        public String getGoogleId() {
            return googleId;
        }

        public void setGoogleId(String googleId) {
            this.googleId = googleId;
        }

        public String getFacebookId() {
            return facebookId;
        }

        public void setFacebookId(String facebookId) {
            this.facebookId = facebookId;
        }
    }
}
