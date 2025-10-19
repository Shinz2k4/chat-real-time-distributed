import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Snackbar, Alert, IconButton, Slide } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { removeNotification } from '../store/slices/notificationSlice';

/**
 * NotificationSnackbar Component
 * 
 * Displays notifications as snackbar alerts at the bottom of the screen.
 * Automatically removes notifications after a timeout or when manually dismissed.
 * 
 * @returns {React.ReactNode} Notification snackbar component
 */
const NotificationSnackbar = () => {
  const dispatch = useDispatch();
  const { notifications } = useSelector(state => state.notifications || { notifications: [] });
  const [open, setOpen] = useState(false);
  const [currentNotification, setCurrentNotification] = useState(null);

  // Handle notification display
  useEffect(() => {
    if (notifications && notifications.length > 0) {
      const latestNotification = notifications[notifications.length - 1];
      setCurrentNotification(latestNotification);
      setOpen(true);
    } else {
      setOpen(false);
    }
  }, [notifications]);

  // Handle snackbar close
  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setOpen(false);
  };

  // Handle notification removal
  const handleExited = () => {
    if (currentNotification) {
      dispatch(removeNotification(currentNotification.id));
      setCurrentNotification(null);
    }
  };

  // Handle manual dismiss
  const handleDismiss = () => {
    setOpen(false);
  };

  // Auto-hide after timeout
  useEffect(() => {
    if (open && currentNotification) {
      const timeout = setTimeout(() => {
        setOpen(false);
      }, currentNotification.duration || 6000); // Default 6 seconds

      return () => clearTimeout(timeout);
    }
  }, [open, currentNotification]);

  if (!currentNotification) {
    return null;
  }

  return (
    <Snackbar
      open={open}
      autoHideDuration={currentNotification.duration || 6000}
      onClose={handleClose}
      onExited={handleExited}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'right',
      }}
      TransitionComponent={Slide}
      TransitionProps={{
        direction: 'up',
      }}
    >
      <Alert
        onClose={handleDismiss}
        severity={currentNotification.type || 'info'}
        variant="filled"
        action={
          <IconButton
            size="small"
            aria-label="close"
            color="inherit"
            onClick={handleDismiss}
          >
            <CloseIcon fontSize="small" />
          </IconButton>
        }
        sx={{
          width: '100%',
          '& .MuiAlert-message': {
            width: '100%',
          },
        }}
      >
        <div>
          {currentNotification.title && (
            <div style={{ fontWeight: 'bold', marginBottom: '4px' }}>
              {currentNotification.title}
            </div>
          )}
          <div>{currentNotification.message}</div>
        </div>
      </Alert>
    </Snackbar>
  );
};

export default NotificationSnackbar;
