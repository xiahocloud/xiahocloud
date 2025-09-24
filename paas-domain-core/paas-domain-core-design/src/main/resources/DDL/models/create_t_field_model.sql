-- PostgreSQL 建表语句
-- 字段模型表 (t_field_model)
-- 用于定义数据字段的属性和行为，按照元模型配置生成

CREATE TABLE t_field_model (
    id BIGINT PRIMARY KEY,
    ukey VARCHAR(255) NOT NULL,
    tenant VARCHAR(128) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    version VARCHAR(50),
    type VARCHAR(128),
    status INTEGER DEFAULT 1,
    enable INTEGER DEFAULT 1,
    visible INTEGER DEFAULT 1,
    app VARCHAR(128) DEFAULT 'std',
    sys INTEGER DEFAULT 0,
    namespace VARCHAR(255),
    created_time BIGINT,
    creator VARCHAR(128),
    updated_time BIGINT,
    updater VARCHAR(128),
    width VARCHAR(50)
);

-- 创建索引
CREATE UNIQUE INDEX idx_field_model_ukey_tenant ON t_field_model(tenant, ukey);
CREATE INDEX idx_field_model_type ON t_field_model(type);
CREATE INDEX idx_field_model_app ON t_field_model(app);
CREATE INDEX idx_field_model_namespace ON t_field_model(namespace);
CREATE INDEX idx_field_model_status ON t_field_model(status);
CREATE INDEX idx_field_model_enable ON t_field_model(enable);
CREATE INDEX idx_field_model_visible ON t_field_model(visible);
CREATE INDEX idx_field_model_sys ON t_field_model(sys);

-- 添加注释
COMMENT ON TABLE t_field_model IS '字段模型表，用于定义数据字段的属性和行为';
COMMENT ON COLUMN t_field_model.id IS '全局唯一标识符';
COMMENT ON COLUMN t_field_model.ukey IS '租户内可见的唯一属性';
COMMENT ON COLUMN t_field_model.tenant IS '租户编码';
COMMENT ON COLUMN t_field_model.name IS '中文名称';
COMMENT ON COLUMN t_field_model.description IS '关于属性的描述';
COMMENT ON COLUMN t_field_model.version IS '版本号';
COMMENT ON COLUMN t_field_model.type IS '描述当前模型的类型';
COMMENT ON COLUMN t_field_model.status IS '描述元素的状态';
COMMENT ON COLUMN t_field_model.enable IS '启用状态，null/1：启用，0：禁用';
COMMENT ON COLUMN t_field_model.visible IS '可见性，null/1：可见，0：不可见';
COMMENT ON COLUMN t_field_model.app IS '所属应用的标识';
COMMENT ON COLUMN t_field_model.sys IS '系统标识，null/1：系统，0：自定义';
COMMENT ON COLUMN t_field_model.namespace IS '模型或组件的命名空间';
COMMENT ON COLUMN t_field_model.created_time IS '创建时间戳';
COMMENT ON COLUMN t_field_model.creator IS '创建者标识';
COMMENT ON COLUMN t_field_model.updated_time IS '最后更新时间戳';
COMMENT ON COLUMN t_field_model.updater IS '最后更新者标识';
COMMENT ON COLUMN t_field_model.width IS '字段在界面上的显示宽度';
