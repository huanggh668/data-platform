## Context

当前 data-platform 项目提供基础的脱敏、水印、加密功能，但缺少完善的管理能力。用户需要：
- 管理多个数据源（配置源和目标）
- 管理脱敏/水印/加密规则
- 创建和执行批量作业
- 追溯数据处理历史

项目使用 SpringCloud Alibaba 微服务架构，前端使用 Vue 3 + Element Plus。

## Goals / Non-Goals

**Goals:**
- 为三个核心服务（脱敏、水印、加密）补充完整的管理功能
- 支持配置化管理（数据源、规则、算法）
- 支持作业化执行（脱敏作业、水印作业、加密作业）
- 支持数据溯源（追踪数据处理历史）

**Non-Goals:**
- 不实现数据源的连接测试功能
- 不实现数据源的元数据同步

## Decisions

### 1. 数据库设计

每个服务独立管理自己的数据，使用统一的表命名规范：
- 配置表：`{service}_{entity}`（如 `desensitization_data_source`）
- 作业表：`{service}_job`

使用 MyBatis-Plus 的逻辑删除（`@TableLogic`）和自动填充（`@TableField(fill = FieldFill.INSERT)`）。

**脱敏服务表结构：**
```
desensitization_data_source(id, name, type, config JSON, created_at, updated_at, deleted)
desensitization_rule(id, name, data_type, pattern, replacement, priority, enabled, created_at, updated_at, deleted)
desensitization_job(id, name, source_id, target_id, rule_ids, status, cron_expression, is_scheduled, execute_at, completed_at, result, created_at, updated_at, deleted)
```

**水印服务表结构：**
```
watermark_factor(id, name, type, value, created_at, updated_at, deleted)
watermark_job(id, name, type, factor_ids, source_path, target_path, status, cron_expression, is_scheduled, execute_at, completed_at, result, created_at, updated_at, deleted)
watermark_trace_record(id, job_id, factor_id, embedded_data, created_at, updated_at, deleted)
```

**加密服务表结构：**
```
encryption_algorithm(id, name, type, key_length, mode, padding, config JSON, created_at, updated_at, deleted)
encryption_job(id, name, algorithm_id, source_path, target_path, operation, status, cron_expression, is_scheduled, execute_at, completed_at, result, created_at, updated_at, deleted)
```

### 2. 后端模块结构

遵循现有项目模式，每个服务新增管理模块：

```
{service}/
├── controller/
│   └── {entity}Controller.java      # 管理接口
├── service/
│   └── {entity}Service.java         # 管理服务
├── mapper/
│   └── {entity}Mapper.java          # 数据访问
├── entity/
│   └── {Entity}.java               # 数据库实体
└── dto/
    ├── {Entity}Request.java        # 创建/更新请求
    └── {Entity}Response.java       # 响应DTO
```

使用 `@RequiredArgsConstructor` 构造器注入，继承 `BaseEntity` 提供公共字段。

### 3. 前端页面结构

```
admin-ui/src/views/
├── desensitization/
│   ├── DataSource.vue              # 数据源管理
│   ├── RuleManagement.vue          # 规则管理
│   └── JobManagement.vue           # 作业管理
├── watermark/
│   ├── FactorManagement.vue         # 水印因子管理
│   ├── JobManagement.vue            # 水印作业管理
│   └── Traceability.vue            # 溯源管理
└── encryption/
    ├── AlgorithmManagement.vue      # 算法管理
    └── JobManagement.vue            # 加密作业管理
```

API 调用封装在 `admin-ui/src/api/` 目录下：
```
admin-ui/src/api/
├── desensitization.js               # 脱敏管理 API
├── watermark.js                    # 水印管理 API
└── encryption.js                   # 加密管理 API
```

### 4. API 设计规范

管理 API 遵循 RESTful 规范：

| 操作 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 列表 | GET | /api/v1/{entity}s | 分页查询 |
| 详情 | GET | /api/v1/{entity}s/{id} | 获取单个 |
| 创建 | POST | /api/v1/{entity}s | 创建 |
| 更新 | PUT | /api/v1/{entity}s/{id} | 更新 |
| 删除 | DELETE | /api/v1/{entity}s/{id} | 删除（逻辑） |
| 执行 | POST | /api/v1/{entity}s/{id}/execute | 作业专用 |

### 5. 作业执行方式

**异步执行**：作业接口立即返回 HTTP 200，实际处理在后台线程/线程池中进行。

**定时调度**：支持 Cron 表达式配置定时执行。
- `is_scheduled` 字段：是否启用定时执行
- `cron_expression` 字段：标准 Cron 表达式（如 `0 0 2 * * ?` 表示每天凌晨2点）

### 6. 作业状态机

```
PENDING -> RUNNING -> COMPLETED
                \-> FAILED
```

作业创建后状态为 PENDING，执行后变为 RUNNING，完成后变为 COMPLETED 或 FAILED。

## Risks / Trade-offs

[风险] 大量新增表可能需要数据库迁移
→ 缓解：提供 SQL 初始化脚本，包含建表语句

[风险] 前端页面较多，可能影响开发效率
→ 缓解：页面结构相似，可复用模板代码

[风险] 作业执行可能耗时较长，需要异步处理
→ 缓解：作业接口立即返回，实际执行异步进行

[风险] 定时调度需要调度器支持
→ 缓解：使用 Spring @Scheduled 注解，支持 Cron 表达式

## Open Questions

~~1. 作业执行方式：同步还是异步？~~ → **异步执行，作业接口立即返回，后台执行**
~~2. 数据源配置格式：JSON 还是分开存储字段？~~ → **JSON 格式存储，灵活配置**
~~3. 作业调度：是否需要定时执行功能？~~ → **需要定时执行功能，支持 Cron 表达式**
