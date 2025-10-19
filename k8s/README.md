# Kubernetes Deployment Guide

This directory contains Kubernetes manifests for deploying the chat application to a production environment.

## Prerequisites

- Kubernetes cluster (v1.20+)
- kubectl configured to access your cluster
- Docker images built and pushed to a container registry
- Ingress controller (e.g., NGINX Ingress Controller)
- cert-manager for SSL certificates (optional)

## Deployment Steps

### 1. Create Namespace and Secrets

```bash
# Apply namespace
kubectl apply -f namespace.yaml

# Create secrets (update values as needed)
kubectl apply -f secrets.yaml

# Apply configmap
kubectl apply -f configmap.yaml
```

### 2. Deploy Infrastructure Services

```bash
# Deploy MongoDB
kubectl apply -f mongodb.yaml

# Deploy Redis
kubectl apply -f redis.yaml

# Deploy RabbitMQ
kubectl apply -f rabbitmq.yaml

# Deploy Eureka
kubectl apply -f eureka.yaml
```

### 3. Deploy Application Services

```bash
# Deploy Gateway
kubectl apply -f gateway.yaml

# Deploy User Service
kubectl apply -f user-service.yaml

# Deploy Chat Service
kubectl apply -f chat-service.yaml

# Deploy Notification Service
kubectl apply -f notification-service.yaml

# Deploy Frontend
kubectl apply -f frontend.yaml
```

### 4. Deploy Ingress and Monitoring

```bash
# Deploy Ingress
kubectl apply -f ingress.yaml

# Deploy Monitoring (optional)
kubectl apply -f monitoring.yaml
```

### 5. Verify Deployment

```bash
# Check all pods are running
kubectl get pods -n chat-app

# Check services
kubectl get services -n chat-app

# Check ingress
kubectl get ingress -n chat-app
```

## Using Kustomize

You can also deploy everything at once using Kustomize:

```bash
# Deploy all resources
kubectl apply -k .

# Delete all resources
kubectl delete -k .
```

## Configuration

### Environment Variables

Update the `configmap.yaml` and `secrets.yaml` files with your specific configuration:

- Database credentials
- Redis password
- RabbitMQ credentials
- JWT secret
- Email configuration
- Firebase configuration

### Ingress Configuration

Update the `ingress.yaml` file with your domain names:

- Replace `yourdomain.com` with your actual domain
- Update SSL certificate configuration
- Configure load balancing rules

### Resource Limits

Adjust resource requests and limits in each deployment file based on your cluster capacity and expected load.

## Monitoring

The deployment includes Prometheus and Grafana for monitoring:

- Prometheus: Collects metrics from all services
- Grafana: Provides dashboards for visualization
- ServiceMonitor: Automatically discovers services to monitor

## Scaling

The deployment includes HorizontalPodAutoscaler (HPA) for automatic scaling:

- Gateway: 2-10 replicas
- User Service: 2-8 replicas
- Chat Service: 3-15 replicas
- Notification Service: 2-8 replicas
- Frontend: 2-6 replicas

## Security

- All secrets are stored in Kubernetes secrets
- Services use ClusterIP for internal communication
- Ingress provides SSL termination
- Resource limits prevent resource exhaustion

## Troubleshooting

### Check Pod Status

```bash
kubectl get pods -n chat-app
kubectl describe pod <pod-name> -n chat-app
kubectl logs <pod-name> -n chat-app
```

### Check Service Status

```bash
kubectl get services -n chat-app
kubectl describe service <service-name> -n chat-app
```

### Check Ingress Status

```bash
kubectl get ingress -n chat-app
kubectl describe ingress chat-ingress -n chat-app
```

### Port Forward for Local Testing

```bash
# Gateway
kubectl port-forward service/gateway-service 8080:8080 -n chat-app

# Frontend
kubectl port-forward service/frontend-service 3000:3000 -n chat-app
```

## Production Considerations

1. **High Availability**: Deploy across multiple availability zones
2. **Backup**: Set up regular backups for MongoDB and Redis
3. **Monitoring**: Configure alerting for critical metrics
4. **Security**: Use network policies and RBAC
5. **Updates**: Use rolling updates for zero-downtime deployments
6. **Logging**: Centralize logs using ELK stack or similar
7. **SSL**: Use cert-manager for automatic SSL certificate management
