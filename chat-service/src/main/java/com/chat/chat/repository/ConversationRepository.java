package com.chat.chat.repository;

import com.chat.chat.model.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Conversation Repository
 * 
 * MongoDB repository for Conversation entities with custom query methods
 * for conversation management and user participation.
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    /**
     * Find conversations by participant user ID
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0 }")
    Page<Conversation> findByParticipantUserId(String userId, Pageable pageable);

    /**
     * Find conversations by participant user ID and not archived
     * 
     * @param userId User ID
     * @param isArchived Archived status
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0, 'settings.isArchived': ?1 }")
    Page<Conversation> findByParticipantUserIdAndArchived(String userId, boolean isArchived, Pageable pageable);

    /**
     * Find conversations by participant user ID ordered by last message
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0 }")
    Page<Conversation> findByParticipantUserIdOrderByLastMessageTimestampDesc(String userId, Pageable pageable);

    /**
     * Find direct conversation between two users
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Optional conversation
     */
    @Query("{ 'type': 'DIRECT', 'participants.userId': { $all: [?0, ?1] } }")
    Optional<Conversation> findDirectConversationBetweenUsers(String userId1, String userId2);

    /**
     * Find conversations by type
     * 
     * @param type Conversation type
     * @param pageable Pagination
     * @return Page of conversations
     */
    Page<Conversation> findByTypeOrderByCreatedAtDesc(Conversation.ConversationType type, Pageable pageable);

    /**
     * Find conversations by creator
     * 
     * @param createdBy Creator user ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    Page<Conversation> findByCreatedByOrderByCreatedAtDesc(String createdBy, Pageable pageable);

    /**
     * Find conversations by name containing
     * 
     * @param name Name to search
     * @param pageable Pagination
     * @return Page of conversations
     */
    Page<Conversation> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name, Pageable pageable);

    /**
     * Find conversations by description containing
     * 
     * @param description Description to search
     * @param pageable Pagination
     * @return Page of conversations
     */
    Page<Conversation> findByDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(String description, Pageable pageable);

    /**
     * Find conversations created after date
     * 
     * @param createdAtAfter Created after this date
     * @param pageable Pagination
     * @return Page of conversations
     */
    Page<Conversation> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAtAfter, Pageable pageable);

    /**
     * Find conversations updated after date
     * 
     * @param updatedAtAfter Updated after this date
     * @param pageable Pagination
     * @return Page of conversations
     */
    Page<Conversation> findByUpdatedAtAfterOrderByUpdatedAtDesc(LocalDateTime updatedAtAfter, Pageable pageable);

    /**
     * Find conversations with unread messages for user
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0, 'metadata.unreadCount': { $gt: 0 } }")
    Page<Conversation> findConversationsWithUnreadMessages(String userId, Pageable pageable);

    /**
     * Find conversations by participant role
     * 
     * @param userId User ID
     * @param role Participant role
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0, 'participants.role': ?1 }")
    Page<Conversation> findByParticipantUserIdAndRole(String userId, Conversation.ParticipantRole role, Pageable pageable);

    /**
     * Find conversations by multiple participant user IDs
     * 
     * @param userIds List of user IDs
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': { $in: ?0 } }")
    Page<Conversation> findByParticipantUserIds(List<String> userIds, Pageable pageable);

    /**
     * Find conversations by name or description containing
     * 
     * @param searchTerm Search term
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ $or: [ " +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Conversation> findByNameOrDescriptionContainingIgnoreCase(String searchTerm, Pageable pageable);

    /**
     * Count conversations by participant user ID
     * 
     * @param userId User ID
     * @return Conversation count
     */
    @Query(value = "{ 'participants.userId': ?0 }", count = true)
    long countByParticipantUserId(String userId);

    /**
     * Count conversations by type
     * 
     * @param type Conversation type
     * @return Conversation count
     */
    long countByType(Conversation.ConversationType type);

    /**
     * Count conversations by creator
     * 
     * @param createdBy Creator user ID
     * @return Conversation count
     */
    long countByCreatedBy(String createdBy);

    /**
     * Count conversations with unread messages for user
     * 
     * @param userId User ID
     * @return Conversation count
     */
    @Query(value = "{ 'participants.userId': ?0, 'metadata.unreadCount': { $gt: 0 } }", count = true)
    long countConversationsWithUnreadMessages(String userId);

    /**
     * Find conversations by participant user ID and type
     * 
     * @param userId User ID
     * @param type Conversation type
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0, 'type': ?1 }")
    Page<Conversation> findByParticipantUserIdAndType(String userId, Conversation.ConversationType type, Pageable pageable);

    /**
     * Find conversations by participant user ID and muted status
     * 
     * @param userId User ID
     * @param isMuted Muted status
     * @param pageable Pagination
     * @return Page of conversations
     */
    @Query("{ 'participants.userId': ?0, 'settings.isMuted': ?1 }")
    Page<Conversation> findByParticipantUserIdAndMuted(String userId, boolean isMuted, Pageable pageable);

}
