## Why

当前 data-platform 项目仅提供基础的数据处理能力（脱敏、水印、加密），缺少完善的管理功能。用户无法通过界面管理数据源、配置规则、执行作业等，需要补充完整的管理链路以支持生产环境使用。

## What Changes

### 脱敏服务增强
- **数据源管理**：支持配置脱敏数据源（源数据库/文件）和目标位置
- **脱敏规则管理**：支持 CRUD 操作的脱敏规则配置界面
- **脱敏作业**：支持创建、执行、监控脱敏作业

### 水印服务增强
- **水印因子管理**：管理水印因子配置（用户ID、时间戳、业务标识等）
- **水印作业**：支持创建水印嵌入/提取作业并跟踪执行状态
- **溯源管理**：记录水印嵌入历史，支持数据泄露时追溯

### 加密服务增强
- **加密算法管理**：管理加密算法配置（AES/RSA/SM4等）
- **加密作业**：支持创建加密/解密作业并跟踪执行状态

## Capabilities

### New Capabilities

- `desensitization-data-source`: 数据源管理 - 管理脱敏的源数据和目标位置配置
- `desensitization-rule-management`: 脱敏规则管理 - CRUD 脱敏规则，支持正则匹配和替换策略
- `desensitization-job`: 脱敏作业管理 - 创建、执行、监控批量脱敏作业
- `watermark-factor`: 水印因子管理 - 配置水印嵌入因子（用户ID、时间戳、业务代码等）
- `watermark-job`: 水印作业管理 - 创建水印嵌入/提取作业，支持定时执行
- `watermark-traceability`: 水印溯源管理 - 记录水印历史，支持泄露追溯
- `encryption-algorithm`: 加密算法管理 - 配置和管理加密算法参数
- `encryption-job`: 加密作业管理 - 创建加密/解密作业，执行状态跟踪

### Modified Capabilities

无 - 现有能力均为接口调用，无需修改现有规格

## Impact

### 后端影响
- 新增 3 个微服务的完整管理模块（Controller、Service、Mapper、Entity）
- 需要新增数据库表存储配置和作业状态
- 需要新增管理 API 端点

### 前端影响
- admin-ui 新增 6 个管理页面
- 新增管理 API 调用封装

### 数据库影响
- 脱敏服务新增表：data_source, desensitization_rule, desensitization_job
- 水印服务新增表：watermark_factor, watermark_job, watermark_trace_record
- 加密服务新增表：encryption_algorithm, encryption_job

### 基础设施影响
- 无
