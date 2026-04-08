# Data Platform Deployment Guide

## Prerequisites
- Server: 192.168.70.197 (root/Zsec@123456)
- Local: Docker, kubectl, Helm, K9s installed

## Deployment Steps

### 1. Infrastructure Setup
```bash
# SSH to server
ssh root@192.168.70.197

# Run K3s installation
curl -fsSL https://get.k3s.io | sh -

# Exit server
exit

# Copy kubeconfig
scp root@192.168.70.197:/etc/rancher/k3s/k3s.yaml ~/.kube/config
sed -i 's/127.0.0.1/192.168.70.197/g' ~/.kube/config
```

### 2. Build Microservices
```bash
cd C:/Users/arcvi/data-platform

# Run Docker build script
./scripts/build-images.sh
```

### 3. Deploy to K3s
```bash
# Deploy all services
./infrastructure/deploy.sh

# Or manually deploy Nacos first
helm upgrade --install nacos ./infrastructure/helm/charts/nacos --namespace dataplatform

# Then deploy services
for svc in user-service desensitization-service watermark-service encryption-service; do
    helm upgrade --install $svc ./infrastructure/helm/charts/$svc --namespace dataplatform
done
```

### 4. Verify Deployment
```bash
./infrastructure/k3s/verify.sh
```

### 5. Access Services
- Nacos Console: http://192.168.70.197:8848/nacos (nacos/nacos)
- User Service: http://192.168.70.197/api/v1/users/*
- Desensitization Service: http://192.168.70.197/api/v1/desensitize/*
- Watermark Service: http://192.168.70.197/api/v1/watermark/*
- Encryption Service: http://192.168.70.197/api/v1/*

### 6. K9s Operations
```bash
# Launch K9s
k9s

# Useful commands in K9s
:pods                    # List pods
:deploys                 # List deployments
:svc                     # List services
l                        # View logs
s                        # Shell into container
```

## Troubleshooting

### Pods not starting
```bash
kubectl describe pod <pod-name> -n dataplatform
kubectl logs <pod-name> -n dataplatform
```

### Image pull error
```bash
# Check if image exists in registry
curl http://192.168.70.197:5000/v2/_catalog

# Manually pull image on nodes
docker pull 192.168.70.197:5000/<service>:1.0.0
```

### Service not reachable
```bash
# Check endpoints
kubectl get endpoints -n dataplatform

# Check ingress
kubectl describe ingress -n dataplatform
```
