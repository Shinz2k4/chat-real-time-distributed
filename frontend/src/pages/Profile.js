import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { 
  Container, 
  Paper, 
  Typography, 
  TextField, 
  Button, 
  Avatar, 
  Box, 
  Switch, 
  FormControlLabel,
  Divider,
  Alert,
  CircularProgress
} from '@mui/material';
import { PhotoCamera, Save, Edit } from '@mui/icons-material';
import { updateProfile } from '../store/slices/userSlice';

/**
 * Profile Page Component
 * 
 * Allows users to view and edit their profile information including:
 * - Basic profile details (display name, bio, avatar)
 * - Notification preferences
 * - Privacy settings
 * - Theme preferences
 */
const Profile = () => {
  const dispatch = useDispatch();
  const { user, loading, error } = useSelector(state => state.user);
  const { updateProfile: updateProfileAction } = useSelector(state => state.user);

  // Local state for form data
  const [formData, setFormData] = useState({
    displayName: '',
    bio: '',
    avatar: '',
    notifications: {
      email: true,
      push: true,
      inApp: true
    },
    privacy: {
      showOnlineStatus: true,
      showLastSeen: true,
      allowFriendRequests: true
    },
    theme: 'light'
  });

  // Local state for UI
  const [isEditing, setIsEditing] = useState(false);
  const [saveLoading, setSaveLoading] = useState(false);
  const [message, setMessage] = useState('');

  // Initialize form data when user data loads
  useEffect(() => {
    if (user) {
      setFormData({
        displayName: user.displayName || '',
        bio: user.bio || '',
        avatar: user.avatar || '',
        notifications: {
          email: user.settings?.notifications?.email ?? true,
          push: user.settings?.notifications?.push ?? true,
          inApp: user.settings?.notifications?.inApp ?? true
        },
        privacy: {
          showOnlineStatus: user.settings?.privacy?.showOnlineStatus ?? true,
          showLastSeen: user.settings?.privacy?.showLastSeen ?? true,
          allowFriendRequests: user.settings?.privacy?.allowFriendRequests ?? true
        },
        theme: user.settings?.theme || 'light'
      });
    }
  }, [user]);

  // Handle form input changes
  const handleInputChange = (field, value) => {
    if (field.includes('.')) {
      const [parent, child] = field.split('.');
      setFormData(prev => ({
        ...prev,
        [parent]: {
          ...prev[parent],
          [child]: value
        }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [field]: value
      }));
    }
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaveLoading(true);
    setMessage('');

    try {
      await dispatch(updateProfile({
        userId: user.id,
        updates: {
          displayName: formData.displayName,
          bio: formData.bio,
          avatar: formData.avatar,
          settings: {
            notifications: formData.notifications,
            privacy: formData.privacy,
            theme: formData.theme
          }
        }
      })).unwrap();

      setMessage('Profile updated successfully!');
      setIsEditing(false);
    } catch (error) {
      setMessage(`Error updating profile: ${error.message}`);
    } finally {
      setSaveLoading(false);
    }
  };

  // Handle cancel editing
  const handleCancel = () => {
    if (user) {
      setFormData({
        displayName: user.displayName || '',
        bio: user.bio || '',
        avatar: user.avatar || '',
        notifications: {
          email: user.settings?.notifications?.email ?? true,
          push: user.settings?.notifications?.push ?? true,
          inApp: user.settings?.notifications?.inApp ?? true
        },
        privacy: {
          showOnlineStatus: user.settings?.privacy?.showOnlineStatus ?? true,
          showLastSeen: user.settings?.privacy?.showLastSeen ?? true,
          allowFriendRequests: user.settings?.privacy?.allowFriendRequests ?? true
        },
        theme: user.settings?.theme || 'light'
      });
    }
    setIsEditing(false);
    setMessage('');
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (!user) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Alert severity="warning">Please log in to view your profile.</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h4" component="h1">
            Profile Settings
          </Typography>
          <Button
            variant={isEditing ? "outlined" : "contained"}
            startIcon={isEditing ? <Edit /> : <Edit />}
            onClick={() => setIsEditing(!isEditing)}
          >
            {isEditing ? 'Cancel' : 'Edit Profile'}
          </Button>
        </Box>

        {message && (
          <Alert 
            severity={message.includes('Error') ? 'error' : 'success'} 
            sx={{ mb: 3 }}
            onClose={() => setMessage('')}
          >
            {message}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          {/* Profile Picture Section */}
          <Box display="flex" alignItems="center" mb={4}>
            <Avatar
              src={formData.avatar}
              sx={{ width: 80, height: 80, mr: 3 }}
            >
              {user.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Box>
              <Typography variant="h6">{user.username}</Typography>
              <Typography variant="body2" color="text.secondary">
                {user.email}
              </Typography>
              {isEditing && (
                <Button
                  variant="outlined"
                  startIcon={<PhotoCamera />}
                  sx={{ mt: 1 }}
                  onClick={() => {
                    const url = prompt('Enter avatar URL:');
                    if (url) handleInputChange('avatar', url);
                  }}
                >
                  Change Avatar
                </Button>
              )}
            </Box>
          </Box>

          <Divider sx={{ mb: 4 }} />

          {/* Basic Information */}
          <Typography variant="h6" gutterBottom>
            Basic Information
          </Typography>
          <Box display="flex" flexDirection="column" gap={2} mb={4}>
            <TextField
              label="Display Name"
              value={formData.displayName}
              onChange={(e) => handleInputChange('displayName', e.target.value)}
              disabled={!isEditing}
              fullWidth
            />
            <TextField
              label="Bio"
              value={formData.bio}
              onChange={(e) => handleInputChange('bio', e.target.value)}
              disabled={!isEditing}
              multiline
              rows={3}
              fullWidth
              placeholder="Tell us about yourself..."
            />
          </Box>

          <Divider sx={{ mb: 4 }} />

          {/* Notification Settings */}
          <Typography variant="h6" gutterBottom>
            Notification Preferences
          </Typography>
          <Box display="flex" flexDirection="column" gap={1} mb={4}>
            <FormControlLabel
              control={
                <Switch
                  checked={formData.notifications.email}
                  onChange={(e) => handleInputChange('notifications.email', e.target.checked)}
                  disabled={!isEditing}
                />
              }
              label="Email Notifications"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formData.notifications.push}
                  onChange={(e) => handleInputChange('notifications.push', e.target.checked)}
                  disabled={!isEditing}
                />
              }
              label="Push Notifications"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formData.notifications.inApp}
                  onChange={(e) => handleInputChange('notifications.inApp', e.target.checked)}
                  disabled={!isEditing}
                />
              }
              label="In-App Notifications"
            />
          </Box>

          <Divider sx={{ mb: 4 }} />

          {/* Privacy Settings */}
          <Typography variant="h6" gutterBottom>
            Privacy Settings
          </Typography>
          <Box display="flex" flexDirection="column" gap={1} mb={4}>
            <FormControlLabel
              control={
                <Switch
                  checked={formData.privacy.showOnlineStatus}
                  onChange={(e) => handleInputChange('privacy.showOnlineStatus', e.target.checked)}
                  disabled={!isEditing}
                />
              }
              label="Show Online Status"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formData.privacy.showLastSeen}
                  onChange={(e) => handleInputChange('privacy.showLastSeen', e.target.checked)}
                  disabled={!isEditing}
                />
              }
              label="Show Last Seen"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formData.privacy.allowFriendRequests}
                  onChange={(e) => handleInputChange('privacy.allowFriendRequests', e.target.checked)}
                  disabled={!isEditing}
                />
              }
              label="Allow Friend Requests"
            />
          </Box>

          <Divider sx={{ mb: 4 }} />

          {/* Theme Settings */}
          <Typography variant="h6" gutterBottom>
            Appearance
          </Typography>
          <Box display="flex" flexDirection="column" gap={1} mb={4}>
            <TextField
              select
              label="Theme"
              value={formData.theme}
              onChange={(e) => handleInputChange('theme', e.target.value)}
              disabled={!isEditing}
              fullWidth
              SelectProps={{
                native: true,
              }}
            >
              <option value="light">Light</option>
              <option value="dark">Dark</option>
              <option value="auto">Auto</option>
            </TextField>
          </Box>

          {/* Action Buttons */}
          {isEditing && (
            <Box display="flex" gap={2} justifyContent="flex-end">
              <Button
                variant="outlined"
                onClick={handleCancel}
                disabled={saveLoading}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                startIcon={saveLoading ? <CircularProgress size={20} /> : <Save />}
                disabled={saveLoading}
              >
                {saveLoading ? 'Saving...' : 'Save Changes'}
              </Button>
            </Box>
          )}
        </form>
      </Paper>
    </Container>
  );
};

export default Profile;
