package com.chat.user.controller;

import com.chat.user.model.User;
import com.chat.user.service.UserService;
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
 * User Controller
 * 
 * Handles user profile management and user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get user profile
     * 
     * @param userId User ID
     * @return User profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("displayName", user.getDisplayName());
            response.put("avatar", user.getAvatar());
            response.put("bio", user.getBio());
            response.put("status", user.getStatus().toString());
            response.put("lastSeen", user.getLastSeen());
            response.put("emailVerified", user.isEmailVerified());
            response.put("createdAt", user.getCreatedAt());
            response.put("settings", user.getSettings());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update user profile
     * 
     * @param userId User ID
     * @param request Update request
     * @return Updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, 
                                             @Valid @RequestBody UpdateProfileRequest request) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update profile fields
            if (request.getDisplayName() != null) {
                user.setDisplayName(request.getDisplayName());
            }
            if (request.getBio() != null) {
                user.setBio(request.getBio());
            }
            if (request.getAvatar() != null) {
                user.setAvatar(request.getAvatar());
            }
            
            // Update settings
            if (request.getSettings() != null) {
                if (user.getSettings() == null) {
                    user.setSettings(new User.UserSettings());
                }
                
                if (request.getSettings().getNotifications() != null) {
                    if (user.getSettings().getNotifications() == null) {
                        user.getSettings().setNotifications(new User.NotificationSettings());
                    }
                    
                    NotificationSettingsRequest notifSettings = request.getSettings().getNotifications();
                    if (notifSettings.isEmail() != null) {
                        user.getSettings().getNotifications().setEmail(notifSettings.isEmail());
                    }
                    if (notifSettings.isPush() != null) {
                        user.getSettings().getNotifications().setPush(notifSettings.isPush());
                    }
                    if (notifSettings.isInApp() != null) {
                        user.getSettings().getNotifications().setInApp(notifSettings.isInApp());
                    }
                }
                
                if (request.getSettings().getPrivacy() != null) {
                    if (user.getSettings().getPrivacy() == null) {
                        user.getSettings().setPrivacy(new User.PrivacySettings());
                    }
                    
                    PrivacySettingsRequest privacySettings = request.getSettings().getPrivacy();
                    if (privacySettings.isShowOnlineStatus() != null) {
                        user.getSettings().getPrivacy().setShowOnlineStatus(privacySettings.isShowOnlineStatus());
                    }
                    if (privacySettings.isShowLastSeen() != null) {
                        user.getSettings().getPrivacy().setShowLastSeen(privacySettings.isShowLastSeen());
                    }
                    if (privacySettings.isAllowFriendRequests() != null) {
                        user.getSettings().getPrivacy().setAllowFriendRequests(privacySettings.isAllowFriendRequests());
                    }
                }
                
                if (request.getSettings().getTheme() != null) {
                    user.getSettings().setTheme(request.getSettings().getTheme());
                }
            }
            
            user = userService.updateUser(user);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "username", user.getUsername(),
                            "displayName", user.getDisplayName(),
                            "avatar", user.getAvatar(),
                            "bio", user.getBio(),
                            "status", user.getStatus().toString(),
                            "settings", user.getSettings()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Search users
     * 
     * @param query Search query
     * @param pageable Pagination
     * @return Page of users
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query, Pageable pageable) {
        try {
            Page<User> users = userService.searchUsers(query, pageable);
            
            return ResponseEntity.ok(Map.of(
                    "users", users.getContent().stream().map(user -> Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "displayName", user.getDisplayName(),
                            "avatar", user.getAvatar(),
                            "status", user.getStatus().toString(),
                            "lastSeen", user.getLastSeen()
                    )),
                    "totalElements", users.getTotalElements(),
                    "totalPages", users.getTotalPages(),
                    "currentPage", users.getNumber(),
                    "size", users.getSize()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get online users
     * 
     * @return List of online users
     */
    @GetMapping("/online")
    public ResponseEntity<?> getOnlineUsers() {
        try {
            List<User> onlineUsers = userService.getOnlineUsers();
            
            return ResponseEntity.ok(Map.of(
                    "users", onlineUsers.stream().map(user -> Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "displayName", user.getDisplayName(),
                            "avatar", user.getAvatar(),
                            "status", user.getStatus().toString(),
                            "lastSeen", user.getLastSeen()
                    ))
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update user presence status
     * 
     * @param userId User ID
     * @param request Status update request
     * @return Updated status
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable String userId, 
                                            @RequestBody UpdateStatusRequest request) {
        try {
            User user = userService.updatePresenceStatus(userId, request.getStatus());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Status updated successfully",
                    "user", Map.of(
                            "id", user.getId(),
                            "status", user.getStatus().toString(),
                            "lastSeen", user.getLastSeen()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get user statistics
     * 
     * @return User statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            UserService.UserStatistics stats = userService.getUserStatistics();
            
            return ResponseEntity.ok(Map.of(
                    "totalUsers", stats.getTotalUsers(),
                    "activeUsers", stats.getActiveUsers(),
                    "onlineUsers", stats.getOnlineUsers()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Request DTOs
    public static class UpdateProfileRequest {
        private String displayName;
        private String bio;
        private String avatar;
        private UserSettingsRequest settings;

        // Getters and Setters
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public UserSettingsRequest getSettings() { return settings; }
        public void setSettings(UserSettingsRequest settings) { this.settings = settings; }
    }

    public static class UserSettingsRequest {
        private NotificationSettingsRequest notifications;
        private PrivacySettingsRequest privacy;
        private String theme;

        // Getters and Setters
        public NotificationSettingsRequest getNotifications() { return notifications; }
        public void setNotifications(NotificationSettingsRequest notifications) { this.notifications = notifications; }
        public PrivacySettingsRequest getPrivacy() { return privacy; }
        public void setPrivacy(PrivacySettingsRequest privacy) { this.privacy = privacy; }
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
    }

    public static class NotificationSettingsRequest {
        private Boolean email;
        private Boolean push;
        private Boolean inApp;

        // Getters and Setters
        public Boolean isEmail() { return email; }
        public void setEmail(Boolean email) { this.email = email; }
        public Boolean isPush() { return push; }
        public void setPush(Boolean push) { this.push = push; }
        public Boolean isInApp() { return inApp; }
        public void setInApp(Boolean inApp) { this.inApp = inApp; }
    }

    public static class PrivacySettingsRequest {
        private Boolean showOnlineStatus;
        private Boolean showLastSeen;
        private Boolean allowFriendRequests;

        // Getters and Setters
        public Boolean isShowOnlineStatus() { return showOnlineStatus; }
        public void setShowOnlineStatus(Boolean showOnlineStatus) { this.showOnlineStatus = showOnlineStatus; }
        public Boolean isShowLastSeen() { return showLastSeen; }
        public void setShowLastSeen(Boolean showLastSeen) { this.showLastSeen = showLastSeen; }
        public Boolean isAllowFriendRequests() { return allowFriendRequests; }
        public void setAllowFriendRequests(Boolean allowFriendRequests) { this.allowFriendRequests = allowFriendRequests; }
    }

    public static class UpdateStatusRequest {
        private User.UserStatus status;

        // Getters and Setters
        public User.UserStatus getStatus() { return status; }
        public void setStatus(User.UserStatus status) { this.status = status; }
    }
}
