import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  IconButton,
  Menu,
  MenuItem,
  Avatar,
  Box,
  Badge,
  Tooltip,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider
} from '@mui/material';
import {
  Menu as MenuIcon,
  Notifications,
  Settings,
  Logout,
  Chat,
  People,
  Home,
  Person
} from '@mui/icons-material';
import { logout } from '../store/slices/authSlice';

/**
 * Navbar Component
 * 
 * Main navigation bar for the chat application.
 * Provides navigation links, user menu, and notifications.
 * 
 * @returns {React.ReactNode} Navigation bar component
 */
const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  // const { notifications } = useSelector(state => state.notifications || { notifications: [] });
  const notifications = []; // Temporary fix

  // Local state
  const [anchorEl, setAnchorEl] = useState(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [notificationAnchor, setNotificationAnchor] = useState(null);

  // Handle user menu
  const handleUserMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleUserMenuClose = () => {
    setAnchorEl(null);
  };

  // Handle logout
  const handleLogout = () => {
    logout();
    navigate('/login');
    handleUserMenuClose();
  };

  // Handle navigation
  const handleNavigation = (path) => {
    navigate(path);
    setMobileMenuOpen(false);
  };

  // Handle notification menu
  const handleNotificationClick = (event) => {
    setNotificationAnchor(event.currentTarget);
  };

  const handleNotificationClose = () => {
    setNotificationAnchor(null);
  };

  // Navigation items
  const navigationItems = [
    { label: 'Home', path: '/', icon: <Home /> },
    { label: 'Chat', path: '/chat', icon: <Chat /> },
    { label: 'Friends', path: '/friends', icon: <People /> },
  ];

  // Check if current path is active
  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <>
      <AppBar position="static" elevation={1}>
        <Toolbar>
          {/* Mobile menu button */}
          <IconButton
            edge="start"
            color="inherit"
            aria-label="menu"
            onClick={() => setMobileMenuOpen(true)}
            sx={{ mr: 2, display: { md: 'none' } }}
          >
            <MenuIcon />
          </IconButton>

          {/* Logo/Brand */}
          <Typography
            variant="h6"
            component="div"
            sx={{ 
              flexGrow: 1, 
              cursor: 'pointer',
              fontWeight: 'bold'
            }}
            onClick={() => navigate('/')}
          >
            ChatApp
          </Typography>

          {/* Desktop Navigation */}
          <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 1 }}>
            {navigationItems.map((item) => (
              <Button
                key={item.path}
                color="inherit"
                startIcon={item.icon}
                onClick={() => handleNavigation(item.path)}
                sx={{
                  backgroundColor: isActive(item.path) ? 'rgba(255,255,255,0.1)' : 'transparent',
                  '&:hover': {
                    backgroundColor: 'rgba(255,255,255,0.1)',
                  },
                }}
              >
                {item.label}
              </Button>
            ))}
          </Box>

          {/* Right side actions */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {/* Notifications */}
            <Tooltip title="Notifications">
              <IconButton
                color="inherit"
                onClick={handleNotificationClick}
              >
                <Badge badgeContent={notifications?.length || 0} color="error">
                  <Notifications />
                </Badge>
              </IconButton>
            </Tooltip>

            {/* User Menu */}
            {isAuthenticated ? (
              <>
                <IconButton
                  onClick={handleUserMenuOpen}
                  sx={{ p: 0, ml: 1 }}
                >
                  <Avatar
                    src={user?.avatar}
                    alt={user?.displayName || user?.username}
                    sx={{ width: 32, height: 32 }}
                  >
                    {user?.displayName?.charAt(0) || user?.username?.charAt(0)}
                  </Avatar>
                </IconButton>

                <Menu
                  anchorEl={anchorEl}
                  open={Boolean(anchorEl)}
                  onClose={handleUserMenuClose}
                  anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right',
                  }}
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                  }}
                >
                  <MenuItem onClick={() => { handleNavigation('/profile'); handleUserMenuClose(); }}>
                    <ListItemIcon>
                      <Person fontSize="small" />
                    </ListItemIcon>
                    Profile
                  </MenuItem>
                  <MenuItem onClick={() => { handleNavigation('/settings'); handleUserMenuClose(); }}>
                    <ListItemIcon>
                      <Settings fontSize="small" />
                    </ListItemIcon>
                    Settings
                  </MenuItem>
                  <Divider />
                  <MenuItem onClick={handleLogout}>
                    <ListItemIcon>
                      <Logout fontSize="small" />
                    </ListItemIcon>
                    Logout
                  </MenuItem>
                </Menu>
              </>
            ) : (
              <Button color="inherit" onClick={() => navigate('/login')}>
                Login
              </Button>
            )}
          </Box>
        </Toolbar>
      </AppBar>

      {/* Mobile Drawer */}
      <Drawer
        anchor="left"
        open={mobileMenuOpen}
        onClose={() => setMobileMenuOpen(false)}
      >
        <Box sx={{ width: 250 }}>
          <Toolbar>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              Menu
            </Typography>
          </Toolbar>
          <Divider />
          <List>
            {navigationItems.map((item) => (
              <ListItem
                key={item.path}
                button
                onClick={() => handleNavigation(item.path)}
                selected={isActive(item.path)}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItem>
            ))}
          </List>
        </Box>
      </Drawer>

      {/* Notifications Menu */}
      <Menu
        anchorEl={notificationAnchor}
        open={Boolean(notificationAnchor)}
        onClose={handleNotificationClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        {notifications && notifications.length > 0 ? (
          notifications.map((notification, index) => (
            <MenuItem key={index} onClick={handleNotificationClose}>
              <ListItemText
                primary={notification.title}
                secondary={notification.message}
              />
            </MenuItem>
          ))
        ) : (
          <MenuItem disabled>
            <ListItemText primary="No notifications" />
          </MenuItem>
        )}
      </Menu>
    </>
  );
};

export default Navbar;
