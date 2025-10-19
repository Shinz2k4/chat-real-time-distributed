/**
 * API Configuration
 * 
 * Centralized configuration for API endpoints and base URLs
 */

const config = {
  // API Base URL - points to the API Gateway
  API_BASE_URL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
  
  // WebSocket URL
  WS_URL: process.env.REACT_APP_WS_URL || 'http://localhost:8080/ws/chat',
  
  // API Endpoints
  endpoints: {
    // Authentication
    auth: {
      login: '/api/users/auth/login',
      register: '/api/users/auth/register',
      logout: '/api/users/auth/logout',
      refresh: '/api/users/auth/refresh',
      profile: '/api/users/profile',
    },
    
    // Notifications
    notifications: {
      list: '/api/notifications/users/current',
      markRead: '/api/notifications',
      markAllRead: '/api/notifications/mark-all-read',
      delete: '/api/notifications',
    },
    
    // Chat
    chat: {
      conversations: '/api/chat/conversations',
      messages: '/api/chat/messages',
      send: '/api/chat/messages',
    },
    
    // Users
    users: {
      profile: '/api/users/profile',
      search: '/api/users/search',
      friends: '/api/users/friends',
    },
  },
  
  // Helper function to build full API URLs
  buildUrl: (endpoint) => {
    return `${config.API_BASE_URL}${endpoint}`;
  },
  
  // Helper function to get API headers
  getHeaders: (token = null) => {
    const headers = {
      'Content-Type': 'application/json',
    };
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    } else if (localStorage.getItem('authToken')) {
      headers['Authorization'] = `Bearer ${localStorage.getItem('authToken')}`;
    }
    
    return headers;
  },
};

export default config;

