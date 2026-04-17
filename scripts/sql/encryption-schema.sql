-- Encryption Service Schema

-- Encryption Algorithm Table
CREATE TABLE encryption_algorithm (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    key_length INT,
    mode VARCHAR(50),
    padding VARCHAR(50),
    config JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

CREATE INDEX idx_encryption_algorithm_name ON encryption_algorithm(name);
CREATE INDEX idx_encryption_algorithm_type ON encryption_algorithm(type);
CREATE INDEX idx_encryption_algorithm_deleted ON encryption_algorithm(deleted);

-- Encryption Job Table
CREATE TABLE encryption_job (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    algorithm_id BIGINT,
    source_path VARCHAR(500),
    target_path VARCHAR(500),
    operation VARCHAR(20),
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
    CONSTRAINT fk_encryption_job_algorithm FOREIGN KEY (algorithm_id) REFERENCES encryption_algorithm(id)
);

CREATE INDEX idx_encryption_job_name ON encryption_job(name);
CREATE INDEX idx_encryption_job_algorithm_id ON encryption_job(algorithm_id);
CREATE INDEX idx_encryption_job_operation ON encryption_job(operation);
CREATE INDEX idx_encryption_job_status ON encryption_job(status);
CREATE INDEX idx_encryption_job_is_scheduled ON encryption_job(is_scheduled);
CREATE INDEX idx_encryption_job_deleted ON encryption_job(deleted);
