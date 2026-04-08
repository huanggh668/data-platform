# K3s Setup Scripts
# Target Server: 192.168.70.197
# User: root / Zsec@123456

## Prerequisites
- Fresh Ubuntu 20.04+ or Debian system
- Root access
- At least 2GB RAM and 2 CPU cores
- 20GB disk space

## Usage

### Local development (Docker Compose)
```bash
cd ../nacos
docker-compose up -d
```

### Production (K3s on remote server)
```bash
# Copy script to server
scp install-k3s.sh root@192.168.70.197:/tmp/

# SSH to server and run
ssh root@192.168.70.197
chmod +x /tmp/install-k3s.sh
/tmp/install-k3s.sh
```

## Post-Installation

### Verify K3s
```bash
kubectl get nodes
kubectl get pods --all-namespaces
```

### Verify Docker Registry
```bash
docker ps
curl http://localhost:5000/v2/_catalog
```

### Copy kubeconfig to local workstation
```bash
scp root@192.168.70.197:~/.kube/config ~/.kube/config-197
# Edit and set server IP to 192.168.70.197
```
