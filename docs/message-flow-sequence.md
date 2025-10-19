# Core Message Flow Sequence Diagram

## User A sends a 1-to-1 message to User B

This diagram illustrates the complete flow when User A sends a message to User B in the distributed chat application.

```mermaid
sequenceDiagram
    participant ClientA as React App (User A)
    participant Gateway as API Gateway
    participant ChatService as Chat Service
    participant WebSocket as WebSocket Broker
    participant MongoDB as MongoDB
    participant Redis as Redis
    participant ClientB as React App (User B)
    participant NotificationService as Notification Service

    Note over ClientA, NotificationService: User A sends a 1-to-1 message to User B

    %% Step 1: User A types and sends message
    ClientA->>ClientA: User types message
    ClientA->>WebSocket: Send message via STOMP
    Note right of ClientA: /app/chat.send<br/>{conversationId, content, type: "text"}

    %% Step 2: WebSocket routes to Chat Service
    WebSocket->>ChatService: Route message to Chat Service
    Note right of WebSocket: STOMP message with headers

    %% Step 3: Chat Service processes message
    ChatService->>ChatService: Validate message & user permissions
    ChatService->>ChatService: Generate message ID & timestamp
    ChatService->>MongoDB: Save message to database
    Note right of ChatService: Insert into messages collection

    %% Step 4: Update conversation metadata
    ChatService->>MongoDB: Update conversation lastMessage
    Note right of ChatService: Update conversations collection

    %% Step 5: Update user presence in Redis
    ChatService->>Redis: Update User A's last activity
    Note right of ChatService: Set user presence status

    %% Step 6: Broadcast message to conversation participants
    ChatService->>WebSocket: Broadcast to conversation topic
    Note right of ChatService: /topic/conversation.{conversationId}

    %% Step 7: WebSocket delivers to User A (sender)
    WebSocket->>ClientA: Deliver message confirmation
    Note right of WebSocket: Message status: "sent"

    %% Step 8: WebSocket delivers to User B (recipient)
    WebSocket->>ClientB: Deliver message to User B
    Note right of WebSocket: Real-time message delivery

    %% Step 9: User B receives and reads message
    ClientB->>ClientB: Display message in UI
    ClientB->>WebSocket: Send read receipt
    Note right of ClientB: /app/chat.read<br/>{messageId, conversationId}

    %% Step 10: Process read receipt
    WebSocket->>ChatService: Route read receipt
    ChatService->>MongoDB: Update message status to "seen"
    ChatService->>Redis: Update User B's last read message

    %% Step 11: Notify User A that message was read
    ChatService->>WebSocket: Send read status update
    WebSocket->>ClientA: Deliver read confirmation
    Note right of WebSocket: Message status: "seen"

    %% Step 12: Check if User B is offline (for push notifications)
    alt User B is offline
        ChatService->>NotificationService: Trigger push notification
        Note right of ChatService: Async message via RabbitMQ
        NotificationService->>NotificationService: Generate push notification
        NotificationService->>NotificationService: Send to User B's device
    end

    %% Step 13: Update conversation metadata
    ChatService->>MongoDB: Update conversation unread count
    Note right of ChatService: Increment unread count for User B

    %% Step 14: Real-time presence updates
    ChatService->>Redis: Update User A's online status
    ChatService->>WebSocket: Broadcast presence update
    WebSocket->>ClientB: Notify User B of User A's activity
    Note right of WebSocket: /topic/presence

    Note over ClientA, NotificationService: Message flow completed successfully
```

## Key Components and Data Flow

### 1. Message Sending Flow
- **Client A** sends message via WebSocket using STOMP protocol
- **API Gateway** routes WebSocket connections to Chat Service
- **Chat Service** validates, processes, and persists the message
- **MongoDB** stores the message and updates conversation metadata

### 2. Real-time Delivery
- **WebSocket Broker** handles real-time message distribution
- **Redis** manages user presence and session state
- **Clients** receive messages instantly through WebSocket connections

### 3. Message Status Tracking
- **Sent**: Message successfully sent from sender
- **Delivered**: Message received by recipient's client
- **Seen**: Recipient has read the message

### 4. Notification System
- **Notification Service** handles push notifications for offline users
- **RabbitMQ** manages asynchronous notification delivery
- **Real-time notifications** for online users via WebSocket

### 5. Data Persistence
- **MongoDB** stores all messages, conversations, and user data
- **Redis** caches presence status and session information
- **Conversation metadata** is updated in real-time

## Error Handling and Resilience

### Connection Failures
- WebSocket reconnection with exponential backoff
- Message queuing during disconnection
- Graceful degradation to polling if WebSocket fails

### Service Failures
- Circuit breaker pattern for service calls
- Message retry mechanisms
- Fallback to cached data when services are unavailable

### Data Consistency
- Eventual consistency for message delivery
- Idempotent message processing
- Conflict resolution for concurrent updates

## Performance Considerations

### Message Throughput
- Horizontal scaling of Chat Service instances
- Load balancing across multiple WebSocket brokers
- Database sharding for high-volume conversations

### Latency Optimization
- Message caching in Redis
- Connection pooling for database access
- Asynchronous processing for non-critical operations

### Resource Management
- Connection limits per user
- Message rate limiting
- Memory management for WebSocket connections
