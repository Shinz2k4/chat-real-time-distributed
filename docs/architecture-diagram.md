# System Architecture Diagram

## C4 Model - Container Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        WebApp[React Web App<br/>Material-UI + Redux Toolkit]
        MobileApp[Mobile App<br/>Future Extension]
    end

    subgraph "API Gateway Layer"
        Gateway[Spring Cloud Gateway<br/>Route Management & Load Balancing]
    end

    subgraph "Service Discovery"
        Eureka[Netflix Eureka<br/>Service Registry]
    end

    subgraph "Microservices Layer"
        UserService[User Service<br/>Spring Boot<br/>Authentication & Profile Management]
        ChatService[Chat Service<br/>Spring Boot<br/>Real-time Messaging]
        NotificationService[Notification Service<br/>Spring Boot<br/>Push Notifications]
    end

    subgraph "Message Broker"
        RabbitMQ[RabbitMQ<br/>Asynchronous Communication]
    end

    subgraph "WebSocket Layer"
        WebSocketBroker[STOMP WebSocket Broker<br/>Real-time Communication]
    end

    subgraph "Data Layer"
        MongoDB[(MongoDB<br/>Primary Database<br/>Users, Messages, Conversations)]
        Redis[(Redis<br/>Cache & Presence Status)]
    end

    subgraph "Infrastructure"
        Docker[Docker Containers]
        K8s[Kubernetes<br/>Production Orchestration]
    end

    %% Client connections
    WebApp -->|HTTPS/WSS| Gateway
    MobileApp -->|HTTPS/WSS| Gateway

    %% Gateway routing
    Gateway -->|/api/users/**| UserService
    Gateway -->|/api/chat/**| ChatService
    Gateway -->|/api/notifications/**| NotificationService
    Gateway -->|/ws/**| WebSocketBroker

    %% Service discovery
    UserService --> Eureka
    ChatService --> Eureka
    NotificationService --> Eureka
    Gateway --> Eureka

    %% Inter-service communication
    UserService -->|Async Events| RabbitMQ
    ChatService -->|Async Events| RabbitMQ
    NotificationService -->|Async Events| RabbitMQ

    %% WebSocket connections
    WebApp -->|STOMP over WSS| WebSocketBroker
    ChatService --> WebSocketBroker

    %% Database connections
    UserService --> MongoDB
    UserService --> Redis
    ChatService --> MongoDB
    ChatService --> Redis
    NotificationService --> MongoDB

    %% Infrastructure
    UserService --> Docker
    ChatService --> Docker
    NotificationService --> Docker
    Gateway --> Docker
    RabbitMQ --> Docker
    MongoDB --> Docker
    Redis --> Docker

    Docker --> K8s

    %% Styling
    classDef clientLayer fill:#e1f5fe
    classDef gatewayLayer fill:#f3e5f5
    classDef serviceLayer fill:#e8f5e8
    classDef dataLayer fill:#fff3e0
    classDef infraLayer fill:#fce4ec

    class WebApp,MobileApp clientLayer
    class Gateway gatewayLayer
    class UserService,ChatService,NotificationService,Eureka serviceLayer
    class MongoDB,Redis,RabbitMQ,WebSocketBroker dataLayer
    class Docker,K8s infraLayer
```

## Key Components Description

### Client Layer
- **React Web App**: Frontend application using Material-UI for UI components and Redux Toolkit for state management
- **Mobile App**: Future mobile application (placeholder for extensibility)

### API Gateway Layer
- **Spring Cloud Gateway**: Central entry point handling routing, load balancing, and cross-cutting concerns

### Microservices Layer
- **User Service**: Handles authentication, user profiles, and social features
- **Chat Service**: Manages real-time messaging and conversation persistence
- **Notification Service**: Handles push notifications and in-app alerts

### Data Layer
- **MongoDB**: Primary database for persistent data storage
- **Redis**: Caching layer and real-time presence status management
- **RabbitMQ**: Message broker for asynchronous inter-service communication
- **STOMP WebSocket Broker**: Real-time bidirectional communication

### Infrastructure
- **Docker**: Containerization for all services
- **Kubernetes**: Production orchestration and scaling
