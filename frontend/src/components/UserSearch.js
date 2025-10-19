import React, { useState, useEffect } from 'react';
import {
  Box,
  TextField,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Typography,
  CircularProgress,
  Alert,
  InputAdornment,
  Chip,
  Button,
} from '@mui/material';
import {
  Search as SearchIcon,
  PersonAdd as PersonAddIcon,
  Check as CheckIcon,
} from '@mui/icons-material';

const UserSearch = ({ onUserSelect }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedUsers, setSelectedUsers] = useState([]);

  // Debounced search
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      if (searchTerm.trim().length >= 2) {
        searchUsers(searchTerm);
      } else {
        setUsers([]);
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  const searchUsers = async (query) => {
    setLoading(true);
    setError('');

    try {
      const response = await fetch(`/api/users/search?query=${encodeURIComponent(query)}`);
      if (!response.ok) {
        throw new Error('Failed to search users');
      }

      const data = await response.json();
      setUsers(data.users || []);
    } catch (err) {
      setError('Failed to search users. Please try again.');
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleUserSelect = (user) => {
    if (selectedUsers.find(u => u.id === user.id)) {
      // User already selected, remove them
      setSelectedUsers(prev => prev.filter(u => u.id !== user.id));
    } else {
      // Add user to selection
      setSelectedUsers(prev => [...prev, user]);
    }
  };

  const handleCreateConversation = () => {
    if (selectedUsers.length > 0) {
      onUserSelect(selectedUsers);
    }
  };

  const isUserSelected = (user) => {
    return selectedUsers.some(u => u.id === user.id);
  };

  const getInitials = (name) => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <Box>
      <TextField
        fullWidth
        placeholder="Search for users..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
        }}
        sx={{ mb: 2 }}
      />

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
          <CircularProgress size={24} />
        </Box>
      )}

      {selectedUsers.length > 0 && (
        <Box sx={{ mb: 2 }}>
          <Typography variant="subtitle2" gutterBottom>
            Selected Users:
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {selectedUsers.map(user => (
              <Chip
                key={user.id}
                label={user.displayName}
                avatar={<Avatar sx={{ width: 24, height: 24 }}>{getInitials(user.displayName)}</Avatar>}
                onDelete={() => handleUserSelect(user)}
                color="primary"
                variant="outlined"
              />
            ))}
          </Box>
        </Box>
      )}

      {users.length > 0 && (
        <List sx={{ maxHeight: 300, overflow: 'auto' }}>
          {users.map(user => (
            <ListItem
              key={user.id}
              button
              onClick={() => handleUserSelect(user)}
              sx={{
                '&:hover': {
                  bgcolor: 'action.hover',
                },
                bgcolor: isUserSelected(user) ? 'primary.light' : 'transparent',
              }}
            >
              <ListItemAvatar>
                <Avatar
                  src={user.avatar}
                  sx={{
                    bgcolor: isUserSelected(user) ? 'primary.dark' : 'primary.main',
                    color: 'white',
                  }}
                >
                  {getInitials(user.displayName)}
                </Avatar>
              </ListItemAvatar>
              <ListItemText
                primary={user.displayName}
                secondary={
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="body2" color="text.secondary">
                      @{user.username}
                    </Typography>
                    <Chip
                      label={user.status || 'offline'}
                      size="small"
                      color={user.status === 'online' ? 'success' : 'default'}
                      sx={{ fontSize: '0.75rem', height: 20 }}
                    />
                  </Box>
                }
              />
              {isUserSelected(user) && (
                <CheckIcon color="primary" />
              )}
            </ListItem>
          ))}
        </List>
      )}

      {searchTerm.length > 0 && users.length === 0 && !loading && (
        <Box sx={{ textAlign: 'center', p: 2 }}>
          <Typography variant="body2" color="text.secondary">
            No users found for "{searchTerm}"
          </Typography>
        </Box>
      )}

      {selectedUsers.length > 0 && (
        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
          <Button
            variant="contained"
            startIcon={<PersonAddIcon />}
            onClick={handleCreateConversation}
          >
            Start Conversation
          </Button>
        </Box>
      )}
    </Box>
  );
};

export default UserSearch;
