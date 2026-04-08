#!/bin/bash
# Deploy all microservices to K3s
set -e

REGISTRY="192.168.70.197:5000"
NAMESPACE="dataplatform"
CHARTS_DIR="./infrastructure/helm/charts"

echo "=== Deploying Data Platform to K3s ==="

kubectl create namespace $NAMESPACE || echo "Namespace $NAMESPACE already exists"

echo "Building and pushing Docker images..."

for service in user-service desensitization-service watermark-service encryption-service; do
    echo "Building $service..."
    cd $service
    docker build -t $REGISTRY/$service:1.0.0 .
    docker push $REGISTRY/$service:1.0.0
    cd ..
done

echo "Installing Helm charts..."

for service in user-service desensitization-service watermark-service encryption-service; do
    echo "Deploying $service..."
    helm upgrade --install $service $CHARTS_DIR/$service \
        --namespace $NAMESPACE \
        --create-namespace
done

echo "=== Deployment Complete ==="
echo "Checking pod status..."
kubectl get pods -n $NAMESPACE
