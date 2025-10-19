# Real-time Distributed Chat Application

A comprehensive, production-ready chat application built with Spring Boot microservices and React frontend, featuring real-time messaging, user management, and notification systems.

## 🏗️ Architecture Overview

This application follows a microservices architecture pattern with the following key components:

- **API Gateway**: Spring Cloud Gateway for routing and load balancing
- **User Service**: Handles authentication, user profiles, and social features
- **Chat Service**: Manages real-time messaging and conversations
- **Notification Service**: Handles push notifications and in-app alerts
- **Frontend**: React application with Material-UI for modern user interface

## 🚀 Features

### Core Features
- **Real-time Messaging**: WebSocket-based instant messaging
- **User Authentication**: JWT-based authentication with OAuth2 support
- **Profile Management**: User profiles with avatars and settings
- **Group Conversations**: Create and manage group chats
- **Message Reactions**: React to messages with emojis
- **Typing Indicators**: See when users are typing
- **Message Status**: Track sent, delivered, and seen status
- **File Attachments**: Send images, videos, and files
- **Push Notifications**: Real-time notifications for offline users

### Technical Features
- **Microservices Architecture**: Scalable and maintainable
- **Service Discovery**: Netflix Eureka for service registration
- **Load Balancing**: Automatic load distribution
- **Circuit Breaker**: Fault tolerance and resilience
- **Message Broker**: RabbitMQ for asynchronous communication
- **Caching**: Redis for performance optimization
- **Database**: MongoDB for data persistence
- **Containerization**: Docker and Kubernetes support
- **Monitoring**: Prometheus and Grafana integration

## 📁 Project Structure

```
chat-real-time-distributed/
├── docs/                           # Documentation
│   ├── architecture-diagram.md    # System architecture
│   ├── mongodb-schema.md          # Database schema
│   └── message-flow-sequence.md   # Message flow diagrams
├── gateway-service/                # API Gateway
├── user-service/                   # User management service
├── chat-service/                   # Chat and messaging service
├── notification-service/           # Notification service
├── frontend/                       # React frontend
├── k8s/                           # Kubernetes manifests
├── scripts/                       # Utility scripts
├── docker-compose.yml             # Complete Docker Compose setup with MongoDB Atlas
└── env.example                    # Environment variables template
```

## 🛠️ Technology Stack

### Backend
- **Java 17+**: Programming language
- **Spring Boot 3.2.0**: Application framework
- **Spring Cloud**: Microservices framework
- **Spring Security**: Authentication and authorization
- **Spring WebSocket**: Real-time communication
- **Spring Data MongoDB**: Database integration
- **Spring Data Redis**: Caching layer
- **RabbitMQ**: Message broker
- **JWT**: Token-based authentication
- **Maven**: Build tool

### Frontend
- **React 18+**: UI framework
- **Material-UI (MUI)**: Component library
- **Redux Toolkit**: State management
- **React Router**: Navigation
- **STOMP.js**: WebSocket client
- **SockJS**: WebSocket fallback

### Infrastructure
- **Docker**: Containerization
- **Kubernetes**: Container orchestration
- **MongoDB**: Primary database
- **Redis**: Caching and session storage
- **RabbitMQ**: Message broker
- **Nginx**: Load balancer and reverse proxy
- **Prometheus**: Metrics collection
- **Grafana**: Monitoring dashboards

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker and Docker Compose
- Maven 3.6+

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd chat-real-time-distributed
   ```

2. **Set up environment variables**
   ```bash
   cp env.example .env
   # Edit .env with your configuration
   ```

3. **Start the application with Docker Compose**
   ```bash
   # Start all services with MongoDB Atlas
   docker-compose up -d
   
   # View logs
   docker-compose logs -f
   
   # View logs for specific service
   docker-compose logs -f user-service
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - API Gateway: http://localhost:8080
   - RabbitMQ Management: http://localhost:15672
   - MongoDB Express: http://localhost:8081
   - Redis Commander: http://localhost:8082
   - Eureka Dashboard: http://localhost:8761 (only with full version)

### Manual Setup

1. **Start infrastructure services**
   ```bash
   # MongoDB
   docker run -d --name mongodb -p 27017:27017 mongo:7.0
   
   # Redis
   docker run -d --name redis -p 6379:6379 redis:7.2-alpine
   
   # RabbitMQ
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
   ```

2. **Build and run services**
   ```bash
   # Build all services
   mvn clean package -DskipTests
   
   # Run services (in separate terminals)
   java -jar gateway-service/target/gateway-service-1.0.0.jar
   java -jar user-service/target/user-service-1.0.0.jar
   java -jar chat-service/target/chat-service-1.0.0.jar
   java -jar notification-service/target/notification-service-1.0.0.jar
   ```

3. **Start frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

## 🐳 Docker Deployment

### Build Images
```bash
# Build all images
docker-compose build

# Build specific service
docker-compose build gateway-service
```

### Run with Docker Compose
```bash
# Start all services
docker-compose up -d

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and start
docker-compose up --build -d
```

## ☸️ Kubernetes Deployment

### Prerequisites
- Kubernetes cluster (v1.20+)
- kubectl configured
- Ingress controller installed

### Deploy to Kubernetes
```bash
# Apply all manifests
kubectl apply -k k8s/

# Check deployment status
kubectl get pods -n chat-app

# Access services
kubectl port-forward service/gateway-service 8080:8080 -n chat-app
kubectl port-forward service/frontend-service 3000:3000 -n chat-app
```

## 📊 Monitoring and Observability

### Metrics
- **Prometheus**: Collects metrics from all services
- **Grafana**: Provides dashboards for visualization
- **Spring Actuator**: Exposes health and metrics endpoints

### Logging
- **Structured Logging**: JSON format for better parsing
- **Log Levels**: Configurable per service
- **Centralized Logging**: Ready for ELK stack integration

### Health Checks
- **Liveness Probes**: Ensure services are running
- **Readiness Probes**: Ensure services are ready to serve traffic
- **Circuit Breakers**: Prevent cascade failures

## 🔧 Configuration

### Environment Variables
Key configuration options:

```bash
# Database
MONGODB_URI=mongodb://localhost:27017/chat
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400

# Email
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password

# Firebase (for push notifications)
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY=your-private-key
FIREBASE_CLIENT_EMAIL=your-client-email
```

### Service Configuration
Each service has its own `application.yml` with:
- Database connections
- Service discovery settings
- Security configurations
- Logging levels
- Feature flags

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific service tests
cd user-service && mvn test
```

### Integration Tests
```bash
# Run integration tests
mvn verify
```

### End-to-End Tests
```bash
# Frontend tests
cd frontend && npm test
```

## 📈 Performance Considerations

### Scalability
- **Horizontal Scaling**: All services support multiple replicas
- **Load Balancing**: Automatic traffic distribution
- **Database Sharding**: MongoDB supports horizontal scaling
- **Caching**: Redis reduces database load

### Optimization
- **Connection Pooling**: Optimized database connections
- **Message Batching**: Efficient message processing
- **Compression**: Gzip compression for API responses
- **CDN Ready**: Static assets can be served from CDN

## 🔒 Security

### Authentication & Authorization
- **JWT Tokens**: Stateless authentication
- **OAuth2 Integration**: Social login support
- **Password Hashing**: bcrypt for secure password storage
- **Session Management**: Redis-based session storage

### Data Protection
- **HTTPS/WSS**: Encrypted communication
- **Input Validation**: Comprehensive input sanitization
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Content Security Policy headers

### Network Security
- **CORS Configuration**: Controlled cross-origin access
- **Rate Limiting**: API abuse prevention
- **Firewall Rules**: Network-level protection

## 🚀 Production Deployment

### Prerequisites
- Kubernetes cluster
- Domain name and SSL certificates
- Container registry access
- Monitoring setup

### Deployment Steps
1. **Build and push images**
2. **Configure secrets and configmaps**
3. **Deploy infrastructure services**
4. **Deploy application services**
5. **Configure ingress and SSL**
6. **Set up monitoring and alerting**

### High Availability
- **Multi-AZ Deployment**: Deploy across availability zones
- **Database Replication**: MongoDB replica sets
- **Load Balancing**: Multiple ingress points
- **Backup Strategy**: Regular data backups

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in the `docs/` folder
- Review the troubleshooting guide in `k8s/README.md`

## 🗺️ Roadmap

### Upcoming Features
- [ ] Voice and video calling
- [ ] File sharing with preview
- [ ] Message encryption
- [ ] Bot integration
- [ ] Mobile applications
- [ ] Advanced analytics
- [ ] Multi-language support

### Technical Improvements
- [ ] GraphQL API
- [ ] Event sourcing
- [ ] CQRS pattern
- [ ] Advanced caching strategies
- [ ] Machine learning integration
- [ ] Performance optimization

---

**Built with ❤️ using Spring Boot, React, and modern microservices architecture.**