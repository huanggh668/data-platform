#!/bin/bash
# Build and push all Docker images
set -e

REGISTRY="192.168.70.197:5000"
VERSION="1.0.0"
PROJECT_DIR="C:/Users/arcvi/data-platform"

echo "=== Building Docker Images ==="

cd "$PROJECT_DIR"

services=("user-service" "desensitization-service" "watermark-service" "encryption-service")

for service in "${services[@]}"; do
    echo "Building $service..."
    cd "$service"
    
    docker build -t "$REGISTRY/$service:$VERSION" .
    
    echo "Pushing $service..."
    docker push "$REGISTRY/$service:$VERSION"
    
    cd ..
done

echo "=== Build Complete ==="
echo "Images pushed to $REGISTRY"
curl -s "http://$REGISTRY/v2/_catalog"
