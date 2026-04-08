#!/bin/bash
# Verify K3s cluster status
set -e

NAMESPACE="dataplatform"
SERVER_IP="192.168.70.197"

echo "=== K3s Cluster Status ==="

echo "Nodes:"
kubectl get nodes

echo ""
echo "Pods in all namespaces:"
kubectl get pods --all-namespaces

echo ""
echo "Services in $NAMESPACE:"
kubectl get svc -n $NAMESPACE

echo ""
echo "Ingress in $NAMESPACE:"
kubectl get ingress -n $NAMESPACE

echo ""
echo "=== Nacos Service Check ==="
curl -s "http://$SERVER_IP:8848/nacos/v1/console/health/readiness" || echo "Nacos not ready"

echo ""
echo "=== Docker Registry Check ==="
curl -s "http://$SERVER_IP:5000/v2/_catalog" || echo "Registry not accessible"

echo ""
echo "=== Service Logs ==="
for svc in user-service desensitization-service watermark-service encryption-service; do
    echo "-- $svc --"
    kubectl logs -n $NAMESPACE -l app=$svc --tail=5 2>/dev/null || echo "No logs available"
done
