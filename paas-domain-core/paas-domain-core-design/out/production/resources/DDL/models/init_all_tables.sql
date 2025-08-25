-- ============================================================================
-- PostgreSQL DDL 脚本 - 元模型相关表
-- 基于 AbstractModel.java 生成的完整数据库结构
-- ============================================================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- 开始事务
BEGIN;

-- ============================================================================
-- 1. 删除已存在的表（如果需要重新创建）
-- ============================================================================
-- DROP TABLE IF EXISTS t_field_model CASCADE;
-- DROP TABLE IF EXISTS t_page_model CASCADE;
-- DROP TABLE IF EXISTS t_data_model CASCADE;

-- ============================================================================
-- 2. 创建数据模型表 (t_data_model)
-- ============================================================================
CREATE TABLE t_data_model (
    id BIGINT PRIMARY KEY,
    ukey VARCHAR(255) NOT NULL,
    tenant VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    "description" TEXT,
    version VARCHAR(50),
    type VARCHAR(100),
    status INTEGER DEFAULT 1,
    enable INTEGER DEFAULT 1,
    visible INTEGER DEFAULT 1,
    app VARCHAR(100) DEFAULT 'std',
    sys INTEGER DEFAULT 0,
    namespace VARCHAR(255),
    created_time BIGINT,
    "created_By" VARCHAR(100),
    updated_time BIGINT,
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 3. 创建页面模型表 (t_page_model)
-- ============================================================================
CREATE TABLE t_page_model (
    id BIGINT PRIMARY KEY,
    ukey VARCHAR(255) NOT NULL,
    tenant VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    "description" TEXT,
    version VARCHAR(50),
    type VARCHAR(100),
    status INTEGER DEFAULT 1,
    enable INTEGER DEFAULT 1,
    visible INTEGER DEFAULT 1,
    app VARCHAR(100) DEFAULT 'std',
    sys INTEGER DEFAULT 0,
    namespace VARCHAR(255),
    created_time BIGINT,
    "created_By" VARCHAR(100),
    updated_time BIGINT,
    updated_by VARCHAR(100),
    page_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 4. 创建字段模型表 (t_field_model)
-- ============================================================================
CREATE TABLE t_field_model (
    id BIGINT PRIMARY KEY,
    ukey VARCHAR(255) NOT NULL,
    tenant VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    "description" TEXT,
    version VARCHAR(50),
    type VARCHAR(100),
    status INTEGER DEFAULT 1,
    enable INTEGER DEFAULT 1,
    visible INTEGER DEFAULT 1,
    app VARCHAR(100) DEFAULT 'std',
    sys INTEGER DEFAULT 0,
    namespace VARCHAR(255),
    created_time BIGINT,
    "created_By" VARCHAR(100),
    updated_time BIGINT,
    updated_by VARCHAR(100),
    width VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 5. 创建索引
-- ============================================================================

-- t_data_model 索引
CREATE UNIQUE INDEX idx_data_model_ukey_tenant ON t_data_model(ukey, tenant);
CREATE INDEX idx_data_model_type ON t_data_model(type);
CREATE INDEX idx_data_model_app ON t_data_model(app);
CREATE INDEX idx_data_model_namespace ON t_data_model(namespace);
CREATE INDEX idx_data_model_status ON t_data_model(status);
CREATE INDEX idx_data_model_enable ON t_data_model(enable);
CREATE INDEX idx_data_model_visible ON t_data_model(visible);
CREATE INDEX idx_data_model_sys ON t_data_model(sys);

-- t_page_model 索引
CREATE UNIQUE INDEX idx_page_model_ukey_tenant ON t_page_model(ukey, tenant);
CREATE INDEX idx_page_model_type ON t_page_model(type);
CREATE INDEX idx_page_model_page_type ON t_page_model(page_type);
CREATE INDEX idx_page_model_app ON t_page_model(app);
CREATE INDEX idx_page_model_namespace ON t_page_model(namespace);
CREATE INDEX idx_page_model_status ON t_page_model(status);
CREATE INDEX idx_page_model_enable ON t_page_model(enable);
CREATE INDEX idx_page_model_visible ON t_page_model(visible);
CREATE INDEX idx_page_model_sys ON t_page_model(sys);

-- t_field_model 索引
CREATE UNIQUE INDEX idx_field_model_ukey_tenant ON t_field_model(ukey, tenant);
CREATE INDEX idx_field_model_type ON t_field_model(type);
CREATE INDEX idx_field_model_app ON t_field_model(app);
CREATE INDEX idx_field_model_namespace ON t_field_model(namespace);
CREATE INDEX idx_field_model_status ON t_field_model(status);
CREATE INDEX idx_field_model_enable ON t_field_model(enable);
CREATE INDEX idx_field_model_visible ON t_field_model(visible);
CREATE INDEX idx_field_model_sys ON t_field_model(sys);

-- ============================================================================
-- 6. 添加表和字段注释
-- ============================================================================

-- t_data_model 注释
COMMENT ON TABLE t_data_model IS '数据模型表，用于定义数据的基础结构';
COMMENT ON COLUMN t_data_model.id IS '全局唯一标识符';
COMMENT ON COLUMN t_data_model.ukey IS '租户内可见的唯一属性';
COMMENT ON COLUMN t_data_model.tenant IS '租户编码';
COMMENT ON COLUMN t_data_model.name IS '中文名称';
COMMENT ON COLUMN t_data_model."description" IS '关于属性的描述';
COMMENT ON COLUMN t_data_model.version IS '版本号';
COMMENT ON COLUMN t_data_model.type IS '描述当前模型的类型';
COMMENT ON COLUMN t_data_model.status IS '描述元素的状态';
COMMENT ON COLUMN t_data_model.enable IS '启用状态，null/1：启用，0：禁用';
COMMENT ON COLUMN t_data_model.visible IS '可见性，null/1：可见，0：不可见';
COMMENT ON COLUMN t_data_model.app IS '所属应用的标识';
COMMENT ON COLUMN t_data_model.sys IS '系统标识，null/1：系统，0：自定义';
COMMENT ON COLUMN t_data_model.namespace IS '模型或组件的命名空间';
COMMENT ON COLUMN t_data_model.created_time IS '创建时间戳';
COMMENT ON COLUMN t_data_model."created_By" IS '创建者标识';
COMMENT ON COLUMN t_data_model.updated_time IS '最后更新时间戳';
COMMENT ON COLUMN t_data_model.updated_by IS '最后更新者标识';

-- t_page_model 注释
COMMENT ON TABLE t_page_model IS '页面模型表，用于定义页面的结构和布局';
COMMENT ON COLUMN t_page_model.page_type IS '页面类型，如：list, form, detail等';

-- t_field_model 注释
COMMENT ON TABLE t_field_model IS '字段模型表，用于定义数据字段的属性和行为';
COMMENT ON COLUMN t_field_model.width IS '字段在界面上的显示宽度';

-- ============================================================================
-- 7. 创建触发器函数（用于自动更新 updated_at 字段）
-- ============================================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为每个表创建触发器
CREATE TRIGGER update_t_data_model_updated_at
    BEFORE UPDATE ON t_data_model
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_t_page_model_updated_at
    BEFORE UPDATE ON t_page_model
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_t_field_model_updated_at
    BEFORE UPDATE ON t_field_model
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- 8. 插入初始数据（可选）
-- ============================================================================

-- 插入系统默认的数据模型
INSERT INTO t_data_model (
    id, ukey, tenant, name, "description", version, type, status, enable, visible,
    app, sys, namespace, created_time, "created_By", updated_time, updated_by
) VALUES
(1, 'abstract_model', 'system', '抽象模型', '系统基础抽象模型', '1.0.0', 'abstract', 1, 1, 1,
 'system', 1, 'com.xiahou.yu.paasdomincore.design.metamodel',
 extract(epoch from now()) * 1000, 'system', extract(epoch from now()) * 1000, 'system');

-- 提交事务
COMMIT;

-- 显示创建结果
\echo '数据库表创建完成！'
\echo '已创建表：t_data_model, t_page_model, t_field_model'
\echo '已创建索引和触发器'
