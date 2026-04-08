#!/bin/bash
# Build and push Docker image for desensitization-service

set -e

SERVICE_NAME="desensitization-service"
VERSION="1.0.0-SNAPSHOT"
REGISTRY="192.168.70.197:5000"
IMAGE="${REGISTRY}/${SERVICE_NAME}:${VERSION}"

echo "=== Building Docker image for ${SERVICE_NAME} ==="

cd "$(dirname "$0")"

# Build the Docker image
docker build -t ${SERVICE_NAME}:${VERSION} .

# Tag for registry
docker tag ${SERVICE_NAME}:${VERSION} ${IMAGE}

# Push to registry
docker push ${IMAGE}

echo "=== Successfully pushed ${IMAGE} ==="
