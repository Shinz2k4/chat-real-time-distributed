import { createSlice } from '@reduxjs/toolkit';

/**
 * User Slice
 * 
 * Manages user-related state including profile information,
 * preferences, and online status.
 */
const userSlice = createSlice({
  name: 'user',
  initialState: {
    // User profile
    profile: null,
    
    // User preferences
    preferences: {
      theme: 'light',
      notifications: {
        email: true,
        push: true,
        inApp: true,
      },
      privacy: {
        showOnlineStatus: true,
        showLastSeen: true,
        allowFriendRequests: true,
      },
    },
    
    // Online status
    isOnline: false,
    lastSeen: null,
    
    // Friends and contacts
    friends: [],
    friendRequests: [],
    
    // UI state
    isLoading: false,
    error: null,
  },
  reducers: {
    // Profile actions
    setProfile: (state, action) => {
      state.profile = action.payload;
    },
    
    updateProfile: (state, action) => {
      if (state.profile) {
        state.profile = { ...state.profile, ...action.payload };
      }
    },
    
    clearProfile: (state) => {
      state.profile = null;
    },
    
    // Preferences actions
    setPreferences: (state, action) => {
      state.preferences = { ...state.preferences, ...action.payload };
    },
    
    updateNotificationPreferences: (state, action) => {
      state.preferences.notifications = { ...state.preferences.notifications, ...action.payload };
    },
    
    updatePrivacyPreferences: (state, action) => {
      state.preferences.privacy = { ...state.preferences.privacy, ...action.payload };
    },
    
    setTheme: (state, action) => {
      state.preferences.theme = action.payload;
    },
    
    // Online status actions
    setOnlineStatus: (state, action) => {
      state.isOnline = action.payload.isOnline;
      state.lastSeen = action.payload.lastSeen;
    },
    
    updateLastSeen: (state, action) => {
      state.lastSeen = action.payload;
    },
    
    // Friends actions
    setFriends: (state, action) => {
      state.friends = action.payload;
    },
    
    addFriend: (state, action) => {
      state.friends.push(action.payload);
    },
    
    removeFriend: (state, action) => {
      state.friends = state.friends.filter(friend => friend.id !== action.payload);
    },
    
    updateFriend: (state, action) => {
      const { friendId, updates } = action.payload;
      const index = state.friends.findIndex(friend => friend.id === friendId);
      if (index !== -1) {
        state.friends[index] = { ...state.friends[index], ...updates };
      }
    },
    
    // Friend requests actions
    setFriendRequests: (state, action) => {
      state.friendRequests = action.payload;
    },
    
    addFriendRequest: (state, action) => {
      state.friendRequests.push(action.payload);
    },
    
    removeFriendRequest: (state, action) => {
      state.friendRequests = state.friendRequests.filter(request => request.id !== action.payload);
    },
    
    updateFriendRequest: (state, action) => {
      const { requestId, updates } = action.payload;
      const index = state.friendRequests.findIndex(request => request.id === requestId);
      if (index !== -1) {
        state.friendRequests[index] = { ...state.friendRequests[index], ...updates };
      }
    },
    
    // UI actions
    setLoading: (state, action) => {
      state.isLoading = action.payload;
    },
    
    setError: (state, action) => {
      state.error = action.payload;
    },
    
    clearError: (state) => {
      state.error = null;
    },
    
    // Clear all data
    clearAll: (state) => {
      state.profile = null;
      state.friends = [];
      state.friendRequests = [];
      state.isOnline = false;
      state.lastSeen = null;
      state.isLoading = false;
      state.error = null;
    },
  },
});

export const {
  // Profile actions
  setProfile,
  updateProfile,
  clearProfile,
  
  // Preferences actions
  setPreferences,
  updateNotificationPreferences,
  updatePrivacyPreferences,
  setTheme,
  
  // Online status actions
  setOnlineStatus,
  updateLastSeen,
  
  // Friends actions
  setFriends,
  addFriend,
  removeFriend,
  updateFriend,
  
  // Friend requests actions
  setFriendRequests,
  addFriendRequest,
  removeFriendRequest,
  updateFriendRequest,
  
  // UI actions
  setLoading,
  setError,
  clearError,
  
  // Clear all data
  clearAll,
} = userSlice.actions;

export default userSlice.reducer;
