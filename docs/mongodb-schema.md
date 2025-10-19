# MongoDB Schema Design

## Collections Overview

### 1. Users Collection

```json
{
  "_id": "ObjectId",
  "email": "String (required, unique, indexed)",
  "username": "String (required, unique, indexed)",
  "password": "String (required, hashed with bcrypt)",
  "displayName": "String (required)",
  "avatar": "String (optional, URL to avatar image)",
  "bio": "String (optional, max 500 characters)",
  "status": "String (required, enum: ['online', 'offline', 'away', 'busy'], indexed)",
  "lastSeen": "Date (required, indexed)",
  "isActive": "Boolean (required, default: true)",
  "emailVerified": "Boolean (required, default: false)",
  "verificationToken": "String (optional)",
  "passwordResetToken": "String (optional)",
  "passwordResetExpires": "Date (optional)",
  "createdAt": "Date (required, indexed)",
  "updatedAt": "Date (required)",
  "settings": {
    "notifications": {
      "email": "Boolean (default: true)",
      "push": "Boolean (default: true)",
      "inApp": "Boolean (default: true)"
    },
    "privacy": {
      "showOnlineStatus": "Boolean (default: true)",
      "showLastSeen": "Boolean (default: true)",
      "allowFriendRequests": "Boolean (default: true)"
    },
    "theme": "String (default: 'light', enum: ['light', 'dark'])"
  },
  "socialConnections": {
    "googleId": "String (optional, indexed)",
    "facebookId": "String (optional, indexed)"
  }
}
```

**Indexes for Users Collection:**
```javascript
// Primary indexes
db.users.createIndex({ "email": 1 }, { unique: true })
db.users.createIndex({ "username": 1 }, { unique: true })
db.users.createIndex({ "status": 1 })
db.users.createIndex({ "lastSeen": -1 })

// Compound indexes for common queries
db.users.createIndex({ "status": 1, "lastSeen": -1 })
db.users.createIndex({ "isActive": 1, "createdAt": -1 })

// Text search index for user search
db.users.createIndex({ 
  "displayName": "text", 
  "username": "text", 
  "bio": "text" 
})
```

### 2. Conversations Collection

```json
{
  "_id": "ObjectId",
  "type": "String (required, enum: ['direct', 'group'], indexed)",
  "name": "String (optional, for group conversations)",
  "description": "String (optional, for group conversations)",
  "avatar": "String (optional, URL to group avatar)",
  "participants": [
    {
      "userId": "ObjectId (required, ref: users._id)",
      "role": "String (required, enum: ['admin', 'member'], default: 'member')",
      "joinedAt": "Date (required)",
      "lastReadMessageId": "ObjectId (optional, ref: messages._id)",
      "lastReadAt": "Date (optional)"
    }
  ],
  "createdBy": "ObjectId (required, ref: users._id, indexed)",
  "createdAt": "Date (required, indexed)",
  "updatedAt": "Date (required, indexed)",
  "lastMessage": {
    "messageId": "ObjectId (ref: messages._id)",
    "content": "String (preview of last message)",
    "senderId": "ObjectId (ref: users._id)",
    "timestamp": "Date"
  },
  "settings": {
    "isArchived": "Boolean (default: false, indexed)",
    "isMuted": "Boolean (default: false)",
    "allowMemberInvite": "Boolean (default: true)",
    "allowMemberLeave": "Boolean (default: true)"
  },
  "metadata": {
    "messageCount": "Number (default: 0, indexed)",
    "unreadCount": "Number (default: 0)"
  }
}
```

**Indexes for Conversations Collection:**
```javascript
// Primary indexes
db.conversations.createIndex({ "type": 1 })
db.conversations.createIndex({ "createdAt": -1 })
db.conversations.createIndex({ "updatedAt": -1 })
db.conversations.createIndex({ "settings.isArchived": 1 })

// Compound indexes for participant queries
db.conversations.createIndex({ "participants.userId": 1, "updatedAt": -1 })
db.conversations.createIndex({ "participants.userId": 1, "settings.isArchived": 1 })

// Text search for group conversations
db.conversations.createIndex({ 
  "name": "text", 
  "description": "text" 
})
```

### 3. Messages Collection

```json
{
  "_id": "ObjectId",
  "conversationId": "ObjectId (required, ref: conversations._id, indexed)",
  "senderId": "ObjectId (required, ref: users._id, indexed)",
  "content": "String (required, max 5000 characters)",
  "type": "String (required, enum: ['text', 'image', 'video', 'file', 'system'], indexed)",
  "status": "String (required, enum: ['sent', 'delivered', 'seen'], default: 'sent', indexed)",
  "replyTo": {
    "messageId": "ObjectId (ref: messages._id)",
    "senderId": "ObjectId (ref: users._id)",
    "content": "String (preview of replied message)"
  },
  "attachments": [
    {
      "fileId": "String (required)",
      "fileName": "String (required)",
      "fileSize": "Number (required, in bytes)",
      "mimeType": "String (required)",
      "url": "String (required)",
      "thumbnailUrl": "String (optional)"
    }
  ],
  "reactions": [
    {
      "userId": "ObjectId (ref: users._id)",
      "emoji": "String (required)",
      "timestamp": "Date (required)"
    }
  ],
  "mentions": [
    {
      "userId": "ObjectId (ref: users._id)",
      "username": "String",
      "position": "Number (character position in content)"
    }
  ],
  "isEdited": "Boolean (default: false)",
  "editedAt": "Date (optional)",
  "isDeleted": "Boolean (default: false, indexed)",
  "deletedAt": "Date (optional)",
  "createdAt": "Date (required, indexed)",
  "updatedAt": "Date (required)"
}
```

**Indexes for Messages Collection:**
```javascript
// Primary indexes
db.messages.createIndex({ "conversationId": 1, "createdAt": -1 })
db.messages.createIndex({ "senderId": 1, "createdAt": -1 })
db.messages.createIndex({ "type": 1 })
db.messages.createIndex({ "status": 1 })
db.messages.createIndex({ "isDeleted": 1 })

// Compound indexes for common queries
db.messages.createIndex({ "conversationId": 1, "isDeleted": 1, "createdAt": -1 })
db.messages.createIndex({ "conversationId": 1, "status": 1, "createdAt": -1 })

// Text search for message content
db.messages.createIndex({ "content": "text" })
```

### 4. Friend Requests Collection

```json
{
  "_id": "ObjectId",
  "requesterId": "ObjectId (required, ref: users._id, indexed)",
  "recipientId": "ObjectId (required, ref: users._id, indexed)",
  "status": "String (required, enum: ['pending', 'accepted', 'declined', 'cancelled'], indexed)",
  "message": "String (optional, max 200 characters)",
  "createdAt": "Date (required, indexed)",
  "updatedAt": "Date (required)",
  "respondedAt": "Date (optional)"
}
```

**Indexes for Friend Requests Collection:**
```javascript
// Primary indexes
db.friendRequests.createIndex({ "requesterId": 1, "status": 1 })
db.friendRequests.createIndex({ "recipientId": 1, "status": 1 })
db.friendRequests.createIndex({ "createdAt": -1 })

// Compound index to prevent duplicate requests
db.friendRequests.createIndex({ 
  "requesterId": 1, 
  "recipientId": 1 
}, { unique: true })
```

### 5. Notifications Collection

```json
{
  "_id": "ObjectId",
  "userId": "ObjectId (required, ref: users._id, indexed)",
  "type": "String (required, enum: ['message', 'friend_request', 'system'], indexed)",
  "title": "String (required, max 100 characters)",
  "body": "String (required, max 500 characters)",
  "data": "Object (optional, additional data for the notification)",
  "isRead": "Boolean (default: false, indexed)",
  "isDelivered": "Boolean (default: false, indexed)",
  "deliveryMethod": "String (required, enum: ['in_app', 'push', 'email'])",
  "priority": "String (required, enum: ['low', 'normal', 'high'], default: 'normal')",
  "expiresAt": "Date (optional)",
  "createdAt": "Date (required, indexed)",
  "readAt": "Date (optional)"
}
```

**Indexes for Notifications Collection:**
```javascript
// Primary indexes
db.notifications.createIndex({ "userId": 1, "createdAt": -1 })
db.notifications.createIndex({ "userId": 1, "isRead": 1 })
db.notifications.createIndex({ "type": 1 })
db.notifications.createIndex({ "isDelivered": 1 })

// Compound indexes for common queries
db.notifications.createIndex({ "userId": 1, "isRead": 1, "createdAt": -1 })
db.notifications.createIndex({ "userId": 1, "type": 1, "createdAt": -1 })
```

## Performance Optimization Recommendations

### 1. Sharding Strategy
- **Users Collection**: Shard by `_id` (automatic)
- **Messages Collection**: Shard by `conversationId` for better query performance
- **Conversations Collection**: Shard by `participants.userId` for user-centric queries

### 2. Aggregation Pipeline Optimizations
- Use `$lookup` efficiently with proper indexing
- Implement pagination with `$skip` and `$limit`
- Use `$project` to limit returned fields

### 3. Caching Strategy
- Cache frequently accessed user profiles in Redis
- Cache conversation metadata and participant lists
- Implement message pagination caching for active conversations

### 4. Data Archiving
- Archive old messages (> 1 year) to separate collection
- Implement soft delete for better performance
- Use TTL indexes for temporary data (verification tokens, etc.)
