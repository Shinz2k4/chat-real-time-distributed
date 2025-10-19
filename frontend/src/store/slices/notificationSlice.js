import { createSlice } from '@reduxjs/toolkit';

/**
 * Notification Slice
 * 
 * Manages notification state including in-app notifications,
 * notification preferences, and real-time updates.
 */
const notificationSlice = createSlice({
  name: 'notifications',
  initialState: {
    // Notifications
    notifications: [],
    unreadCount: 0,
    
    // Notification preferences
    preferences: {
      email: true,
      push: true,
      inApp: true,
      sound: true,
      vibration: true,
    },
    
    // UI state
    isLoading: false,
    error: null,
    showNotificationCenter: false,
  },
  reducers: {
    // Notification actions
    setNotifications: (state, action) => {
      state.notifications = action.payload;
    },
    
    addNotification: (state, action) => {
      state.notifications.unshift(action.payload);
      if (!action.payload.isRead) {
        state.unreadCount += 1;
      }
    },
    
    updateNotification: (state, action) => {
      const { notificationId, updates } = action.payload;
      const index = state.notifications.findIndex(n => n.id === notificationId);
      if (index !== -1) {
        const wasRead = state.notifications[index].isRead;
        state.notifications[index] = { ...state.notifications[index], ...updates };
        
        // Update unread count if read status changed
        if (!wasRead && updates.isRead) {
          state.unreadCount = Math.max(0, state.unreadCount - 1);
        } else if (wasRead && !updates.isRead) {
          state.unreadCount += 1;
        }
      }
    },
    
    removeNotification: (state, action) => {
      const notificationId = action.payload;
      const notification = state.notifications.find(n => n.id === notificationId);
      if (notification && !notification.isRead) {
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
      state.notifications = state.notifications.filter(n => n.id !== notificationId);
    },
    
    markAsRead: (state, action) => {
      const notificationId = action.payload;
      const index = state.notifications.findIndex(n => n.id === notificationId);
      if (index !== -1 && !state.notifications[index].isRead) {
        state.notifications[index].isRead = true;
        state.notifications[index].readAt = new Date().toISOString();
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }
    },
    
    markAllAsRead: (state) => {
      state.notifications.forEach(notification => {
        if (!notification.isRead) {
          notification.isRead = true;
          notification.readAt = new Date().toISOString();
        }
      });
      state.unreadCount = 0;
    },
    
    setUnreadCount: (state, action) => {
      state.unreadCount = action.payload;
    },
    
    // Preferences actions
    setPreferences: (state, action) => {
      state.preferences = { ...state.preferences, ...action.payload };
    },
    
    updateNotificationPreference: (state, action) => {
      const { key, value } = action.payload;
      state.preferences[key] = value;
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
    
    setShowNotificationCenter: (state, action) => {
      state.showNotificationCenter = action.payload;
    },
    
    // Clear all data
    clearAll: (state) => {
      state.notifications = [];
      state.unreadCount = 0;
      state.isLoading = false;
      state.error = null;
      state.showNotificationCenter = false;
    },
  },
});

export const {
  // Notification actions
  setNotifications,
  addNotification,
  updateNotification,
  removeNotification,
  markAsRead,
  markAllAsRead,
  setUnreadCount,
  
  // Preferences actions
  setPreferences,
  updateNotificationPreference,
  
  // UI actions
  setLoading,
  setError,
  clearError,
  setShowNotificationCenter,
  
  // Clear all data
  clearAll,
} = notificationSlice.actions;

export default notificationSlice.reducer;
