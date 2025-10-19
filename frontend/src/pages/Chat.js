import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  TextField,
  IconButton,
  Divider,
  Chip,
  Badge,
  Fab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  InputAdornment,
  Tooltip,
} from '@mui/material';
import {
  Send as SendIcon,
  Add as AddIcon,
  Search as SearchIcon,
  MoreVert as MoreVertIcon,
  AttachFile as AttachFileIcon,
  EmojiEmotions as EmojiIcon,
  Videocam as VideoIcon,
  Phone as PhoneIcon,
  Info as InfoIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { useChatWebSocket } from '../hooks/useChatWebSocket';
import MessageList from '../components/MessageList';
import ConversationList from '../components/ConversationList';
import UserSearch from '../components/UserSearch';

const Chat = () => {
  const { user } = useAuth();
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [message, setMessage] = useState('');
  const [searchOpen, setSearchOpen] = useState(false);
  const [conversations, setConversations] = useState([]);
  const [messages, setMessages] = useState([]);
  const [typingUsers, setTypingUsers] = useState([]);
  const [isTyping, setIsTyping] = useState(false);

  // WebSocket connection
  const {
    isConnected,
    sendTextMessage,
    sendTypingIndicator,
    subscribeToMessages,
    subscribeToTyping,
    subscribeToPresence,
  } = useChatWebSocket(selectedConversation?.id);

  // Handle message sending
  const handleSendMessage = () => {
    if (message.trim() && selectedConversation) {
      sendTextMessage(message, selectedConversation.id);
      setMessage('');
    }
  };

  // Handle typing indicator
  const handleTyping = (isTyping) => {
    if (selectedConversation) {
      sendTypingIndicator(selectedConversation.id, isTyping);
      setIsTyping(isTyping);
    }
  };

  // Handle conversation selection
  const handleConversationSelect = (conversation) => {
    setSelectedConversation(conversation);
    setMessages([]);
  };

  // Subscribe to messages
  useEffect(() => {
    if (selectedConversation && isConnected) {
      const unsubscribe = subscribeToMessages(selectedConversation.id, (newMessage) => {
        setMessages(prev => [...prev, newMessage]);
      });
      return unsubscribe;
    }
  }, [selectedConversation, isConnected, subscribeToMessages]);

  // Subscribe to typing indicators
  useEffect(() => {
    if (selectedConversation && isConnected) {
      const unsubscribe = subscribeToTyping(selectedConversation.id, (typingData) => {
        if (typingData.userId !== user?.id) {
          setTypingUsers(prev => {
            if (typingData.isTyping) {
              return [...prev.filter(u => u.userId !== typingData.userId), typingData];
            } else {
              return prev.filter(u => u.userId !== typingData.userId);
            }
          });
        }
      });
      return unsubscribe;
    }
  }, [selectedConversation, isConnected, subscribeToTyping, user?.id]);

  // Subscribe to presence updates
  useEffect(() => {
    if (isConnected) {
      const unsubscribe = subscribeToPresence((presenceData) => {
        // Handle presence updates
        console.log('Presence update:', presenceData);
      });
      return unsubscribe;
    }
  }, [isConnected, subscribeToPresence]);

  return (
    <Box sx={{ height: '100vh', display: 'flex' }}>
      {/* Sidebar */}
      <Paper sx={{ width: 300, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
          <Typography variant="h6" component="h1">
            Conversations
          </Typography>
          <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
            <TextField
              size="small"
              placeholder="Search conversations..."
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
              sx={{ flexGrow: 1 }}
            />
            <IconButton onClick={() => setSearchOpen(true)}>
              <AddIcon />
            </IconButton>
          </Box>
        </Box>
        
        <ConversationList
          conversations={conversations}
          selectedConversation={selectedConversation}
          onConversationSelect={handleConversationSelect}
        />
      </Paper>

      {/* Main Chat Area */}
      <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        {selectedConversation ? (
          <>
            {/* Chat Header */}
            <Paper sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Avatar>
                    {selectedConversation.name?.charAt(0) || 'C'}
                  </Avatar>
                  <Box>
                    <Typography variant="h6">
                      {selectedConversation.name || 'Direct Message'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {selectedConversation.participants?.length || 0} participants
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Tooltip title="Voice Call">
                    <IconButton>
                      <PhoneIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Video Call">
                    <IconButton>
                      <VideoIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Conversation Info">
                    <IconButton>
                      <InfoIcon />
                    </IconButton>
                  </Tooltip>
                  <IconButton>
                    <MoreVertIcon />
                  </IconButton>
                </Box>
              </Box>
            </Paper>

            {/* Messages Area */}
            <Box sx={{ flexGrow: 1, overflow: 'hidden' }}>
              <MessageList
                messages={messages}
                currentUserId={user?.id}
                onMessageReact={(messageId, emoji) => {
                  // Handle message reaction
                  console.log('React to message:', messageId, emoji);
                }}
              />
            </Box>

            {/* Typing Indicator */}
            {typingUsers.length > 0 && (
              <Box sx={{ px: 2, py: 1 }}>
                <Typography variant="body2" color="text.secondary">
                  {typingUsers.map(u => u.userId).join(', ')} {typingUsers.length === 1 ? 'is' : 'are'} typing...
                </Typography>
              </Box>
            )}

            {/* Message Input */}
            <Paper sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
              <Box sx={{ display: 'flex', gap: 1, alignItems: 'flex-end' }}>
                <IconButton>
                  <AttachFileIcon />
                </IconButton>
                <TextField
                  fullWidth
                  multiline
                  maxRows={4}
                  placeholder="Type a message..."
                  value={message}
                  onChange={(e) => {
                    setMessage(e.target.value);
                    if (e.target.value.trim()) {
                      handleTyping(true);
                    } else {
                      handleTyping(false);
                    }
                  }}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter' && !e.shiftKey) {
                      e.preventDefault();
                      handleSendMessage();
                    }
                  }}
                  onBlur={() => handleTyping(false)}
                />
                <IconButton>
                  <EmojiIcon />
                </IconButton>
                <IconButton
                  color="primary"
                  onClick={handleSendMessage}
                  disabled={!message.trim()}
                >
                  <SendIcon />
                </IconButton>
              </Box>
            </Paper>
          </>
        ) : (
          <Box sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            height: '100%',
            flexDirection: 'column',
            gap: 2
          }}>
            <Typography variant="h4" color="text.secondary">
              Welcome to Chat
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Select a conversation to start chatting
            </Typography>
          </Box>
        )}
      </Box>

      {/* User Search Dialog */}
      <Dialog open={searchOpen} onClose={() => setSearchOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Start New Conversation</DialogTitle>
        <DialogContent>
          <UserSearch
            onUserSelect={(user) => {
              // Handle user selection for new conversation
              console.log('Selected user:', user);
              setSearchOpen(false);
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSearchOpen(false)}>Cancel</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Chat;
