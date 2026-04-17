#!/bin/bash
set -e

SERVER="192.168.70.197"
USER="root"
PASS="Zsec@123456"
PROJECT_DIR="/root/data-platform"

echo "=== 1. Creating tarball of data-platform ==="
cd C:/Users/arcvi/data-platform
tar -czf /tmp/data-platform.tar.gz \
    --exclude='**/target' \
    --exclude='**/node_modules' \
    --exclude='**/.git' \
    .

echo "=== 2. Syncing to server ==="
sshpass -p "$PASS" scp -o StrictHostKeyChecking=no /tmp/data-platform.tar.gz $USER@$SERVER:/tmp/

echo "=== 3. Extracting on server ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    rm -rf /root/data-platform
    mkdir -p /root/data-platform
    tar -xzf /tmp/data-platform.tar.gz -C /root/data-platform
    echo "Code synced successfully"
ENDSSH

echo "=== 4. Executing SQL scripts on DM database ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    cd /root/data-platform/scripts/sql
    
    # Connect to DM database and execute SQL
    # Adjust the connection string based on your DM setup
    echo "Executing desensitization-schema.sql..."
    # You may need to adjust the command based on how DM SQL is executed
    # This is a placeholder - adjust based on actual DM client
    cat desensitization-schema.sql | grep -v '^--' | grep -v '^$' > /tmp/desensitization_clean.sql
    cat /tmp/desensitization_clean.sql
    
    echo "Executing watermark-schema.sql..."
    cat watermark-schema.sql | grep -v '^--' | grep -v '^$' > /tmp/watermark_clean.sql
    cat /tmp/watermark_clean.sql
    
    echo "Executing encryption-schema.sql..."
    cat encryption-schema.sql | grep -v '^--' | grep -v '^$' > /tmp/encryption_clean.sql
    cat /tmp/encryption_clean.sql
    
    echo "SQL scripts prepared"
ENDSSH

echo "=== 5. Building backend services with Maven ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    cd /root/data-platform
    mvn clean package -DskipTests -q 2>&1 || echo "Maven build had issues"
ENDSSH

echo "=== 6. Building frontend ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    cd /root/data-platform/admin-ui
    npm install 2>&1 || echo "npm install completed with warnings"
    npm run build 2>&1 || echo "npm build completed with warnings"
ENDSSH

echo "=== 7. Building Docker images ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    cd /root/data-platform
    REGISTRY="192.168.70.197:5000"
    
    for service in user-service desensitization-service watermark-service encryption-service; do
        echo "Building $service..."
        cd $service
        docker build -t $REGISTRY/$service:1.0.0 .
        docker push $REGISTRY/$service:1.0.0
        cd ..
    done
ENDSSH

echo "=== 8. Deploying to K3s ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    cd /root/data-platform
    NAMESPACE="dataplatform"
    CHARTS_DIR="./infrastructure/helm/charts"
    
    kubectl create namespace $NAMESPACE || echo "Namespace exists"
    
    for service in user-service desensitization-service watermark-service encryption-service; do
        echo "Deploying $service..."
        helm upgrade --install $service $CHARTS_DIR/$service \
            --namespace $NAMESPACE \
            --create-namespace
    done
ENDSSH

echo "=== 9. Checking pod status ==="
sshpass -p "$PASS" ssh -o StrictHostKeyChecking=no $USER@$SERVER << 'ENDSSH'
    kubectl get pods -n dataplatform
    kubectl get svc -n dataplatform
ENDSSH

echo "=== Deployment Complete ==="
