package com.chat.user.service;

import com.chat.user.model.User;
import com.chat.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * 
 * Service class for user management operations including
 * authentication, profile management, and social features.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     * 
     * @param user User to create
     * @return Created user
     */
    public User createUser(User user) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Generate verification token
        user.setVerificationToken(UUID.randomUUID().toString());
        
        // Set default values
        user.setStatus(User.UserStatus.OFFLINE);
        user.setLastSeen(LocalDateTime.now());
        user.setActive(true);
        user.setEmailVerified(false);
        
        return userRepository.save(user);
    }

    /**
     * Find user by ID
     * 
     * @param id User ID
     * @return Optional User
     */
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     * 
     * @param email User email
     * @return Optional User
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by username
     * 
     * @param username User username
     * @return Optional User
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find active user by email
     * 
     * @param email User email
     * @return Optional User
     */
    public Optional<User> findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndIsActive(email, true);
    }

    /**
     * Find active user by username
     * 
     * @param username User username
     * @return Optional User
     */
    public Optional<User> findActiveUserByUsername(String username) {
        return userRepository.findByUsernameAndIsActive(username, true);
    }

    /**
     * Update user profile
     * 
     * @param user User to update
     * @return Updated user
     */
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Update user password
     * 
     * @param userId User ID
     * @param newPassword New password
     * @return Updated user
     */
    public User updatePassword(String userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Verify user email
     * 
     * @param verificationToken Verification token
     * @return Updated user
     */
    public User verifyEmail(String verificationToken) {
        User user = userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Generate password reset token
     * 
     * @param email User email
     * @return Password reset token
     */
    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        return token;
    }

    /**
     * Reset password using token
     * 
     * @param token Password reset token
     * @param newPassword New password
     * @return Updated user
     */
    public User resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Update user presence status
     * 
     * @param userId User ID
     * @param status New status
     * @return Updated user
     */
    public User updatePresenceStatus(String userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(status);
        user.setLastSeen(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Search users
     * 
     * @param searchTerm Search term
     * @param pageable Pagination
     * @return Page of users
     */
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        List<User> users = userRepository.findByDisplayNameOrUsernameContainingIgnoreCase(searchTerm);
        // Convert List to Page manually since repository method doesn't support Pageable
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        List<User> pageContent = users.subList(start, end);
        return new PageImpl<>(pageContent, pageable, users.size());
    }

    /**
     * Get online users
     * 
     * @return List of online users
     */
    public List<User> getOnlineUsers() {
        return userRepository.findByStatus(User.UserStatus.ONLINE);
    }

    /**
     * Get users by status
     * 
     * @param status User status
     * @return List of users
     */
    public List<User> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status);
    }

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if username exists
     * 
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Deactivate user
     * 
     * @param userId User ID
     * @return Updated user
     */
    public User deactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setActive(false);
        user.setStatus(User.UserStatus.OFFLINE);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Get user statistics
     * 
     * @return User statistics
     */
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        long onlineUsers = userRepository.countByStatus(User.UserStatus.ONLINE);
        
        return new UserStatistics(totalUsers, activeUsers, onlineUsers);
    }

    /**
     * User Statistics DTO
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long onlineUsers;

        public UserStatistics(long totalUsers, long activeUsers, long onlineUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.onlineUsers = onlineUsers;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getOnlineUsers() {
            return onlineUsers;
        }
    }
}
