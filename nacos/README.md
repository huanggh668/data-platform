# Nacos Docker Compose Configuration
# For local development and testing

## Quick Start
```bash
cd nacos
docker-compose up -d
```

## Access Nacos
- Console: http://localhost:8848/nacos
- Default credentials: nacos/nacos

## Endpoints
- Health: http://localhost:8848/nacos/v1/console/health/readiness
- Service list: http://localhost:8848/nacos/v1/ns/instance/list

## Kubernetes Deployment
For K3s deployment, use the Helm chart in infrastructure/helm/charts/nacos/
