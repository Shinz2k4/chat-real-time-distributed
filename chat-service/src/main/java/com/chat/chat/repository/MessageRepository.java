package com.chat.chat.repository;

import com.chat.chat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Message Repository
 * 
 * MongoDB repository for Message entities with custom query methods
 * for message retrieval, search, and analytics.
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    /**
     * Find messages by conversation ID with pagination
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);

    /**
     * Find messages by conversation ID and not deleted
     * 
     * @param conversationId Conversation ID
     * @param isDeleted Deleted status
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdAndIsDeletedOrderByCreatedAtDesc(String conversationId, boolean isDeleted, Pageable pageable);

    /**
     * Find messages by conversation ID and status
     * 
     * @param conversationId Conversation ID
     * @param status Message status
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdAndStatusOrderByCreatedAtDesc(String conversationId, Message.MessageStatus status, Pageable pageable);

    /**
     * Find messages by sender ID
     * 
     * @param senderId Sender ID
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findBySenderIdOrderByCreatedAtDesc(String senderId, Pageable pageable);

    /**
     * Find messages by type
     * 
     * @param type Message type
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByTypeOrderByCreatedAtDesc(Message.MessageType type, Pageable pageable);

    /**
     * Find messages by conversation ID and created after date
     * 
     * @param conversationId Conversation ID
     * @param createdAtAfter Created after this date
     * @return List of messages
     */
    List<Message> findByConversationIdAndCreatedAtAfterOrderByCreatedAtAsc(String conversationId, LocalDateTime createdAtAfter);

    /**
     * Find messages by conversation ID and created before date
     * 
     * @param conversationId Conversation ID
     * @param createdAtBefore Created before this date
     * @return List of messages
     */
    List<Message> findByConversationIdAndCreatedAtBeforeOrderByCreatedAtDesc(String conversationId, LocalDateTime createdAtBefore);

    /**
     * Find messages by conversation ID and date range
     * 
     * @param conversationId Conversation ID
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdAndCreatedAtBetweenOrderByCreatedAtDesc(String conversationId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Search messages by content
     * 
     * @param conversationId Conversation ID
     * @param content Content to search
     * @param pageable Pagination
     * @return Page of messages
     */
    @Query("{ 'conversationId': ?0, 'content': { $regex: ?1, $options: 'i' }, 'isDeleted': false }")
    Page<Message> searchByContent(String conversationId, String content, Pageable pageable);

    /**
     * Find messages with attachments
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    @Query("{ 'conversationId': ?0, 'attachments': { $exists: true, $ne: [] }, 'isDeleted': false }")
    Page<Message> findMessagesWithAttachments(String conversationId, Pageable pageable);

    /**
     * Find messages with reactions
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    @Query("{ 'conversationId': ?0, 'reactions': { $exists: true, $ne: [] }, 'isDeleted': false }")
    Page<Message> findMessagesWithReactions(String conversationId, Pageable pageable);

    /**
     * Find messages by mention
     * 
     * @param conversationId Conversation ID
     * @param userId User ID mentioned
     * @param pageable Pagination
     * @return Page of messages
     */
    @Query("{ 'conversationId': ?0, 'mentions.userId': ?1, 'isDeleted': false }")
    Page<Message> findMessagesByMention(String conversationId, String userId, Pageable pageable);

    /**
     * Count messages by conversation ID
     * 
     * @param conversationId Conversation ID
     * @return Message count
     */
    long countByConversationId(String conversationId);

    /**
     * Count messages by conversation ID and not deleted
     * 
     * @param conversationId Conversation ID
     * @param isDeleted Deleted status
     * @return Message count
     */
    long countByConversationIdAndIsDeleted(String conversationId, boolean isDeleted);

    /**
     * Count messages by sender ID
     * 
     * @param senderId Sender ID
     * @return Message count
     */
    long countBySenderId(String senderId);

    /**
     * Count messages by type
     * 
     * @param type Message type
     * @return Message count
     */
    long countByType(Message.MessageType type);

    /**
     * Find latest message by conversation ID
     * 
     * @param conversationId Conversation ID
     * @return Latest message
     */
    @Query("{ 'conversationId': ?0, 'isDeleted': false }")
    Message findTopByConversationIdOrderByCreatedAtDesc(String conversationId);

    /**
     * Find messages by conversation ID and sender ID
     * 
     * @param conversationId Conversation ID
     * @param senderId Sender ID
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdAndSenderIdOrderByCreatedAtDesc(String conversationId, String senderId, Pageable pageable);

    /**
     * Find messages by conversation ID and status not equal
     * 
     * @param conversationId Conversation ID
     * @param status Message status to exclude
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdAndStatusNotOrderByCreatedAtDesc(String conversationId, Message.MessageStatus status, Pageable pageable);

    /**
     * Find messages created between dates
     * 
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find messages by multiple conversation IDs
     * 
     * @param conversationIds List of conversation IDs
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdInOrderByCreatedAtDesc(List<String> conversationIds, Pageable pageable);

    /**
     * Find messages by conversation ID and content containing
     * 
     * @param conversationId Conversation ID
     * @param content Content to search
     * @param pageable Pagination
     * @return Page of messages
     */
    Page<Message> findByConversationIdAndContentContainingIgnoreCaseOrderByCreatedAtDesc(String conversationId, String content, Pageable pageable);
}
