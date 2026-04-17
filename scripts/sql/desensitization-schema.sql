-- Desensitization Service Schema

-- Data Source Table
CREATE TABLE desensitization_data_source (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    config JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

CREATE INDEX idx_desensitization_data_source_name ON desensitization_data_source(name);
CREATE INDEX idx_desensitization_data_source_type ON desensitization_data_source(type);
CREATE INDEX idx_desensitization_data_source_deleted ON desensitization_data_source(deleted);

-- Desensitization Rule Table
CREATE TABLE desensitization_rule (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    data_type VARCHAR(50) NOT NULL,
    pattern VARCHAR(500),
    replacement VARCHAR(500),
    priority INT DEFAULT 0,
    enabled INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

CREATE INDEX idx_desensitization_rule_name ON desensitization_rule(name);
CREATE INDEX idx_desensitization_rule_data_type ON desensitization_rule(data_type);
CREATE INDEX idx_desensitization_rule_enabled ON desensitization_rule(enabled);
CREATE INDEX idx_desensitization_rule_deleted ON desensitization_rule(deleted);

-- Desensitization Job Table
CREATE TABLE desensitization_job (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    source_id BIGINT,
    target_id BIGINT,
    rule_ids VARCHAR(500),
    status VARCHAR(20),
    progress INT DEFAULT 0,
    error_message VARCHAR(500),
    cron_expression VARCHAR(50),
    is_scheduled INT DEFAULT 0,
    execute_at TIMESTAMP,
    completed_at TIMESTAMP,
    result JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    CONSTRAINT fk_desensitization_job_source FOREIGN KEY (source_id) REFERENCES desensitization_data_source(id),
    CONSTRAINT fk_desensitization_job_target FOREIGN KEY (target_id) REFERENCES desensitization_data_source(id)
);

CREATE INDEX idx_desensitization_job_name ON desensitization_job(name);
CREATE INDEX idx_desensitization_job_source_id ON desensitization_job(source_id);
CREATE INDEX idx_desensitization_job_target_id ON desensitization_job(target_id);
CREATE INDEX idx_desensitization_job_status ON desensitization_job(status);
CREATE INDEX idx_desensitization_job_is_scheduled ON desensitization_job(is_scheduled);
CREATE INDEX idx_desensitization_job_deleted ON desensitization_job(deleted);
