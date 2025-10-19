package com.chat.user.controller;

import com.chat.user.model.User;
import com.chat.user.service.AuthService;
import com.chat.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller
 * 
 * Handles user authentication endpoints including login, registration,
 * password reset, and email verification.
 */
@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    /**
     * User registration
     * 
     * @param request Registration request
     * @return Registration response
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Check if email already exists
            if (userService.emailExists(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already exists"));
            }

            // Check if username already exists
            if (userService.usernameExists(request.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username already exists"));
            }

            // Create user
            User user = new User(
                    request.getEmail(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getDisplayName() != null ? request.getDisplayName() : request.getUsername()
            );

            user.setBio(request.getBio());
            user = userService.createUser(user);

            // Generate JWT token
            String token = authService.generateToken(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "username", user.getUsername(),
                            "displayName", user.getDisplayName(),
                            "emailVerified", user.isEmailVerified()
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * User login
     * 
     * @param request Login request
     * @return Login response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user
            User user = authService.authenticate(request.getEmail(), request.getPassword());
            
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid credentials"));
            }

            // Update presence status
            userService.updatePresenceStatus(user.getId(), User.UserStatus.ONLINE);

            // Generate JWT token
            String token = authService.generateToken(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "username", user.getUsername(),
                            "displayName", user.getDisplayName(),
                            "emailVerified", user.isEmailVerified(),
                            "status", user.getStatus().toString()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Refresh JWT token
     * 
     * @param request Refresh token request
     * @return New token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String newToken = authService.refreshToken(request.getToken());
            return ResponseEntity.ok(Map.of("token", newToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify email address
     * 
     * @param token Verification token
     * @return Verification response
     */
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            User user = userService.verifyEmail(token);
            return ResponseEntity.ok(Map.of(
                    "message", "Email verified successfully",
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "emailVerified", user.isEmailVerified()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Request password reset
     * 
     * @param request Password reset request
     * @return Reset response
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            String token = userService.generatePasswordResetToken(request.getEmail());
            
            // TODO: Send email with reset link
            // emailService.sendPasswordResetEmail(request.getEmail(), token);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Password reset email sent",
                    "token", token // Remove this in production
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reset password
     * 
     * @param request Reset password request
     * @return Reset response
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            User user = userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of(
                    "message", "Password reset successfully",
                    "user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Logout user
     * 
     * @param request Logout request
     * @return Logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            // Update presence status to offline
            String userId = authService.getUserIdFromToken(request.getToken());
            userService.updatePresenceStatus(userId, User.UserStatus.OFFLINE);
            
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Request DTOs
    public static class RegisterRequest {
        private String email;
        private String username;
        private String password;
        private String displayName;
        private String bio;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RefreshTokenRequest {
        private String token;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class ForgotPasswordRequest {
        private String email;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class LogoutRequest {
        private String token;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
