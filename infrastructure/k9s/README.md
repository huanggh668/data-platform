# K9s Operations Guide

## Installation

### Linux
```bash
curl -fsSL https://github.com/derailed/k9s/releases/download/v0.27.0/k9s_Linux_amd64.tar.gz | tar xz
sudo mv k9s /usr/local/bin/
```

### macOS
```bash
brew install k9s
```

### Windows
```bash
scoop install k9s
```

## Configuration

Copy kubeconfig from server:
```bash
mkdir -p ~/.kube
scp root@192.168.70.197:/etc/rancher/k3s/k3s.yaml ~/.kube/config-dataplatform
```

Set server IP in kubeconfig:
```bash
sed -i 's/127.0.0.1/192.168.70.197/g' ~/.kube/config-dataplatform
```

## Usage

### Launch K9s with specific kubeconfig
```bash
KUBECONFIG=~/.kube/config-dataplatform k9s
```

### Common Commands

| Key | Action |
|-----|--------|
| `:` | Open command prompt |
| `ctrl-a` | Show all resources |
| `ctrl-d` | Describe resource |
| `ctrl-e` | Edit resource |
| `ctrl-k` | Delete resource |
| `l` | View logs |
| `s` | Shell into container |
| `shift-f` | Port-forward |
| `esc` | Back/Cancel |

### View Specific Resources

| Command | Resource |
|---------|----------|
| `:pods` | List pods |
| `:deploys` | List deployments |
| `:svc` | List services |
| `:ing` | List ingresses |
| `:cm` | List configmaps |
| `:sec` | List secrets |

### Useful Aliases
```bash
alias k9s-dataplatform='KUBECONFIG=~/.kube/config-dataplatform k9s --namespace dataplatform'
```

## Troubleshooting

### Connection Issues
```bash
kubectl get nodes
kubectl cluster-info
```

### Pod Issues
```bash
kubectl describe pod <pod-name> -n dataplatform
kubectl logs <pod-name> -n dataplatform
```
