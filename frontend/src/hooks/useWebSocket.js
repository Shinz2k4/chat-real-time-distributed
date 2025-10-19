import { useState, useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

/**
 * Custom React Hook for WebSocket Communication
 * 
 * This hook provides a clean interface for WebSocket communication using STOMP protocol.
 * It handles connection management, message sending/receiving, and error handling.
 * 
 * @param {string} url - WebSocket server URL
 * @param {Object} options - Configuration options
 * @returns {Object} WebSocket state and methods
 */
export const useWebSocket = (url, options = {}) => {
  // Default configuration
  const defaultOptions = {
    debug: false,
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    maxReconnectAttempts: 5,
    onConnect: null,
    onDisconnect: null,
    onError: null,
    onMessage: null,
    ...options
  };

  // State management
  const [isConnected, setIsConnected] = useState(false);
  const [isConnecting, setIsConnecting] = useState(false);
  const [error, setError] = useState(null);
  const [reconnectAttempts, setReconnectAttempts] = useState(0);

  // Refs for stable references
  const stompClientRef = useRef(null);
  const subscriptionsRef = useRef(new Map());
  const reconnectTimeoutRef = useRef(null);
  const isMountedRef = useRef(true);

  /**
   * Initialize WebSocket connection
   */
  const connect = useCallback(() => {
    if (isConnecting || isConnected) {
      return;
    }

    setIsConnecting(true);
    setError(null);

    try {
      // Create SockJS connection
      const socket = new SockJS(url);
      
      // Create STOMP client
      const stompClient = new Stomp.Client({
        webSocketFactory: () => socket,
        debug: defaultOptions.debug ? console.log : () => {},
        heartbeatIncoming: defaultOptions.heartbeatIncoming,
        heartbeatOutgoing: defaultOptions.heartbeatOutgoing,
        reconnectDelay: defaultOptions.reconnectDelay,
      });

      // Connection event handlers
      stompClient.onConnect = (frame) => {
        if (defaultOptions.debug) {
          console.log('WebSocket connected:', frame);
        }
        
        setIsConnected(true);
        setIsConnecting(false);
        setReconnectAttempts(0);
        setError(null);
        
        // Call user-defined onConnect callback
        if (defaultOptions.onConnect) {
          defaultOptions.onConnect(frame);
        }
      };

      stompClient.onDisconnect = (frame) => {
        if (defaultOptions.debug) {
          console.log('WebSocket disconnected:', frame);
        }
        
        setIsConnected(false);
        setIsConnecting(false);
        
        // Clear all subscriptions
        subscriptionsRef.current.clear();
        
        // Call user-defined onDisconnect callback
        if (defaultOptions.onDisconnect) {
          defaultOptions.onDisconnect(frame);
        }
        
        // Attempt reconnection if component is still mounted
        if (isMountedRef.current && reconnectAttempts < defaultOptions.maxReconnectAttempts) {
          scheduleReconnect();
        }
      };

      stompClient.onStompError = (error) => {
        if (defaultOptions.debug) {
          console.error('WebSocket STOMP error:', error);
        }
        
        setError(error);
        setIsConnecting(false);
        
        // Call user-defined onError callback
        if (defaultOptions.onError) {
          defaultOptions.onError(error);
        }
        
        // Attempt reconnection
        if (isMountedRef.current && reconnectAttempts < defaultOptions.maxReconnectAttempts) {
          scheduleReconnect();
        }
      };

      // Store client reference
      stompClientRef.current = stompClient;
      
      // Activate the connection
      stompClient.activate();
      
    } catch (err) {
      if (defaultOptions.debug) {
        console.error('WebSocket connection error:', err);
      }
      
      setError(err);
      setIsConnecting(false);
      
      if (defaultOptions.onError) {
        defaultOptions.onError(err);
      }
    }
  }, [url, isConnecting, isConnected, reconnectAttempts, defaultOptions]);

  /**
   * Schedule reconnection attempt
   */
  const scheduleReconnect = useCallback(() => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
    }

    const delay = defaultOptions.reconnectDelay * Math.pow(2, reconnectAttempts);
    
    reconnectTimeoutRef.current = setTimeout(() => {
      if (isMountedRef.current) {
        setReconnectAttempts(prev => prev + 1);
        connect();
      }
    }, delay);
  }, [connect, reconnectAttempts, defaultOptions.reconnectDelay]);

  /**
   * Disconnect WebSocket
   */
  const disconnect = useCallback(() => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }

    if (stompClientRef.current && isConnected) {
      // Unsubscribe from all topics
      subscriptionsRef.current.forEach((subscription) => {
        subscription.unsubscribe();
      });
      subscriptionsRef.current.clear();

      // Disconnect
      stompClientRef.current.deactivate();
    }
  }, [isConnected]);

  /**
   * Send message to a destination
   * 
   * @param {string} destination - STOMP destination
   * @param {Object} message - Message payload
   * @param {Object} headers - Optional headers
   */
  const sendMessage = useCallback((destination, message, headers = {}) => {
    if (!isConnected || !stompClientRef.current) {
      throw new Error('WebSocket is not connected');
    }

    try {
      stompClientRef.current.publish({
        destination,
        body: JSON.stringify(message),
        headers: {
          'content-type': 'application/json',
          ...headers
        }
      });
    } catch (err) {
      if (defaultOptions.debug) {
        console.error('Error sending message:', err);
      }
      throw err;
    }
  }, [isConnected, defaultOptions.debug]);

  /**
   * Subscribe to a destination
   * 
   * @param {string} destination - STOMP destination
   * @param {Function} callback - Message callback function
   * @param {Object} headers - Optional headers
   * @returns {Function} Unsubscribe function
   */
  const subscribe = useCallback((destination, callback, headers = {}) => {
    if (!isConnected || !stompClientRef.current) {
      throw new Error('WebSocket is not connected');
    }

    try {
      const subscription = stompClientRef.current.subscribe(destination, (message) => {
        try {
          const parsedMessage = JSON.parse(message.body);
          callback(parsedMessage, message);
          
          // Call global message handler if provided
          if (defaultOptions.onMessage) {
            defaultOptions.onMessage(parsedMessage, message);
          }
        } catch (err) {
          if (defaultOptions.debug) {
            console.error('Error parsing message:', err);
          }
        }
      }, headers);

      // Store subscription for cleanup
      subscriptionsRef.current.set(destination, subscription);

      // Return unsubscribe function
      return () => {
        if (subscription) {
          subscription.unsubscribe();
          subscriptionsRef.current.delete(destination);
        }
      };
    } catch (err) {
      if (defaultOptions.debug) {
        console.error('Error subscribing to destination:', err);
      }
      throw err;
    }
  }, [isConnected, defaultOptions.debug, defaultOptions.onMessage]);

  /**
   * Unsubscribe from a destination
   * 
   * @param {string} destination - STOMP destination
   */
  const unsubscribe = useCallback((destination) => {
    const subscription = subscriptionsRef.current.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      subscriptionsRef.current.delete(destination);
    }
  }, []);

  /**
   * Get connection status
   */
  const getConnectionStatus = useCallback(() => {
    return {
      isConnected,
      isConnecting,
      error,
      reconnectAttempts,
      maxReconnectAttempts: defaultOptions.maxReconnectAttempts
    };
  }, [isConnected, isConnecting, error, reconnectAttempts, defaultOptions.maxReconnectAttempts]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      isMountedRef.current = false;
      disconnect();
    };
  }, [disconnect]);

  // Auto-connect on mount
  useEffect(() => {
    connect();
  }, [connect]);

  return {
    // Connection state
    isConnected,
    isConnecting,
    error,
    reconnectAttempts,
    
    // Connection methods
    connect,
    disconnect,
    
    // Message methods
    sendMessage,
    subscribe,
    unsubscribe,
    
    // Utility methods
    getConnectionStatus
  };
};

export default useWebSocket;
