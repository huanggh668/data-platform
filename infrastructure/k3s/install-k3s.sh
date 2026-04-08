#!/bin/bash
# K3s Installation Script for 192.168.70.197
# Run as: curl -fsSL https://get.k3s.io | sh -

set -e

SERVER_IP="192.168.70.197"
REGISTRY_PORT="5000"

echo "=== K3s Installation Script ==="
echo "Server: $SERVER_IP"

# Update system
echo "Updating system packages..."
apt-get update -y
apt-get upgrade -y

# Install Docker if not present
if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    apt-get install -y docker.io docker-compose
    systemctl enable docker
    systemctl start docker
fi

# Install K3s
echo "Installing K3s..."
curl -sfL https://get.k3s.io | sh -

# Wait for K3s to be ready
echo "Waiting for K3s to be ready..."
sleep 10

# Create kubeconfig
echo "Creating kubeconfig..."
mkdir -p ~/.kube
cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
chmod 600 ~/.kube/config

# Configure for external access
sed -i "s/127.0.0.1/$SERVER_IP/g" ~/.kube/config

# Start Docker Registry
echo "Starting Docker Registry on port $REGISTRY_PORT..."
docker run -d \
  --name registry \
  --restart=always \
  -p $REGISTRY_PORT:5000 \
  -v registry-data:/var/lib/registry \
  registry:2

# Configure Docker to allow insecure registry
mkdir -p /etc/docker
cat > /etc/docker/daemon.json <<EOF
{
  "insecure-registries": ["$SERVER_IP:$REGISTRY_PORT"],
  "registry-mirrors": [],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
EOF

systemctl restart docker

# Verify installations
echo "Verifying installations..."
docker ps
kubectl get nodes
kubectl get pods --all-namespaces

echo "=== K3s Installation Complete ==="
echo "Kubeconfig saved to ~/.kube/config"
echo "Docker Registry: $SERVER_IP:$REGISTRY_PORT"
