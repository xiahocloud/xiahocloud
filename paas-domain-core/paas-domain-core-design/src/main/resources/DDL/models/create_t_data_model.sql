-- PostgreSQL 建表语句
-- 基于AbstractModel及其子类生成的数据表结构

-- 1. 数据模型表 (t_data_model)
-- 用于定义数据的基础，按照元模型配置生成
CREATE TABLE t_data_model (
    id BIGINT PRIMARY KEY,
    ukey VARCHAR(255) NOT NULL,
    tenant VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    version VARCHAR(50),
    type VARCHAR(100),
    status INTEGER DEFAULT 1,
    enable INTEGER DEFAULT 1,
    visible INTEGER DEFAULT 1,
    app VARCHAR(100) DEFAULT 'std',
    sys INTEGER DEFAULT 0,
    namespace VARCHAR(255),
    created_time BIGINT,
    created_by VARCHAR(100),
    updated_time BIGINT,
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE UNIQUE INDEX idx_data_model_ukey_tenant ON t_data_model(tenant, ukey);
CREATE INDEX idx_data_model_type ON t_data_model(type);
CREATE INDEX idx_data_model_app ON t_data_model(app);
CREATE INDEX idx_data_model_namespace ON t_data_model(namespace);
CREATE INDEX idx_data_model_status ON t_data_model(status);
CREATE INDEX idx_data_model_enable ON t_data_model(enable);
CREATE INDEX idx_data_model_visible ON t_data_model(visible);
CREATE INDEX idx_data_model_sys ON t_data_model(sys);

-- 添加注释
COMMENT ON TABLE t_data_model IS '数据模型表，用于定义数据的基础结构';
COMMENT ON COLUMN t_data_model.id IS '全局唯一标识符';
COMMENT ON COLUMN t_data_model.ukey IS '租户内可见的唯一属性';
COMMENT ON COLUMN t_data_model.tenant IS '租户编码';
COMMENT ON COLUMN t_data_model.name IS '中文名称';
COMMENT ON COLUMN t_data_model.description IS '关于属性的描述';
COMMENT ON COLUMN t_data_model.version IS '版本号';
COMMENT ON COLUMN t_data_model.type IS '描述当前模型的类型';
COMMENT ON COLUMN t_data_model.status IS '描述元素的状态';
COMMENT ON COLUMN t_data_model.enable IS '启用状态，null/1：启用，0：禁用';
COMMENT ON COLUMN t_data_model.visible IS '可见性，null/1：可见，0：不可见';
COMMENT ON COLUMN t_data_model.app IS '所属应用的标识';
COMMENT ON COLUMN t_data_model.sys IS '系统标识，null/1：系统，0：自定义';
COMMENT ON COLUMN t_data_model.namespace IS '模型或组件的命名空间';
COMMENT ON COLUMN t_data_model.created_time IS '创建时间戳';
COMMENT ON COLUMN t_data_model.created_by IS '创建者标识';
COMMENT ON COLUMN t_data_model.updated_time IS '最后更新时间戳';
COMMENT ON COLUMN t_data_model.updated_by IS '最后更新者标识';
