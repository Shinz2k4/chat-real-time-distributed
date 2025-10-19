import React, { useState, useEffect } from 'react';
import {
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Typography,
  Badge,
  Chip,
  Box,
  IconButton,
  Menu,
  MenuItem,
  Fade,
} from '@mui/material';
import {
  MoreVert as MoreVertIcon,
  Archive as ArchiveIcon,
  NotificationsOff as MuteIcon,
  Delete as DeleteIcon,
  Info as InfoIcon,
  PersonAdd as AddPersonIcon,
} from '@mui/icons-material';
import { format } from 'date-fns';

const ConversationList = ({ conversations, selectedConversation, onConversationSelect }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedConversationMenu, setSelectedConversationMenu] = useState(null);

  const handleMenuOpen = (event, conversation) => {
    setAnchorEl(event.currentTarget);
    setSelectedConversationMenu(conversation);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedConversationMenu(null);
  };

  const handleArchive = () => {
    console.log('Archive conversation:', selectedConversationMenu?.id);
    handleMenuClose();
  };

  const handleMute = () => {
    console.log('Mute conversation:', selectedConversationMenu?.id);
    handleMenuClose();
  };

  const handleDelete = () => {
    console.log('Delete conversation:', selectedConversationMenu?.id);
    handleMenuClose();
  };

  const handleInfo = () => {
    console.log('Show conversation info:', selectedConversationMenu?.id);
    handleMenuClose();
  };

  const handleAddPerson = () => {
    console.log('Add person to conversation:', selectedConversationMenu?.id);
    handleMenuClose();
  };

  const formatLastMessageTime = (timestamp) => {
    if (!timestamp) return '';
    
    const messageTime = new Date(timestamp);
    const now = new Date();
    const diffInHours = (now - messageTime) / (1000 * 60 * 60);

    if (diffInHours < 24) {
      return format(messageTime, 'HH:mm');
    } else if (diffInHours < 168) { // 7 days
      return format(messageTime, 'EEE');
    } else {
      return format(messageTime, 'MMM dd');
    }
  };

  const getConversationDisplayName = (conversation) => {
    if (conversation.type === 'GROUP') {
      return conversation.name || 'Group Chat';
    } else {
      // For direct messages, show the other participant's name
      const otherParticipant = conversation.participants?.find(
        p => p.userId !== 'current-user-id' // Replace with actual current user ID
      );
      return otherParticipant?.displayName || 'Direct Message';
    }
  };

  const getConversationAvatar = (conversation) => {
    if (conversation.avatar) {
      return conversation.avatar;
    }
    
    if (conversation.type === 'GROUP') {
      return conversation.name?.charAt(0) || 'G';
    } else {
      const otherParticipant = conversation.participants?.find(
        p => p.userId !== 'current-user-id' // Replace with actual current user ID
      );
      return otherParticipant?.displayName?.charAt(0) || 'U';
    }
  };

  const getUnreadCount = (conversation) => {
    return conversation.metadata?.unreadCount || 0;
  };

  const isOnline = (conversation) => {
    // This would typically check the user's online status
    // For now, return a mock value
    return Math.random() > 0.5;
  };

  return (
    <List sx={{ flexGrow: 1, overflow: 'auto' }}>
      {conversations.map((conversation) => {
        const unreadCount = getUnreadCount(conversation);
        const isSelected = selectedConversation?.id === conversation.id;
        const displayName = getConversationDisplayName(conversation);
        const avatar = getConversationAvatar(conversation);

        return (
          <ListItem
            key={conversation.id}
            button
            onClick={() => onConversationSelect(conversation)}
            selected={isSelected}
            sx={{
              '&:hover': {
                bgcolor: 'action.hover',
              },
              '&.Mui-selected': {
                bgcolor: 'primary.light',
                '&:hover': {
                  bgcolor: 'primary.light',
                },
              },
            }}
          >
            <ListItemAvatar>
              <Badge
                overlap="circular"
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                badgeContent={
                  isOnline(conversation) ? (
                    <Box
                      sx={{
                        width: 12,
                        height: 12,
                        borderRadius: '50%',
                        bgcolor: 'success.main',
                        border: 2,
                        borderColor: 'white',
                      }}
                    />
                  ) : null
                }
              >
                <Avatar
                  src={conversation.avatar}
                  sx={{
                    bgcolor: isSelected ? 'primary.dark' : 'primary.main',
                    color: 'white',
                  }}
                >
                  {avatar}
                </Avatar>
              </Badge>
            </ListItemAvatar>

            <ListItemText
              primary={
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Typography
                    variant="subtitle2"
                    sx={{
                      fontWeight: unreadCount > 0 ? 'bold' : 'normal',
                      color: isSelected ? 'primary.contrastText' : 'text.primary',
                    }}
                  >
                    {displayName}
                  </Typography>
                  <Typography
                    variant="caption"
                    color="text.secondary"
                    sx={{ ml: 1 }}
                  >
                    {formatLastMessageTime(conversation.lastMessage?.timestamp)}
                  </Typography>
                </Box>
              }
              secondary={
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                      maxWidth: '70%',
                      fontWeight: unreadCount > 0 ? 'bold' : 'normal',
                    }}
                  >
                    {conversation.lastMessage?.content || 'No messages yet'}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    {unreadCount > 0 && (
                      <Chip
                        label={unreadCount > 99 ? '99+' : unreadCount}
                        size="small"
                        color="primary"
                        sx={{
                          minWidth: 20,
                          height: 20,
                          fontSize: '0.75rem',
                          '& .MuiChip-label': {
                            px: 0.5,
                          },
                        }}
                      />
                    )}
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleMenuOpen(e, conversation);
                      }}
                      sx={{
                        opacity: 0,
                        transition: 'opacity 0.2s',
                        '&:hover': {
                          opacity: 1,
                        },
                      }}
                    >
                      <MoreVertIcon fontSize="small" />
                    </IconButton>
                  </Box>
                </Box>
              }
            />
          </ListItem>
        );
      })}

      {/* Conversation context menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        TransitionComponent={Fade}
      >
        <MenuItem onClick={handleInfo}>
          <InfoIcon fontSize="small" sx={{ mr: 1 }} />
          Conversation Info
        </MenuItem>
        <MenuItem onClick={handleAddPerson}>
          <AddPersonIcon fontSize="small" sx={{ mr: 1 }} />
          Add People
        </MenuItem>
        <MenuItem onClick={handleMute}>
          <MuteIcon fontSize="small" sx={{ mr: 1 }} />
          Mute Notifications
        </MenuItem>
        <MenuItem onClick={handleArchive}>
          <ArchiveIcon fontSize="small" sx={{ mr: 1 }} />
          Archive
        </MenuItem>
        <MenuItem onClick={handleDelete} sx={{ color: 'error.main' }}>
          <DeleteIcon fontSize="small" sx={{ mr: 1 }} />
          Delete
        </MenuItem>
      </Menu>
    </List>
  );
};

export default ConversationList;
