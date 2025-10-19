import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiConfig from '../../config/api';

/**
 * Authentication Slice
 * 
 * Manages user authentication state including login, logout,
 * token management, and user profile information.
 */

// Async thunks for API calls
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await fetch(apiConfig.buildUrl(apiConfig.endpoints.auth.login), {
        method: 'POST',
        headers: apiConfig.getHeaders(),
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const error = await response.json();
        return rejectWithValue(error.message || 'Login failed');
      }

      const data = await response.json();
      
      // Store token in localStorage
      if (data.token) {
        localStorage.setItem('token', data.token);
      }

      return data;
    } catch (error) {
      return rejectWithValue(error.message || 'Network error');
    }
  }
);

export const registerUser = createAsyncThunk(
  'auth/registerUser',
  async (userData, { rejectWithValue }) => {
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        const error = await response.json();
        return rejectWithValue(error.message || 'Registration failed');
      }

      const data = await response.json();
      
      // Store token in localStorage
      if (data.token) {
        localStorage.setItem('token', data.token);
      }

      return data;
    } catch (error) {
      return rejectWithValue(error.message || 'Network error');
    }
  }
);

export const logoutUser = createAsyncThunk(
  'auth/logoutUser',
  async (_, { rejectWithValue }) => {
    try {
      const token = localStorage.getItem('token');
      
      if (token) {
        await fetch('/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });
      }

      // Remove token from localStorage
      localStorage.removeItem('token');
      
      return true;
    } catch (error) {
      // Even if logout fails on server, clear local state
      localStorage.removeItem('token');
      return true;
    }
  }
);

export const verifyToken = createAsyncThunk(
  'auth/verifyToken',
  async (_, { rejectWithValue }) => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        return rejectWithValue('No token found');
      }

      const response = await fetch('/api/auth/verify', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        localStorage.removeItem('token');
        return rejectWithValue('Token verification failed');
      }

      const data = await response.json();
      return data;
    } catch (error) {
      localStorage.removeItem('token');
      return rejectWithValue(error.message || 'Token verification failed');
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    // User data
    user: null,
    isAuthenticated: false,
    
    // Token management
    token: localStorage.getItem('token') || null,
    
    // Loading states
    loading: false,
    loginLoading: false,
    registerLoading: false,
    logoutLoading: false,
    
    // Error states
    error: null,
    loginError: null,
    registerError: null,
    logoutError: null,
    
    // UI state
    showLoginModal: false,
    showRegisterModal: false,
  },
  reducers: {
    // Clear errors
    clearError: (state) => {
      state.error = null;
      state.loginError = null;
      state.registerError = null;
      state.logoutError = null;
    },
    
    // Set user data
    setUser: (state, action) => {
      state.user = action.payload;
      state.isAuthenticated = !!action.payload;
    },
    
    // Set token
    setToken: (state, action) => {
      state.token = action.payload;
      if (action.payload) {
        localStorage.setItem('token', action.payload);
      } else {
        localStorage.removeItem('token');
      }
    },
    
    // Update user profile
    updateUserProfile: (state, action) => {
      if (state.user) {
        state.user = { ...state.user, ...action.payload };
      }
    },
    
    // UI actions
    setShowLoginModal: (state, action) => {
      state.showLoginModal = action.payload;
    },
    
    setShowRegisterModal: (state, action) => {
      state.showRegisterModal = action.payload;
    },
    
    // Manual logout (without API call)
    logout: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.token = null;
      state.error = null;
      state.loginError = null;
      state.registerError = null;
      state.logoutError = null;
      localStorage.removeItem('token');
    },
    
    // Clear all auth data
    clearAuth: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.token = null;
      state.loading = false;
      state.loginLoading = false;
      state.registerLoading = false;
      state.logoutLoading = false;
      state.error = null;
      state.loginError = null;
      state.registerError = null;
      state.logoutError = null;
      state.showLoginModal = false;
      state.showRegisterModal = false;
      localStorage.removeItem('token');
    },
  },
  extraReducers: (builder) => {
    // Login user
    builder
      .addCase(loginUser.pending, (state) => {
        state.loginLoading = true;
        state.loginError = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.loginLoading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
        state.token = action.payload.token;
        state.loginError = null;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loginLoading = false;
        state.loginError = action.payload;
        state.isAuthenticated = false;
        state.user = null;
        state.token = null;
      });

    // Register user
    builder
      .addCase(registerUser.pending, (state) => {
        state.registerLoading = true;
        state.registerError = null;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.registerLoading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
        state.token = action.payload.token;
        state.registerError = null;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.registerLoading = false;
        state.registerError = action.payload;
        state.isAuthenticated = false;
        state.user = null;
        state.token = null;
      });

    // Logout user
    builder
      .addCase(logoutUser.pending, (state) => {
        state.logoutLoading = true;
        state.logoutError = null;
      })
      .addCase(logoutUser.fulfilled, (state) => {
        state.logoutLoading = false;
        state.user = null;
        state.isAuthenticated = false;
        state.token = null;
        state.logoutError = null;
      })
      .addCase(logoutUser.rejected, (state, action) => {
        state.logoutLoading = false;
        state.logoutError = action.payload;
        // Still clear local state even if server logout fails
        state.user = null;
        state.isAuthenticated = false;
        state.token = null;
      });

    // Verify token
    builder
      .addCase(verifyToken.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(verifyToken.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(verifyToken.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.user = null;
        state.isAuthenticated = false;
        state.token = null;
      });
  },
});

export const {
  clearError,
  setUser,
  setToken,
  updateUserProfile,
  setShowLoginModal,
  setShowRegisterModal,
  logout,
  clearAuth,
} = authSlice.actions;

export default authSlice.reducer;
