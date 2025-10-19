import { createSlice } from '@reduxjs/toolkit';

/**
 * Chat Slice
 * 
 * Manages chat-related state including messages, conversations,
 * typing indicators, and connection status.
 */
const chatSlice = createSlice({
  name: 'chat',
  initialState: {
    // Connection status
    isConnected: false,
    isConnecting: false,
    error: null,
    
    // Conversations
    conversations: [],
    selectedConversation: null,
    
    // Messages
    messages: {},
    
    // Typing indicators
    typingUsers: {},
    
    // UI state
    isLoading: false,
    searchQuery: '',
    showUserSearch: false,
  },
  reducers: {
    // Connection actions
    setConnectionStatus: (state, action) => {
      state.isConnected = action.payload.isConnected;
      state.isConnecting = action.payload.isConnecting;
      state.error = action.payload.error;
    },
    
    // Conversation actions
    setConversations: (state, action) => {
      state.conversations = action.payload;
    },
    
    addConversation: (state, action) => {
      state.conversations.unshift(action.payload);
    },
    
    updateConversation: (state, action) => {
      const { conversationId, updates } = action.payload;
      const index = state.conversations.findIndex(c => c.id === conversationId);
      if (index !== -1) {
        state.conversations[index] = { ...state.conversations[index], ...updates };
      }
    },
    
    removeConversation: (state, action) => {
      state.conversations = state.conversations.filter(c => c.id !== action.payload);
    },
    
    selectConversation: (state, action) => {
      state.selectedConversation = action.payload;
    },
    
    // Message actions
    setMessages: (state, action) => {
      const { conversationId, messages } = action.payload;
      state.messages[conversationId] = messages;
    },
    
    addMessage: (state, action) => {
      const { conversationId, message } = action.payload;
      if (!state.messages[conversationId]) {
        state.messages[conversationId] = [];
      }
      state.messages[conversationId].push(message);
    },
    
    updateMessage: (state, action) => {
      const { conversationId, messageId, updates } = action.payload;
      if (state.messages[conversationId]) {
        const index = state.messages[conversationId].findIndex(m => m.id === messageId);
        if (index !== -1) {
          state.messages[conversationId][index] = { ...state.messages[conversationId][index], ...updates };
        }
      }
    },
    
    removeMessage: (state, action) => {
      const { conversationId, messageId } = action.payload;
      if (state.messages[conversationId]) {
        state.messages[conversationId] = state.messages[conversationId].filter(m => m.id !== messageId);
      }
    },
    
    // Typing indicator actions
    setTypingUsers: (state, action) => {
      const { conversationId, users } = action.payload;
      state.typingUsers[conversationId] = users;
    },
    
    addTypingUser: (state, action) => {
      const { conversationId, user } = action.payload;
      if (!state.typingUsers[conversationId]) {
        state.typingUsers[conversationId] = [];
      }
      if (!state.typingUsers[conversationId].find(u => u.userId === user.userId)) {
        state.typingUsers[conversationId].push(user);
      }
    },
    
    removeTypingUser: (state, action) => {
      const { conversationId, userId } = action.payload;
      if (state.typingUsers[conversationId]) {
        state.typingUsers[conversationId] = state.typingUsers[conversationId].filter(u => u.userId !== userId);
      }
    },
    
    // UI actions
    setLoading: (state, action) => {
      state.isLoading = action.payload;
    },
    
    setSearchQuery: (state, action) => {
      state.searchQuery = action.payload;
    },
    
    setShowUserSearch: (state, action) => {
      state.showUserSearch = action.payload;
    },
    
    // Clear actions
    clearMessages: (state, action) => {
      const conversationId = action.payload;
      state.messages[conversationId] = [];
    },
    
    clearTypingUsers: (state, action) => {
      const conversationId = action.payload;
      state.typingUsers[conversationId] = [];
    },
    
    clearAll: (state) => {
      state.conversations = [];
      state.messages = {};
      state.typingUsers = {};
      state.selectedConversation = null;
      state.isConnected = false;
      state.isConnecting = false;
      state.error = null;
    },
  },
});

export const {
  // Connection actions
  setConnectionStatus,
  
  // Conversation actions
  setConversations,
  addConversation,
  updateConversation,
  removeConversation,
  selectConversation,
  
  // Message actions
  setMessages,
  addMessage,
  updateMessage,
  removeMessage,
  
  // Typing indicator actions
  setTypingUsers,
  addTypingUser,
  removeTypingUser,
  
  // UI actions
  setLoading,
  setSearchQuery,
  setShowUserSearch,
  
  // Clear actions
  clearMessages,
  clearTypingUsers,
  clearAll,
} = chatSlice.actions;

export default chatSlice.reducer;
