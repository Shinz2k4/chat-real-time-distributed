import React, { useEffect, useRef } from 'react';
import {
  Box,
  Paper,
  Typography,
  Avatar,
  Chip,
  IconButton,
  Menu,
  MenuItem,
  Tooltip,
  Fade,
} from '@mui/material';
import {
  MoreVert as MoreVertIcon,
  Reply as ReplyIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  ThumbUp as ThumbUpIcon,
  ThumbDown as ThumbDownIcon,
  EmojiEmotions as EmojiIcon,
} from '@mui/icons-material';
import { format } from 'date-fns';

const MessageList = ({ messages, currentUserId, onMessageReact }) => {
  const messagesEndRef = useRef(null);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [selectedMessage, setSelectedMessage] = React.useState(null);

  // Auto-scroll to bottom when new messages arrive
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleMenuOpen = (event, message) => {
    setAnchorEl(event.currentTarget);
    setSelectedMessage(message);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedMessage(null);
  };

  const handleReply = () => {
    // Handle reply functionality
    console.log('Reply to message:', selectedMessage.id);
    handleMenuClose();
  };

  const handleEdit = () => {
    // Handle edit functionality
    console.log('Edit message:', selectedMessage.id);
    handleMenuClose();
  };

  const handleDelete = () => {
    // Handle delete functionality
    console.log('Delete message:', selectedMessage.id);
    handleMenuClose();
  };

  const handleReaction = (emoji) => {
    if (selectedMessage) {
      onMessageReact(selectedMessage.id, emoji);
      handleMenuClose();
    }
  };

  const formatMessageTime = (timestamp) => {
    return format(new Date(timestamp), 'HH:mm');
  };

  const formatMessageDate = (timestamp) => {
    const messageDate = new Date(timestamp);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (messageDate.toDateString() === today.toDateString()) {
      return 'Today';
    } else if (messageDate.toDateString() === yesterday.toDateString()) {
      return 'Yesterday';
    } else {
      return format(messageDate, 'MMM dd, yyyy');
    }
  };

  const groupMessagesByDate = (messages) => {
    const groups = {};
    messages.forEach(message => {
      const date = formatMessageDate(message.createdAt);
      if (!groups[date]) {
        groups[date] = [];
      }
      groups[date].push(message);
    });
    return groups;
  };

  const messageGroups = groupMessagesByDate(messages);

  return (
    <Box sx={{ height: '100%', overflow: 'auto', p: 1 }}>
      {Object.entries(messageGroups).map(([date, dateMessages]) => (
        <Box key={date}>
          {/* Date Separator */}
          <Box sx={{ textAlign: 'center', my: 2 }}>
            <Chip
              label={date}
              size="small"
              sx={{ bgcolor: 'grey.100', color: 'text.secondary' }}
            />
          </Box>

          {/* Messages for this date */}
          {dateMessages.map((message, index) => {
            const isOwnMessage = message.senderId === currentUserId;
            const showAvatar = index === 0 || dateMessages[index - 1].senderId !== message.senderId;
            const showTime = index === dateMessages.length - 1 || 
              dateMessages[index + 1].senderId !== message.senderId;

            return (
              <Box key={message.id} sx={{ mb: 1 }}>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: isOwnMessage ? 'flex-end' : 'flex-start',
                    alignItems: 'flex-end',
                    gap: 1,
                  }}
                >
                  {/* Avatar for other users */}
                  {!isOwnMessage && showAvatar && (
                    <Avatar sx={{ width: 32, height: 32 }}>
                      {message.senderName?.charAt(0) || 'U'}
                    </Avatar>
                  )}

                  {/* Spacer for own messages */}
                  {isOwnMessage && <Box sx={{ width: 32 }} />}

                  {/* Message content */}
                  <Box
                    sx={{
                      maxWidth: '70%',
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: isOwnMessage ? 'flex-end' : 'flex-start',
                    }}
                  >
                    {/* Sender name for other users */}
                    {!isOwnMessage && showAvatar && (
                      <Typography variant="caption" color="text.secondary" sx={{ mb: 0.5 }}>
                        {message.senderName || 'Unknown User'}
                      </Typography>
                    )}

                    {/* Message bubble */}
                    <Paper
                      elevation={1}
                      sx={{
                        p: 1.5,
                        bgcolor: isOwnMessage ? 'primary.main' : 'grey.100',
                        color: isOwnMessage ? 'primary.contrastText' : 'text.primary',
                        borderRadius: 2,
                        position: 'relative',
                      }}
                    >
                      {/* Reply to message */}
                      {message.replyTo && (
                        <Box
                          sx={{
                            borderLeft: 3,
                            borderColor: isOwnMessage ? 'primary.light' : 'grey.300',
                            pl: 1,
                            mb: 1,
                            bgcolor: isOwnMessage ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.05)',
                            borderRadius: 1,
                          }}
                        >
                          <Typography variant="caption" color="text.secondary">
                            Replying to {message.replyTo.senderName}
                          </Typography>
                          <Typography variant="body2" sx={{ fontSize: '0.875rem' }}>
                            {message.replyTo.content}
                          </Typography>
                        </Box>
                      )}

                      {/* Message content */}
                      <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                        {message.content}
                      </Typography>

                      {/* Message reactions */}
                      {message.reactions && message.reactions.length > 0 && (
                        <Box sx={{ display: 'flex', gap: 0.5, mt: 1, flexWrap: 'wrap' }}>
                          {message.reactions.map((reaction, idx) => (
                            <Chip
                              key={idx}
                              label={`${reaction.emoji} ${reaction.count || 1}`}
                              size="small"
                              sx={{ fontSize: '0.75rem', height: 20 }}
                              onClick={() => handleReaction(reaction.emoji)}
                            />
                          ))}
                        </Box>
                      )}

                      {/* Message menu */}
                      <IconButton
                        size="small"
                        sx={{
                          position: 'absolute',
                          top: 4,
                          right: 4,
                          opacity: 0,
                          transition: 'opacity 0.2s',
                          '&:hover': {
                            opacity: 1,
                          },
                        }}
                        onClick={(e) => handleMenuOpen(e, message)}
                      >
                        <MoreVertIcon fontSize="small" />
                      </IconButton>
                    </Paper>

                    {/* Message time */}
                    {showTime && (
                      <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, px: 1 }}>
                        {formatMessageTime(message.createdAt)}
                        {message.isEdited && ' (edited)'}
                      </Typography>
                    )}
                  </Box>
                </Box>
              </Box>
            );
          })}
        </Box>
      ))}

      {/* Scroll anchor */}
      <div ref={messagesEndRef} />

      {/* Message context menu */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
        TransitionComponent={Fade}
      >
        <MenuItem onClick={handleReply}>
          <ReplyIcon fontSize="small" sx={{ mr: 1 }} />
          Reply
        </MenuItem>
        {selectedMessage?.senderId === currentUserId && (
          <MenuItem onClick={handleEdit}>
            <EditIcon fontSize="small" sx={{ mr: 1 }} />
            Edit
          </MenuItem>
        )}
        <MenuItem onClick={() => handleReaction('👍')}>
          <ThumbUpIcon fontSize="small" sx={{ mr: 1 }} />
          Like
        </MenuItem>
        <MenuItem onClick={() => handleReaction('👎')}>
          <ThumbDownIcon fontSize="small" sx={{ mr: 1 }} />
          Dislike
        </MenuItem>
        <MenuItem onClick={() => handleReaction('😀')}>
          <EmojiIcon fontSize="small" sx={{ mr: 1 }} />
          😀
        </MenuItem>
        {selectedMessage?.senderId === currentUserId && (
          <MenuItem onClick={handleDelete} sx={{ color: 'error.main' }}>
            <DeleteIcon fontSize="small" sx={{ mr: 1 }} />
            Delete
          </MenuItem>
        )}
      </Menu>
    </Box>
  );
};

export default MessageList;
