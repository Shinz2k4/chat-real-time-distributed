package com.chat.user.repository;

import com.chat.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * 
 * MongoDB repository for User entities with custom query methods
 * for user search, presence management, and social features.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by email
     * 
     * @param email User email
     * @return Optional User
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     * 
     * @param username User username
     * @return Optional User
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email and active status
     * 
     * @param email User email
     * @param isActive Active status
     * @return Optional User
     */
    Optional<User> findByEmailAndIsActive(String email, boolean isActive);

    /**
     * Find user by username and active status
     * 
     * @param username User username
     * @param isActive Active status
     * @return Optional User
     */
    Optional<User> findByUsernameAndIsActive(String username, boolean isActive);

    /**
     * Find user by verification token
     * 
     * @param verificationToken Verification token
     * @return Optional User
     */
    Optional<User> findByVerificationToken(String verificationToken);

    /**
     * Find user by password reset token
     * 
     * @param passwordResetToken Password reset token
     * @return Optional User
     */
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    /**
     * Find user by Google ID
     * 
     * @param googleId Google ID
     * @return Optional User
     */
    Optional<User> findBySocialConnectionsGoogleId(String googleId);

    /**
     * Find user by Facebook ID
     * 
     * @param facebookId Facebook ID
     * @return Optional User
     */
    Optional<User> findBySocialConnectionsFacebookId(String facebookId);

    /**
     * Search users by display name or username
     * 
     * @param searchTerm Search term
     * @return List of matching users
     */
    @Query("{ $or: [ " +
           "{ 'displayName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'username': { $regex: ?0, $options: 'i' } } " +
           "], 'isActive': true }")
    List<User> findByDisplayNameOrUsernameContainingIgnoreCase(String searchTerm);

    /**
     * Find users by status
     * 
     * @param status User status
     * @return List of users with specified status
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * Find users by status and last seen after specified time
     * 
     * @param status User status
     * @param lastSeenAfter Last seen after this time
     * @return List of users
     */
    List<User> findByStatusAndLastSeenAfter(User.UserStatus status, LocalDateTime lastSeenAfter);

    /**
     * Find users created after specified date
     * 
     * @param createdAtAfter Created after this date
     * @return List of users
     */
    List<User> findByCreatedAtAfter(LocalDateTime createdAtAfter);

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     * 
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists and user is active
     * 
     * @param email Email to check
     * @param isActive Active status
     * @return true if exists and active, false otherwise
     */
    boolean existsByEmailAndIsActive(String email, boolean isActive);

    /**
     * Check if username exists and user is active
     * 
     * @param username Username to check
     * @param isActive Active status
     * @return true if exists and active, false otherwise
     */
    boolean existsByUsernameAndIsActive(String username, boolean isActive);

    /**
     * Find users with expired password reset tokens
     * 
     * @param currentTime Current time
     * @return List of users with expired tokens
     */
    @Query("{ 'passwordResetExpires': { $lt: ?0 } }")
    List<User> findUsersWithExpiredPasswordResetTokens(LocalDateTime currentTime);

    /**
     * Count active users
     * 
     * @return Number of active users
     */
    long countByIsActiveTrue();

    /**
     * Count users by status
     * 
     * @param status User status
     * @return Number of users with specified status
     */
    long countByStatus(User.UserStatus status);
}
