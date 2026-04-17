-- DamengDB initialization SQL for data-platform services
-- Run this against SYSDBA schema at 192.168.70.197:5236

-- ============ DESENSITIZATION SERVICE ============

CREATE TABLE IF NOT EXISTS DESENSITIZATION_RULE (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    data_type VARCHAR(100),
    pattern VARCHAR(500),
    replacement VARCHAR(500),
    priority INT DEFAULT 0,
    enabled TINYINT DEFAULT 1,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS DESENSITIZATION_JOB (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200),
    source_id BIGINT,
    target_id BIGINT,
    rule_ids VARCHAR(500),
    status VARCHAR(50),
    progress INT DEFAULT 0,
    error_message VARCHAR(500),
    cron_expression VARCHAR(100),
    is_scheduled TINYINT DEFAULT 0,
    execute_at TIMESTAMP,
    completed_at TIMESTAMP,
    result CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS DESENSITIZATION_LOG (
    id BIGINT PRIMARY KEY,
    data_type VARCHAR(100),
    original_value VARCHAR(1000),
    masked_value VARCHAR(1000),
    operation_type VARCHAR(100),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS DATA_SOURCE (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(100),
    config CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- ============ WATERMARK SERVICE ============

CREATE TABLE IF NOT EXISTS watermark_factor (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(100),
    value VARCHAR(1000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS watermark_job (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200),
    type VARCHAR(100),
    factor_ids VARCHAR(500),
    source_path VARCHAR(500),
    target_path VARCHAR(500),
    status VARCHAR(50),
    progress INT DEFAULT 0,
    error_message VARCHAR(500),
    cron_expression VARCHAR(100),
    is_scheduled TINYINT DEFAULT 0,
    execute_at TIMESTAMP,
    completed_at TIMESTAMP,
    result CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS watermark_trace_record (
    id BIGINT PRIMARY KEY,
    job_id BIGINT,
    factor_id BIGINT,
    embedded_data CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS WATERMARK_RECORD (
    id BIGINT PRIMARY KEY,
    file_name VARCHAR(500),
    file_type VARCHAR(100),
    watermark_content VARCHAR(1000),
    watermark_position VARCHAR(200),
    embedded_at TIMESTAMP,
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- ============ ENCRYPTION SERVICE ============

CREATE TABLE IF NOT EXISTS encryption_algorithm (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(100),
    key_length INT,
    mode VARCHAR(50),
    padding VARCHAR(50),
    config CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS encryption_job (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200),
    algorithm_id BIGINT,
    source_path VARCHAR(500),
    target_path VARCHAR(500),
    operation VARCHAR(100),
    status VARCHAR(50),
    progress INT DEFAULT 0,
    error_message VARCHAR(500),
    cron_expression VARCHAR(100),
    is_scheduled TINYINT DEFAULT 0,
    execute_at TIMESTAMP,
    completed_at TIMESTAMP,
    result CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ENCRYPTION_LOG (
    id BIGINT PRIMARY KEY,
    algorithm VARCHAR(100),
    operation VARCHAR(100),
    original_data CLOB,
    encrypted_data CLOB,
    key_id VARCHAR(200),
    created_at TIMESTAMP,
    status VARCHAR(50)
);

-- ============ USER SERVICE ============

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(300) NOT NULL,
    email VARCHAR(200),
    phone VARCHAR(50),
    role VARCHAR(50) DEFAULT 'user',
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
