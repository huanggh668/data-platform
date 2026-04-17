#!/bin/bash
# ============================================================
# data-platform 一键部署脚本
# 用法：bash deploy.sh [build|up|down|restart|logs]
# ============================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_ROOT"

ACTION=${1:-up}

build_backend() {
  echo "==> 构建后端服务 JAR..."
  cd "$PROJECT_ROOT"
  mvn clean package -DskipTests -q
  echo "==> JAR 构建完成"
}

build_frontend() {
  echo "==> 构建前端静态资源..."
  cd "$PROJECT_ROOT/admin-ui"
  npm install --silent
  npm run build
  echo "==> 前端构建完成"
}

case "$ACTION" in
  build)
    build_backend
    build_frontend
    docker compose build --no-cache
    echo "==> 镜像构建完成"
    ;;
  up)
    build_backend
    build_frontend
    docker compose up -d --build
    echo "==> 服务启动完成，访问 http://localhost"
    ;;
  down)
    docker compose down
    echo "==> 服务已停止"
    ;;
  restart)
    docker compose restart
    echo "==> 服务已重启"
    ;;
  logs)
    docker compose logs -f --tail=100
    ;;
  *)
    echo "用法: $0 [build|up|down|restart|logs]"
    exit 1
    ;;
esac
