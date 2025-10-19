import React, { createContext, useContext, useState, useEffect } from 'react';
import apiConfig from '../config/api';

/**
 * Notification Context
 * 
 * This context provides notification state and methods throughout the application.
 * It manages in-app notifications, notification preferences, and real-time updates.
 */
const NotificationContext = createContext();

/**
 * Notification Provider Component
 * 
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - Child components
 * @returns {JSX.Element} NotificationContext.Provider
 */
export const NotificationProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Fetch notifications from API
   * 
   * @param {number} page - Page number
   * @param {number} limit - Number of notifications per page
   */
  const fetchNotifications = async (page = 0, limit = 20) => {
    try {
      setIsLoading(true);
      setError(null);

      const response = await fetch(`${apiConfig.buildUrl(apiConfig.endpoints.notifications.list)}?page=${page}&size=${limit}`, {
        headers: apiConfig.getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch notifications');
      }

      const data = await response.json();
      setNotifications(data.notifications || []);
      setUnreadCount(data.unreadCount || 0);
    } catch (err) {
      setError(err.message);
      console.error('Error fetching notifications:', err);
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Mark notification as read
   * 
   * @param {string} notificationId - Notification ID
   */
  const markAsRead = async (notificationId) => {
    try {
      const response = await fetch(`${apiConfig.buildUrl(apiConfig.endpoints.notifications.markRead)}/${notificationId}/read`, {
        method: 'PUT',
        headers: apiConfig.getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to mark notification as read');
      }

      // Update local state
      setNotifications(prev => 
        prev.map(notification => 
          notification.id === notificationId 
            ? { ...notification, isRead: true, readAt: new Date().toISOString() }
            : notification
        )
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (err) {
      setError(err.message);
      console.error('Error marking notification as read:', err);
    }
  };

  /**
   * Mark all notifications as read
   */
  const markAllAsRead = async () => {
    try {
      const response = await fetch(`${apiConfig.buildUrl(apiConfig.endpoints.notifications.markAllRead)}`, {
        method: 'PUT',
        headers: apiConfig.getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to mark all notifications as read');
      }

      // Update local state
      setNotifications(prev => 
        prev.map(notification => ({
          ...notification,
          isRead: true,
          readAt: new Date().toISOString()
        }))
      );
      setUnreadCount(0);
    } catch (err) {
      setError(err.message);
      console.error('Error marking all notifications as read:', err);
    }
  };

  /**
   * Delete notification
   * 
   * @param {string} notificationId - Notification ID
   */
  const deleteNotification = async (notificationId) => {
    try {
      const response = await fetch(`${apiConfig.buildUrl(apiConfig.endpoints.notifications.delete)}/${notificationId}`, {
        method: 'DELETE',
        headers: apiConfig.getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to delete notification');
      }

      // Update local state
      setNotifications(prev => prev.filter(notification => notification.id !== notificationId));
    } catch (err) {
      setError(err.message);
      console.error('Error deleting notification:', err);
    }
  };

  /**
   * Add new notification
   * 
   * @param {Object} notification - Notification object
   */
  const addNotification = (notification) => {
    setNotifications(prev => [notification, ...prev]);
    if (!notification.isRead) {
      setUnreadCount(prev => prev + 1);
    }
  };

  /**
   * Get notification count
   * 
   * @returns {Object} Notification counts
   */
  const getNotificationCount = () => {
    return {
      total: notifications.length,
      unread: unreadCount,
    };
  };

  /**
   * Clear error
   */
  const clearError = () => {
    setError(null);
  };

  // Fetch notifications on mount
  useEffect(() => {
    fetchNotifications();
  }, []);

  const value = {
    // State
    notifications,
    unreadCount,
    isLoading,
    error,
    
    // Methods
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    addNotification,
    getNotificationCount,
    clearError,
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
    </NotificationContext.Provider>
  );
};

/**
 * Hook to use notification context
 * 
 * @returns {Object} Notification context value
 */
export const useNotifications = () => {
  const context = useContext(NotificationContext);
  
  if (!context) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  
  return context;
};

export default NotificationContext;
