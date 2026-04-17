-- User Service Schema (DamengDB compatible)

CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(200) NOT NULL,
    email       VARCHAR(100),
    phone       VARCHAR(20),
    role        VARCHAR(20)  DEFAULT 'user',
    status      INT          DEFAULT 1,
    last_login_at DATETIME,
    created_at  DATETIME,
    updated_at  DATETIME,
    deleted     INT          DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_t_user_username ON t_user(username);
CREATE INDEX IF NOT EXISTS idx_t_user_email   ON t_user(email);
CREATE INDEX IF NOT EXISTS idx_t_user_status  ON t_user(status);
CREATE INDEX IF NOT EXISTS idx_t_user_deleted ON t_user(deleted);

-- 初始管理员账号（密码: admin123，BCrypt哈希，首次启动后请修改）
INSERT INTO t_user (username, password, email, role, status, created_at, updated_at, deleted)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
    'admin@dataplatform.com',
    'admin',
    1,
    NOW(),
    NOW(),
    0
);
