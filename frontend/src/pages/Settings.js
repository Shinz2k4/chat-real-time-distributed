import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { 
  Container, 
  Paper, 
  Typography, 
  Box, 
  Switch, 
  FormControlLabel,
  Divider,
  Alert,
  CircularProgress,
  Tabs,
  Tab,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField
} from '@mui/material';
import { 
  Notifications, 
  Security, 
  Palette, 
  Language, 
  Storage,
  Delete,
  Edit,
  Save,
  Cancel
} from '@mui/icons-material';

/**
 * Settings Page Component
 * 
 * Provides comprehensive application settings including:
 * - Notification preferences
 * - Privacy and security settings
 * - Appearance and theme options
 * - Language and localization
 * - Data management
 */
const Settings = () => {
  const dispatch = useDispatch();
  const { user, loading, error } = useSelector(state => state.user);

  // Local state
  const [activeTab, setActiveTab] = useState(0);
  const [settings, setSettings] = useState({
    notifications: {
      email: true,
      push: true,
      inApp: true,
      sound: true,
      desktop: true
    },
    privacy: {
      showOnlineStatus: true,
      showLastSeen: true,
      allowFriendRequests: true,
      allowMessages: true,
      showReadReceipts: true
    },
    appearance: {
      theme: 'light',
      fontSize: 'medium',
      compactMode: false,
      showAvatars: true,
      showTimestamps: true
    },
    language: {
      locale: 'en',
      timezone: 'UTC',
      dateFormat: 'MM/DD/YYYY',
      timeFormat: '12h'
    },
    data: {
      autoDeleteMessages: false,
      deleteAfterDays: 30,
      exportData: false,
      clearCache: false
    }
  });

  const [message, setMessage] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  // Initialize settings when user data loads
  useEffect(() => {
    if (user && user.settings) {
      setSettings(prev => ({
        ...prev,
        notifications: {
          ...prev.notifications,
          ...user.settings.notifications
        },
        privacy: {
          ...prev.privacy,
          ...user.settings.privacy
        },
        appearance: {
          ...prev.appearance,
          theme: user.settings.theme || 'light'
        }
      }));
    }
  }, [user]);

  // Handle tab change
  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  // Handle setting changes
  const handleSettingChange = (category, setting, value) => {
    setSettings(prev => ({
      ...prev,
      [category]: {
        ...prev[category],
        [setting]: value
      }
    }));
  };

  // Handle save settings
  const handleSave = async () => {
    setMessage('');
    try {
      // Here you would dispatch an action to save settings
      // await dispatch(updateSettings(settings));
      setMessage('Settings saved successfully!');
      setIsEditing(false);
    } catch (error) {
      setMessage(`Error saving settings: ${error.message}`);
    }
  };

  // Handle cancel editing
  const handleCancel = () => {
    if (user && user.settings) {
      setSettings(prev => ({
        ...prev,
        notifications: {
          ...prev.notifications,
          ...user.settings.notifications
        },
        privacy: {
          ...prev.privacy,
          ...user.settings.privacy
        },
        appearance: {
          ...prev.appearance,
          theme: user.settings.theme || 'light'
        }
      }));
    }
    setIsEditing(false);
    setMessage('');
  };

  // Handle data deletion
  const handleDeleteData = async () => {
    try {
      // Here you would dispatch an action to delete user data
      // await dispatch(deleteUserData());
      setMessage('Data deleted successfully!');
      setDeleteDialogOpen(false);
    } catch (error) {
      setMessage(`Error deleting data: ${error.message}`);
    }
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
        <Alert severity="warning">Please log in to access settings.</Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h4" component="h1">
            Settings
          </Typography>
          <Box>
            {isEditing ? (
              <Box display="flex" gap={1}>
                <Button
                  variant="outlined"
                  startIcon={<Cancel />}
                  onClick={handleCancel}
                >
                  Cancel
                </Button>
                <Button
                  variant="contained"
                  startIcon={<Save />}
                  onClick={handleSave}
                >
                  Save
                </Button>
              </Box>
            ) : (
              <Button
                variant="contained"
                startIcon={<Edit />}
                onClick={() => setIsEditing(true)}
              >
                Edit Settings
              </Button>
            )}
          </Box>
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

        <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 3 }}>
          <Tab icon={<Notifications />} label="Notifications" />
          <Tab icon={<Security />} label="Privacy" />
          <Tab icon={<Palette />} label="Appearance" />
          <Tab icon={<Language />} label="Language" />
          <Tab icon={<Storage />} label="Data" />
        </Tabs>

        {/* Notifications Tab */}
        {activeTab === 0 && (
          <Box>
            <Typography variant="h6" gutterBottom>
              Notification Preferences
            </Typography>
            <List>
              <ListItem>
                <ListItemText 
                  primary="Email Notifications" 
                  secondary="Receive notifications via email"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.notifications.email}
                    onChange={(e) => handleSettingChange('notifications', 'email', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Push Notifications" 
                  secondary="Receive push notifications on your device"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.notifications.push}
                    onChange={(e) => handleSettingChange('notifications', 'push', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="In-App Notifications" 
                  secondary="Show notifications within the application"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.notifications.inApp}
                    onChange={(e) => handleSettingChange('notifications', 'inApp', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Sound Notifications" 
                  secondary="Play sound for new messages"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.notifications.sound}
                    onChange={(e) => handleSettingChange('notifications', 'sound', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Desktop Notifications" 
                  secondary="Show desktop notifications"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.notifications.desktop}
                    onChange={(e) => handleSettingChange('notifications', 'desktop', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
            </List>
          </Box>
        )}

        {/* Privacy Tab */}
        {activeTab === 1 && (
          <Box>
            <Typography variant="h6" gutterBottom>
              Privacy & Security
            </Typography>
            <List>
              <ListItem>
                <ListItemText 
                  primary="Show Online Status" 
                  secondary="Let others see when you're online"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.privacy.showOnlineStatus}
                    onChange={(e) => handleSettingChange('privacy', 'showOnlineStatus', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Show Last Seen" 
                  secondary="Let others see when you were last active"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.privacy.showLastSeen}
                    onChange={(e) => handleSettingChange('privacy', 'showLastSeen', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Allow Friend Requests" 
                  secondary="Let others send you friend requests"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.privacy.allowFriendRequests}
                    onChange={(e) => handleSettingChange('privacy', 'allowFriendRequests', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Allow Messages" 
                  secondary="Let others send you direct messages"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.privacy.allowMessages}
                    onChange={(e) => handleSettingChange('privacy', 'allowMessages', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Show Read Receipts" 
                  secondary="Let others see when you've read their messages"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.privacy.showReadReceipts}
                    onChange={(e) => handleSettingChange('privacy', 'showReadReceipts', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
            </List>
          </Box>
        )}

        {/* Appearance Tab */}
        {activeTab === 2 && (
          <Box>
            <Typography variant="h6" gutterBottom>
              Appearance
            </Typography>
            <List>
              <ListItem>
                <ListItemText 
                  primary="Theme" 
                  secondary="Choose your preferred theme"
                />
                <ListItemSecondaryAction>
                  <TextField
                    select
                    value={settings.appearance.theme}
                    onChange={(e) => handleSettingChange('appearance', 'theme', e.target.value)}
                    disabled={!isEditing}
                    size="small"
                    SelectProps={{
                      native: true,
                    }}
                  >
                    <option value="light">Light</option>
                    <option value="dark">Dark</option>
                    <option value="auto">Auto</option>
                  </TextField>
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Font Size" 
                  secondary="Adjust text size"
                />
                <ListItemSecondaryAction>
                  <TextField
                    select
                    value={settings.appearance.fontSize}
                    onChange={(e) => handleSettingChange('appearance', 'fontSize', e.target.value)}
                    disabled={!isEditing}
                    size="small"
                    SelectProps={{
                      native: true,
                    }}
                  >
                    <option value="small">Small</option>
                    <option value="medium">Medium</option>
                    <option value="large">Large</option>
                  </TextField>
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Compact Mode" 
                  secondary="Use less space for messages"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.appearance.compactMode}
                    onChange={(e) => handleSettingChange('appearance', 'compactMode', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Show Avatars" 
                  secondary="Display user avatars in messages"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.appearance.showAvatars}
                    onChange={(e) => handleSettingChange('appearance', 'showAvatars', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Show Timestamps" 
                  secondary="Display message timestamps"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.appearance.showTimestamps}
                    onChange={(e) => handleSettingChange('appearance', 'showTimestamps', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
            </List>
          </Box>
        )}

        {/* Language Tab */}
        {activeTab === 3 && (
          <Box>
            <Typography variant="h6" gutterBottom>
              Language & Localization
            </Typography>
            <List>
              <ListItem>
                <ListItemText 
                  primary="Language" 
                  secondary="Choose your preferred language"
                />
                <ListItemSecondaryAction>
                  <TextField
                    select
                    value={settings.language.locale}
                    onChange={(e) => handleSettingChange('language', 'locale', e.target.value)}
                    disabled={!isEditing}
                    size="small"
                    SelectProps={{
                      native: true,
                    }}
                  >
                    <option value="en">English</option>
                    <option value="es">Spanish</option>
                    <option value="fr">French</option>
                    <option value="de">German</option>
                    <option value="zh">Chinese</option>
                    <option value="ja">Japanese</option>
                  </TextField>
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Timezone" 
                  secondary="Set your timezone"
                />
                <ListItemSecondaryAction>
                  <TextField
                    select
                    value={settings.language.timezone}
                    onChange={(e) => handleSettingChange('language', 'timezone', e.target.value)}
                    disabled={!isEditing}
                    size="small"
                    SelectProps={{
                      native: true,
                    }}
                  >
                    <option value="UTC">UTC</option>
                    <option value="America/New_York">Eastern Time</option>
                    <option value="America/Chicago">Central Time</option>
                    <option value="America/Denver">Mountain Time</option>
                    <option value="America/Los_Angeles">Pacific Time</option>
                    <option value="Europe/London">London</option>
                    <option value="Europe/Paris">Paris</option>
                    <option value="Asia/Tokyo">Tokyo</option>
                  </TextField>
                </ListItemSecondaryAction>
              </ListItem>
            </List>
          </Box>
        )}

        {/* Data Tab */}
        {activeTab === 4 && (
          <Box>
            <Typography variant="h6" gutterBottom>
              Data Management
            </Typography>
            <List>
              <ListItem>
                <ListItemText 
                  primary="Auto-delete Messages" 
                  secondary="Automatically delete old messages"
                />
                <ListItemSecondaryAction>
                  <Switch
                    checked={settings.data.autoDeleteMessages}
                    onChange={(e) => handleSettingChange('data', 'autoDeleteMessages', e.target.checked)}
                    disabled={!isEditing}
                  />
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Export Data" 
                  secondary="Download your data"
                />
                <ListItemSecondaryAction>
                  <Button
                    variant="outlined"
                    size="small"
                    onClick={() => setMessage('Data export feature coming soon!')}
                  >
                    Export
                  </Button>
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Clear Cache" 
                  secondary="Clear application cache"
                />
                <ListItemSecondaryAction>
                  <Button
                    variant="outlined"
                    size="small"
                    onClick={() => setMessage('Cache cleared!')}
                  >
                    Clear
                  </Button>
                </ListItemSecondaryAction>
              </ListItem>
              <ListItem>
                <ListItemText 
                  primary="Delete Account Data" 
                  secondary="Permanently delete all your data"
                />
                <ListItemSecondaryAction>
                  <Button
                    variant="outlined"
                    color="error"
                    size="small"
                    startIcon={<Delete />}
                    onClick={() => setDeleteDialogOpen(true)}
                  >
                    Delete
                  </Button>
                </ListItemSecondaryAction>
              </ListItem>
            </List>
          </Box>
        )}

        {/* Delete Confirmation Dialog */}
        <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
          <DialogTitle>Delete Account Data</DialogTitle>
          <DialogContent>
            <Typography>
              Are you sure you want to permanently delete all your account data? 
              This action cannot be undone.
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
            <Button onClick={handleDeleteData} color="error" variant="contained">
              Delete Data
            </Button>
          </DialogActions>
        </Dialog>
      </Paper>
    </Container>
  );
};

export default Settings;
