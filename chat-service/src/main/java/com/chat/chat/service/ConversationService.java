package com.chat.chat.service;

import com.chat.chat.model.Conversation;
import com.chat.chat.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Conversation Service
 * 
 * Service class for conversation management operations including
 * creating, updating, and managing conversations and participants.
 */
@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    /**
     * Create a new conversation
     * 
     * @param conversation Conversation to create
     * @return Created conversation
     */
    public Conversation createConversation(Conversation conversation) {
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        
        // Initialize metadata
        if (conversation.getMetadata() == null) {
            conversation.setMetadata(new Conversation.ConversationMetadata());
        }
        
        // Initialize settings
        if (conversation.getSettings() == null) {
            conversation.setSettings(new Conversation.ConversationSettings());
        }
        
        return conversationRepository.save(conversation);
    }

    /**
     * Create a direct conversation between two users
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Created or existing conversation
     */
    public Conversation createDirectConversation(String userId1, String userId2) {
        // Check if direct conversation already exists
        Optional<Conversation> existingConversation = conversationRepository
                .findDirectConversationBetweenUsers(userId1, userId2);
        
        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }
        
        // Create new direct conversation
        Conversation conversation = new Conversation(Conversation.ConversationType.DIRECT, userId1);
        
        // Add participants
        List<Conversation.Participant> participants = List.of(
                new Conversation.Participant(userId1, Conversation.ParticipantRole.ADMIN),
                new Conversation.Participant(userId2, Conversation.ParticipantRole.MEMBER)
        );
        conversation.setParticipants(participants);
        
        return createConversation(conversation);
    }

    /**
     * Create a group conversation
     * 
     * @param name Group name
     * @param description Group description
     * @param createdBy Creator user ID
     * @param participantIds List of participant user IDs
     * @return Created conversation
     */
    public Conversation createGroupConversation(String name, String description, String createdBy, List<String> participantIds) {
        Conversation conversation = new Conversation(Conversation.ConversationType.GROUP, createdBy);
        conversation.setName(name);
        conversation.setDescription(description);
        
        // Add participants
        List<Conversation.Participant> participants = participantIds.stream()
                .map(userId -> new Conversation.Participant(userId, 
                        userId.equals(createdBy) ? Conversation.ParticipantRole.ADMIN : Conversation.ParticipantRole.MEMBER))
                .toList();
        conversation.setParticipants(participants);
        
        return createConversation(conversation);
    }

    /**
     * Get conversation by ID
     * 
     * @param conversationId Conversation ID
     * @return Optional conversation
     */
    public Optional<Conversation> getConversationById(String conversationId) {
        return conversationRepository.findById(conversationId);
    }

    /**
     * Get conversations by user ID
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    public Page<Conversation> getConversationsByUser(String userId, Pageable pageable) {
        return conversationRepository.findByParticipantUserIdOrderByLastMessageTimestampDesc(userId, pageable);
    }

    /**
     * Get direct conversation between two users
     * 
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return Optional conversation
     */
    public Optional<Conversation> getDirectConversation(String userId1, String userId2) {
        return conversationRepository.findDirectConversationBetweenUsers(userId1, userId2);
    }

    /**
     * Update conversation
     * 
     * @param conversation Conversation to update
     * @return Updated conversation
     */
    public Conversation updateConversation(Conversation conversation) {
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    /**
     * Add participant to conversation
     * 
     * @param conversationId Conversation ID
     * @param userId User ID to add
     * @return Updated conversation
     */
    public Conversation addParticipant(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Check if user is already a participant
        boolean isAlreadyParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(userId));
        
        if (isAlreadyParticipant) {
            throw new RuntimeException("User is already a participant");
        }
        
        // Add new participant
        Conversation.Participant participant = new Conversation.Participant(userId, Conversation.ParticipantRole.MEMBER);
        conversation.getParticipants().add(participant);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Remove participant from conversation
     * 
     * @param conversationId Conversation ID
     * @param userId User ID to remove
     * @return Updated conversation
     */
    public Conversation removeParticipant(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Remove participant
        conversation.getParticipants().removeIf(p -> p.getUserId().equals(userId));
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Update participant role
     * 
     * @param conversationId Conversation ID
     * @param userId User ID
     * @param role New role
     * @return Updated conversation
     */
    public Conversation updateParticipantRole(String conversationId, String userId, Conversation.ParticipantRole role) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Find and update participant
        conversation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .ifPresent(p -> p.setRole(role));
        
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Archive conversation
     * 
     * @param conversationId Conversation ID
     * @return Updated conversation
     */
    public Conversation archiveConversation(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        conversation.getSettings().setArchived(true);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Unarchive conversation
     * 
     * @param conversationId Conversation ID
     * @return Updated conversation
     */
    public Conversation unarchiveConversation(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        conversation.getSettings().setArchived(false);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Mute conversation
     * 
     * @param conversationId Conversation ID
     * @return Updated conversation
     */
    public Conversation muteConversation(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        conversation.getSettings().setMuted(true);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Unmute conversation
     * 
     * @param conversationId Conversation ID
     * @return Updated conversation
     */
    public Conversation unmuteConversation(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        conversation.getSettings().setMuted(false);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }

    /**
     * Search conversations
     * 
     * @param searchTerm Search term
     * @param pageable Pagination
     * @return Page of conversations
     */
    public Page<Conversation> searchConversations(String searchTerm, Pageable pageable) {
        return conversationRepository.findByNameOrDescriptionContainingIgnoreCase(searchTerm, pageable);
    }

    /**
     * Get conversations with unread messages
     * 
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of conversations
     */
    public Page<Conversation> getConversationsWithUnreadMessages(String userId, Pageable pageable) {
        return conversationRepository.findConversationsWithUnreadMessages(userId, pageable);
    }

    /**
     * Get conversation count by user
     * 
     * @param userId User ID
     * @return Conversation count
     */
    public long getConversationCountByUser(String userId) {
        return conversationRepository.countByParticipantUserId(userId);
    }

    /**
     * Get unread conversation count by user
     * 
     * @param userId User ID
     * @return Unread conversation count
     */
    public long getUnreadConversationCountByUser(String userId) {
        return conversationRepository.countConversationsWithUnreadMessages(userId);
    }
}
