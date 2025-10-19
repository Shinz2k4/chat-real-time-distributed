import { configureStore } from '@reduxjs/toolkit';
import chatReducer from './slices/chatSlice';
import userReducer from './slices/userSlice';
import notificationReducer from './slices/notificationSlice';
import authReducer from './slices/authSlice';

/**
 * Redux Store Configuration
 * 
 * This store manages the global state of the application including
 * chat messages, user data, and notifications.
 */
export const store = configureStore({
  reducer: {
    chat: chatReducer,
    user: userReducer,
    notifications: notificationReducer,
    auth: authReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['chat/messageReceived', 'chat/typingIndicator'],
        ignoredPaths: ['chat.messages', 'chat.typingUsers'],
      },
    }),
});

export default store;
