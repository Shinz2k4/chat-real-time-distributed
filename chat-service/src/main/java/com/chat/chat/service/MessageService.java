package com.chat.chat.service;

import com.chat.chat.model.Message;
import com.chat.chat.model.Conversation;
import com.chat.chat.repository.MessageRepository;
import com.chat.chat.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Message Service
 * 
 * Service class for message management operations including
 * sending, retrieving, updating, and deleting messages.
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    /**
     * Send a new message
     * 
     * @param message Message to send
     * @return Saved message
     */
    public Message sendMessage(Message message) {
        // Validate conversation exists
        Optional<Conversation> conversation = conversationRepository.findById(message.getConversationId());
        if (conversation.isEmpty()) {
            throw new RuntimeException("Conversation not found");
        }

        // Set message properties
        message.setStatus(Message.MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        // Save message
        Message savedMessage = messageRepository.save(message);

        // Update conversation last message
        updateConversationLastMessage(conversation.get(), savedMessage);

        return savedMessage;
    }

    /**
     * Get messages by conversation ID
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    public Page<Message> getMessagesByConversation(String conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdAndIsDeletedOrderByCreatedAtDesc(conversationId, false, pageable);
    }

    /**
     * Get message by ID
     * 
     * @param messageId Message ID
     * @return Optional message
     */
    public Optional<Message> getMessageById(String messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * Update message status
     * 
     * @param messageId Message ID
     * @param status New status
     * @return Updated message
     */
    public Message updateMessageStatus(String messageId, Message.MessageStatus status) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setStatus(status);
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Edit message
     * 
     * @param messageId Message ID
     * @param newContent New content
     * @return Updated message
     */
    public Message editMessage(String messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setContent(newContent);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Delete message (soft delete)
     * 
     * @param messageId Message ID
     * @return Updated message
     */
    public Message deleteMessage(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setDeleted(true);
        message.setDeletedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Add reaction to message
     * 
     * @param messageId Message ID
     * @param userId User ID
     * @param emoji Reaction emoji
     * @return Updated message
     */
    public Message addReaction(String messageId, String userId, String emoji) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.getReactions() == null) {
            message.setReactions(new java.util.ArrayList<>());
        }

        // Remove existing reaction from same user
        message.getReactions().removeIf(reaction -> reaction.getUserId().equals(userId));

        // Add new reaction
        message.getReactions().add(new Message.Reaction(userId, emoji));
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Remove reaction from message
     * 
     * @param messageId Message ID
     * @param userId User ID
     * @return Updated message
     */
    public Message removeReaction(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.getReactions() != null) {
            message.getReactions().removeIf(reaction -> reaction.getUserId().equals(userId));
            message.setUpdatedAt(LocalDateTime.now());
        }

        return messageRepository.save(message);
    }

    /**
     * Search messages by content
     * 
     * @param conversationId Conversation ID
     * @param content Content to search
     * @param pageable Pagination
     * @return Page of messages
     */
    public Page<Message> searchMessages(String conversationId, String content, Pageable pageable) {
        return messageRepository.searchByContent(conversationId, content, pageable);
    }

    /**
     * Get messages with attachments
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    public Page<Message> getMessagesWithAttachments(String conversationId, Pageable pageable) {
        return messageRepository.findMessagesWithAttachments(conversationId, pageable);
    }

    /**
     * Get messages with reactions
     * 
     * @param conversationId Conversation ID
     * @param pageable Pagination
     * @return Page of messages
     */
    public Page<Message> getMessagesWithReactions(String conversationId, Pageable pageable) {
        return messageRepository.findMessagesWithReactions(conversationId, pageable);
    }

    /**
     * Get messages by mention
     * 
     * @param conversationId Conversation ID
     * @param userId User ID mentioned
     * @param pageable Pagination
     * @return Page of messages
     */
    public Page<Message> getMessagesByMention(String conversationId, String userId, Pageable pageable) {
        return messageRepository.findMessagesByMention(conversationId, userId, pageable);
    }

    /**
     * Get message count by conversation
     * 
     * @param conversationId Conversation ID
     * @return Message count
     */
    public long getMessageCountByConversation(String conversationId) {
        return messageRepository.countByConversationIdAndIsDeleted(conversationId, false);
    }

    /**
     * Get latest message by conversation
     * 
     * @param conversationId Conversation ID
     * @return Latest message
     */
    public Optional<Message> getLatestMessageByConversation(String conversationId) {
        return Optional.ofNullable(messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversationId));
    }

    /**
     * Update conversation last message
     * 
     * @param conversation Conversation to update
     * @param message Last message
     */
    private void updateConversationLastMessage(Conversation conversation, Message message) {
        Conversation.LastMessage lastMessage = new Conversation.LastMessage(
                message.getId(),
                message.getContent(),
                message.getSenderId(),
                message.getCreatedAt()
        );

        conversation.setLastMessage(lastMessage);
        conversation.setUpdatedAt(LocalDateTime.now());

        // Update message count
        if (conversation.getMetadata() == null) {
            conversation.setMetadata(new Conversation.ConversationMetadata());
        }
        conversation.getMetadata().setMessageCount(conversation.getMetadata().getMessageCount() + 1);

        conversationRepository.save(conversation);
    }
}
