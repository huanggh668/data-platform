-- Watermark Service Schema

-- Watermark Factor Table
CREATE TABLE watermark_factor (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    value VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

CREATE INDEX idx_watermark_factor_name ON watermark_factor(name);
CREATE INDEX idx_watermark_factor_type ON watermark_factor(type);
CREATE INDEX idx_watermark_factor_deleted ON watermark_factor(deleted);

-- Watermark Job Table
CREATE TABLE watermark_job (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    factor_ids VARCHAR(500),
    source_path VARCHAR(500),
    target_path VARCHAR(500),
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
    deleted INT DEFAULT 0
);

CREATE INDEX idx_watermark_job_name ON watermark_job(name);
CREATE INDEX idx_watermark_job_type ON watermark_job(type);
CREATE INDEX idx_watermark_job_status ON watermark_job(status);
CREATE INDEX idx_watermark_job_is_scheduled ON watermark_job(is_scheduled);
CREATE INDEX idx_watermark_job_deleted ON watermark_job(deleted);

-- Watermark Trace Record Table
CREATE TABLE watermark_trace_record (
    id BIGINT PRIMARY KEY,
    job_id BIGINT,
    factor_id BIGINT,
    embedded_data VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    CONSTRAINT fk_watermark_trace_record_job FOREIGN KEY (job_id) REFERENCES watermark_job(id),
    CONSTRAINT fk_watermark_trace_record_factor FOREIGN KEY (factor_id) REFERENCES watermark_factor(id)
);

CREATE INDEX idx_watermark_trace_record_job_id ON watermark_trace_record(job_id);
CREATE INDEX idx_watermark_trace_record_factor_id ON watermark_trace_record(factor_id);
CREATE INDEX idx_watermark_trace_record_deleted ON watermark_trace_record(deleted);
