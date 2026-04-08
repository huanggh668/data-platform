# Data Platform

基于 SpringCloud Alibaba 的数据中台，包含用户服务、脱敏服务、水印服务、加密服务，支持 K3s 部署和 K9s 运维管理。

## 技术栈

### 后端
- **SpringCloud Alibaba 2021.0.5.0**
- **SpringBoot 2.7.18**
- **Nacos 2.2.3** - 注册中心与配置中心
- **MyBatis-Plus 3.5.3.1** - ORM 框架
- **JWT** - 无状态认证
- **达梦数据库** - 国产数据库

### 前端
- **Vue 3** - 前端框架
- **Element Plus** - UI 组件库
- **Vite** - 构建工具
- **Pinia** - 状态管理
- **Axios** - HTTP 客户端

### 基础设施
- **Docker** - 容器化
- **Docker Compose** - 本地开发编排
- **K3s** - Kubernetes 轻量级发行版
- **K9s** - Kubernetes CLI 工具
- **Nginx** - 反向代理

## 项目结构

```
data-platform/
├── data-platform-common/      # 公共模块
├── user-service/              # 用户服务
├── desensitization-service/   # 脱敏服务
├── watermark-service/         # 水印服务
├── encryption-service/        # 加密服务
├── admin-ui/                 # Vue3 管理后台
├── infrastructure/
│   ├── nginx/                # Nginx 配置
│   ├── k3s/                  # K3s 部署脚本
│   └── helm/                 # Helm Charts
├── nacos/                    # Nacos 配置
├── scripts/                  # 脚本
├── docker-compose.yml         # Docker Compose 编排
└── DEPLOYMENT.md             # 部署文档
```

## 服务说明

| 服务 | 端口 | 说明 |
|------|------|------|
| user-service | 8080 | 用户注册、登录、JWT 认证 |
| desensitization-service | 8081 | 敏感数据脱敏（手机号、身份证、银行卡等） |
| watermark-service | 8082 | 数据水印嵌入、提取、验证 |
| encryption-service | 8083 | AES/RSA/SM4 加密 |
| nacos | 8848 | 注册中心、配置中心 |
| nginx | 80/443 | 反向代理、前端托管 |

## 快速开始

### 1. 本地开发

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 2. 访问地址

- 管理后台: http://localhost
- Nacos 控制台: http://localhost:8848/nacos (nacos/nacos)
- API 端点: http://localhost/api/*

### 3. 前端开发

```bash
cd admin-ui
npm install
npm run dev
```

### 4. 后端构建

```bash
# 构建所有微服务
mvn clean package -DskipTests

# 构建 Docker 镜像
docker build -t 192.168.70.197:5000/<service>:1.0.0 ./<service>
```

## K3s 部署

```bash
# 在 192.168.70.197 服务器上安装 K3s
curl -fsSL https://get.k3s.io | sh -

# 部署所有服务
./infrastructure/deploy.sh
```

## API 文档

### 用户服务
- `POST /api/v1/users/register` - 用户注册
- `POST /api/v1/users/login` - 用户登录
- `GET /api/v1/users/profile` - 获取用户信息
- `PUT /api/v1/users/profile` - 更新用户信息
- `POST /api/v1/users/password` - 修改密码

### 脱敏服务
- `POST /api/v1/desensitize` - 单字段脱敏
- `POST /api/v1/desensitize/auto` - 自动识别脱敏
- `POST /api/v1/desensitize/batch` - 批量脱敏

### 水印服务
- `POST /api/v1/watermark/embed/text` - 嵌入文本水印
- `POST /api/v1/watermark/embed/image` - 嵌入图像水印
- `POST /api/v1/watermark/extract` - 提取水印
- `POST /api/v1/watermark/verify` - 验证水印

### 加密服务
- `POST /api/v1/encrypt` - 加密
- `POST /api/v1/decrypt` - 解密
- `POST /api/v1/sign` - 签名
- `POST /api/v1/verify` - 验签
- `POST /api/v1/keys/generate` - 生成密钥

## 数据库配置

使用达梦数据库，连接信息：
- 地址: 192.168.70.197:5236
- 用户: SYSDBA
- 密码: vLkmcChDJpu0QM8sOKz8

## License

MIT
