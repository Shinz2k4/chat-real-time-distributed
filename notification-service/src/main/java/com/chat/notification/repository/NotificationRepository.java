package com.chat.notification.repository;

import com.chat.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Repository
 * 
 * MongoDB repository for Notification entities with custom query methods
 * for notification retrieval and management.
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    /**
     * Find notifications by user ID ordered by creation date
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Find notifications by user ID and read status
     * 
     * @param userId User ID
     * @param isRead Read status
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(String userId, boolean isRead, Pageable pageable);

    /**
     * Find notifications by user ID and read status
     * 
     * @param userId User ID
     * @param isRead Read status
     * @return List of notifications
     */
    List<Notification> findByUserIdAndIsRead(String userId, boolean isRead);

    /**
     * Find notifications by user ID and type
     * 
     * @param userId User ID
     * @param type Notification type
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, Notification.NotificationType type, Pageable pageable);

    /**
     * Find notifications by user ID and delivery method
     * 
     * @param userId User ID
     * @param deliveryMethod Delivery method
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndDeliveryMethodOrderByCreatedAtDesc(String userId, Notification.DeliveryMethod deliveryMethod, Pageable pageable);

    /**
     * Find notifications by user ID and priority
     * 
     * @param userId User ID
     * @param priority Priority
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndPriorityOrderByCreatedAtDesc(String userId, Notification.Priority priority, Pageable pageable);

    /**
     * Find notifications by user ID and created after date
     * 
     * @param userId User ID
     * @param createdAtAfter Created after this date
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(String userId, LocalDateTime createdAtAfter, Pageable pageable);

    /**
     * Find notifications by user ID and created before date
     * 
     * @param userId User ID
     * @param createdAtBefore Created before this date
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(String userId, LocalDateTime createdAtBefore, Pageable pageable);

    /**
     * Find notifications by user ID and date range
     * 
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(String userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find notifications by type
     * 
     * @param type Notification type
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByTypeOrderByCreatedAtDesc(Notification.NotificationType type, Pageable pageable);

    /**
     * Find notifications by delivery method
     * 
     * @param deliveryMethod Delivery method
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByDeliveryMethodOrderByCreatedAtDesc(Notification.DeliveryMethod deliveryMethod, Pageable pageable);

    /**
     * Find notifications by priority
     * 
     * @param priority Priority
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByPriorityOrderByCreatedAtDesc(Notification.Priority priority, Pageable pageable);

    /**
     * Find notifications by read status
     * 
     * @param isRead Read status
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByIsReadOrderByCreatedAtDesc(boolean isRead, Pageable pageable);

    /**
     * Find notifications by delivered status
     * 
     * @param isDelivered Delivered status
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByIsDeliveredOrderByCreatedAtDesc(boolean isDelivered, Pageable pageable);

    /**
     * Find notifications created before date
     * 
     * @param createdAtBefore Created before this date
     * @return List of notifications
     */
    List<Notification> findByCreatedAtBefore(LocalDateTime createdAtBefore);

    /**
     * Find notifications by user ID and not delivered
     * 
     * @param userId User ID
     * @param isDelivered Delivered status
     * @return List of notifications
     */
    List<Notification> findByUserIdAndIsDelivered(String userId, boolean isDelivered);

    /**
     * Count notifications by user ID
     * 
     * @param userId User ID
     * @return Notification count
     */
    long countByUserId(String userId);

    /**
     * Count notifications by user ID and read status
     * 
     * @param userId User ID
     * @param isRead Read status
     * @return Notification count
     */
    long countByUserIdAndIsRead(String userId, boolean isRead);

    /**
     * Count notifications by user ID and delivered status
     * 
     * @param userId User ID
     * @param isDelivered Delivered status
     * @return Notification count
     */
    long countByUserIdAndIsDelivered(String userId, boolean isDelivered);

    /**
     * Count notifications by type
     * 
     * @param type Notification type
     * @return Notification count
     */
    long countByType(Notification.NotificationType type);

    /**
     * Count notifications by delivery method
     * 
     * @param deliveryMethod Delivery method
     * @return Notification count
     */
    long countByDeliveryMethod(Notification.DeliveryMethod deliveryMethod);

    /**
     * Count notifications by priority
     * 
     * @param priority Priority
     * @return Notification count
     */
    long countByPriority(Notification.Priority priority);

    /**
     * Count notifications by read status
     * 
     * @param isRead Read status
     * @return Notification count
     */
    long countByIsRead(boolean isRead);

    /**
     * Count notifications by delivered status
     * 
     * @param isDelivered Delivered status
     * @return Notification count
     */
    long countByIsDelivered(boolean isDelivered);

    /**
     * Find notifications by user ID and type and read status
     * 
     * @param userId User ID
     * @param type Notification type
     * @param isRead Read status
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndTypeAndIsReadOrderByCreatedAtDesc(String userId, Notification.NotificationType type, boolean isRead, Pageable pageable);

    /**
     * Find notifications by user ID and delivery method and read status
     * 
     * @param userId User ID
     * @param deliveryMethod Delivery method
     * @param isRead Read status
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndDeliveryMethodAndIsReadOrderByCreatedAtDesc(String userId, Notification.DeliveryMethod deliveryMethod, boolean isRead, Pageable pageable);

    /**
     * Find notifications by user ID and priority and read status
     * 
     * @param userId User ID
     * @param priority Priority
     * @param isRead Read status
     * @param pageable Pagination
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndPriorityAndIsReadOrderByCreatedAtDesc(String userId, Notification.Priority priority, boolean isRead, Pageable pageable);
}
