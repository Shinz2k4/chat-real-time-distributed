import { useCallback, useEffect, useRef } from 'react';
import { useWebSocket } from './useWebSocket';
import { useAuth } from '../contexts/AuthContext';

/**
 * Custom React Hook for Chat WebSocket Communication
 * 
 * This hook provides chat-specific WebSocket functionality including:
 * - Real-time messaging
 * - Typing indicators
 * - Message status updates
 * - Presence management
 * 
 * @param {string} conversationId - Current conversation ID
 * @param {Object} options - Configuration options
 * @returns {Object} Chat WebSocket state and methods
 */
export const useChatWebSocket = (conversationId, options = {}) => {
  const { user, token } = useAuth();
  const messageHandlersRef = useRef(new Map());
  const typingUsersRef = useRef(new Set());

  // WebSocket configuration
  const wsUrl = process.env.REACT_APP_WS_URL || 'http://localhost:8080/ws/chat';
  
  const wsOptions = {
    debug: process.env.NODE_ENV === 'development',
    reconnectDelay: 3000,
    maxReconnectAttempts: 10,
    onConnect: (frame) => {
      console.log('Chat WebSocket connected');
      // Join conversation room
      if (conversationId) {
        joinConversation(conversationId);
      }
    },
    onDisconnect: (frame) => {
      console.log('Chat WebSocket disconnected');
      // Leave conversation room
      if (conversationId) {
        leaveConversation(conversationId);
      }
    },
    onError: (error) => {
      console.error('Chat WebSocket error:', error);
    },
    ...options
  };

  // Initialize WebSocket connection
  const {
    isConnected,
    isConnecting,
    error,
    sendMessage,
    subscribe,
    unsubscribe,
    connect,
    disconnect
  } = useWebSocket(wsUrl, wsOptions);

  /**
   * Join a conversation room
   * 
   * @param {string} conversationId - Conversation ID to join
   */
  const joinConversation = useCallback((conversationId) => {
    if (!isConnected) return;

    try {
      sendMessage('/app/chat.join', {
        conversationId,
        userId: user?.id,
        timestamp: new Date().toISOString()
      });
    } catch (err) {
      console.error('Error joining conversation:', err);
    }
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Leave a conversation room
   * 
   * @param {string} conversationId - Conversation ID to leave
   */
  const leaveConversation = useCallback((conversationId) => {
    if (!isConnected) return;

    try {
      sendMessage('/app/chat.leave', {
        conversationId,
        userId: user?.id,
        timestamp: new Date().toISOString()
      });
    } catch (err) {
      console.error('Error leaving conversation:', err);
    }
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Send a text message
   * 
   * @param {string} content - Message content
   * @param {string} conversationId - Target conversation ID
   * @param {Object} options - Additional options
   */
  const sendTextMessage = useCallback((content, conversationId, options = {}) => {
    if (!isConnected) {
      throw new Error('WebSocket is not connected');
    }

    const message = {
      id: generateMessageId(),
      conversationId,
      senderId: user?.id,
      content,
      type: 'text',
      timestamp: new Date().toISOString(),
      ...options
    };

    sendMessage('/app/chat.send', message);
    return message;
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Send a typing indicator
   * 
   * @param {string} conversationId - Target conversation ID
   * @param {boolean} isTyping - Whether user is typing
   */
  const sendTypingIndicator = useCallback((conversationId, isTyping) => {
    if (!isConnected) return;

    sendMessage('/app/chat.typing', {
      conversationId,
      userId: user?.id,
      isTyping,
      timestamp: new Date().toISOString()
    });
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Mark message as read
   * 
   * @param {string} messageId - Message ID to mark as read
   * @param {string} conversationId - Conversation ID
   */
  const markMessageAsRead = useCallback((messageId, conversationId) => {
    if (!isConnected) return;

    sendMessage('/app/chat.read', {
      messageId,
      conversationId,
      userId: user?.id,
      timestamp: new Date().toISOString()
    });
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Subscribe to conversation messages
   * 
   * @param {string} conversationId - Conversation ID to subscribe to
   * @param {Function} onMessage - Message handler function
   */
  const subscribeToMessages = useCallback((conversationId, onMessage) => {
    if (!isConnected) return null;

    const destination = `/topic/conversation.${conversationId}`;
    
    const unsubscribe = subscribe(destination, (message) => {
      onMessage(message);
    });

    // Store handler for cleanup
    messageHandlersRef.current.set(conversationId, { unsubscribe, onMessage });

    return unsubscribe;
  }, [isConnected, subscribe]);

  /**
   * Subscribe to typing indicators
   * 
   * @param {string} conversationId - Conversation ID to subscribe to
   * @param {Function} onTyping - Typing indicator handler function
   */
  const subscribeToTyping = useCallback((conversationId, onTyping) => {
    if (!isConnected) return null;

    const destination = `/topic/typing.${conversationId}`;
    
    const unsubscribe = subscribe(destination, (data) => {
      onTyping(data);
    });

    return unsubscribe;
  }, [isConnected, subscribe]);

  /**
   * Subscribe to user presence updates
   * 
   * @param {Function} onPresence - Presence update handler function
   */
  const subscribeToPresence = useCallback((onPresence) => {
    if (!isConnected) return null;

    const destination = '/topic/presence';
    
    const unsubscribe = subscribe(destination, (data) => {
      onPresence(data);
    });

    return unsubscribe;
  }, [isConnected, subscribe]);

  /**
   * Subscribe to message status updates
   * 
   * @param {Function} onStatusUpdate - Status update handler function
   */
  const subscribeToMessageStatus = useCallback((onStatusUpdate) => {
    if (!isConnected) return null;

    const destination = `/user/${user?.id}/queue/message.status`;
    
    const unsubscribe = subscribe(destination, (data) => {
      onStatusUpdate(data);
    });

    return unsubscribe;
  }, [isConnected, subscribe, user?.id]);

  /**
   * Update user presence status
   * 
   * @param {string} status - New presence status (online, offline, away, busy)
   */
  const updatePresence = useCallback((status) => {
    if (!isConnected) return;

    sendMessage('/app/presence.update', {
      userId: user?.id,
      status,
      timestamp: new Date().toISOString()
    });
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Send a reaction to a message
   * 
   * @param {string} messageId - Target message ID
   * @param {string} conversationId - Conversation ID
   * @param {string} emoji - Reaction emoji
   */
  const sendReaction = useCallback((messageId, conversationId, emoji) => {
    if (!isConnected) return;

    sendMessage('/app/chat.react', {
      messageId,
      conversationId,
      userId: user?.id,
      emoji,
      timestamp: new Date().toISOString()
    });
  }, [isConnected, sendMessage, user?.id]);

  /**
   * Generate a unique message ID
   * 
   * @returns {string} Unique message ID
   */
  const generateMessageId = () => {
    return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  };

  // Auto-join conversation when conversationId changes
  useEffect(() => {
    if (isConnected && conversationId) {
      joinConversation(conversationId);
    }
  }, [isConnected, conversationId, joinConversation]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      // Unsubscribe from all message handlers
      messageHandlersRef.current.forEach(({ unsubscribe }) => {
        unsubscribe();
      });
      messageHandlersRef.current.clear();
    };
  }, []);

  return {
    // Connection state
    isConnected,
    isConnecting,
    error,
    
    // Connection methods
    connect,
    disconnect,
    
    // Chat methods
    sendTextMessage,
    sendTypingIndicator,
    markMessageAsRead,
    sendReaction,
    updatePresence,
    
    // Subscription methods
    subscribeToMessages,
    subscribeToTyping,
    subscribeToPresence,
    subscribeToMessageStatus,
    
    // Utility methods
    joinConversation,
    leaveConversation
  };
};

export default useChatWebSocket;
