-- 已有数据库升级脚本（DamengDB）
-- 如果数据库已初始化，执行此脚本补充新增字段
-- 执行前确认已连接至正确数据库实例

-- 1. t_user 表：新增 role 字段
ALTER TABLE t_user ADD COLUMN role VARCHAR(20) DEFAULT 'user';
-- 将现有用户设为普通用户，若表中已有 username='admin' 则设为管理员
UPDATE t_user SET role = 'user' WHERE role IS NULL;
-- UPDATE t_user SET role = 'admin' WHERE username = 'admin';

-- 2. desensitization_job 表：新增进度字段
ALTER TABLE desensitization_job ADD COLUMN progress INT DEFAULT 0;
ALTER TABLE desensitization_job ADD COLUMN error_message VARCHAR(500);

-- 3. watermark_job 表：新增进度字段
ALTER TABLE watermark_job ADD COLUMN progress INT DEFAULT 0;
ALTER TABLE watermark_job ADD COLUMN error_message VARCHAR(500);

-- 4. encryption_job 表：新增进度字段
ALTER TABLE encryption_job ADD COLUMN progress INT DEFAULT 0;
ALTER TABLE encryption_job ADD COLUMN error_message VARCHAR(500);
