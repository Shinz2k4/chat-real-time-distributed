import React, { createContext, useContext, useState, useEffect } from 'react';
import apiConfig from '../config/api';

/**
 * Authentication Context
 * 
 * This context provides authentication state and methods throughout the application.
 * It manages user authentication, token storage, and login/logout functionality.
 */
const AuthContext = createContext();

/**
 * Authentication Provider Component
 * 
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - Child components
 * @returns {JSX.Element} AuthContext.Provider
 */
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  /**
   * Initialize authentication state from localStorage
   */
  useEffect(() => {
    const initializeAuth = () => {
      try {
        const storedToken = localStorage.getItem('authToken');
        const storedUser = localStorage.getItem('user');

        if (storedToken && storedUser) {
          setToken(storedToken);
          setUser(JSON.parse(storedUser));
          setIsAuthenticated(true);
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        // Clear invalid data
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  /**
   * Login user
   * 
   * @param {string} email - User email
   * @param {string} password - User password
   * @returns {Promise<Object>} Login result
   */
  const login = async (email, password) => {
    try {
      setIsLoading(true);

      const response = await fetch(apiConfig.buildUrl(apiConfig.endpoints.auth.login), {
        method: 'POST',
        headers: apiConfig.getHeaders(),
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();
      const { token: authToken, user: userData } = data;

      // Store authentication data
      setToken(authToken);
      setUser(userData);
      setIsAuthenticated(true);

      // Persist to localStorage
      localStorage.setItem('authToken', authToken);
      localStorage.setItem('user', JSON.stringify(userData));

      return { success: true, user: userData };
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, error: error.message };
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Register new user
   * 
   * @param {Object} userData - User registration data
   * @returns {Promise<Object>} Registration result
   */
  const register = async (userData) => {
    try {
      setIsLoading(true);

      const response = await fetch(apiConfig.buildUrl(apiConfig.endpoints.auth.register), {
        method: 'POST',
        headers: apiConfig.getHeaders(),
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Registration failed');
      }

      const data = await response.json();
      return { success: true, user: data.user };
    } catch (error) {
      console.error('Registration error:', error);
      return { success: false, error: error.message };
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Logout user
   */
  const logout = () => {
    setUser(null);
    setToken(null);
    setIsAuthenticated(false);

    // Clear localStorage
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  };

  /**
   * Update user profile
   * 
   * @param {Object} updates - Profile updates
   * @returns {Promise<Object>} Update result
   */
  const updateProfile = async (updates) => {
    try {
      if (!token) {
        throw new Error('No authentication token');
      }

      const response = await fetch('/api/users/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(updates),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Profile update failed');
      }

      const updatedUser = await response.json();
      setUser(updatedUser);
      localStorage.setItem('user', JSON.stringify(updatedUser));

      return { success: true, user: updatedUser };
    } catch (error) {
      console.error('Profile update error:', error);
      return { success: false, error: error.message };
    }
  };

  /**
   * Refresh authentication token
   * 
   * @returns {Promise<boolean>} Success status
   */
  const refreshToken = async () => {
    try {
      if (!token) {
        return false;
      }

      const response = await fetch('/api/users/auth/refresh', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Token refresh failed');
      }

      const data = await response.json();
      const newToken = data.token;

      setToken(newToken);
      localStorage.setItem('authToken', newToken);

      return true;
    } catch (error) {
      console.error('Token refresh error:', error);
      logout();
      return false;
    }
  };

  /**
   * Get authorization headers for API requests
   * 
   * @returns {Object} Headers object
   */
  const getAuthHeaders = () => {
    return {
      'Authorization': `Bearer ${token}`,
    };
  };

  const value = {
    // State
    user,
    token,
    isLoading,
    isAuthenticated,
    
    // Methods
    login,
    register,
    logout,
    updateProfile,
    refreshToken,
    getAuthHeaders,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

/**
 * Hook to use authentication context
 * 
 * @returns {Object} Authentication context value
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  
  return context;
};

export default AuthContext;
