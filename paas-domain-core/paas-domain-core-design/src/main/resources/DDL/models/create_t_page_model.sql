-- PostgreSQL 建表语句
-- 页面模型表 (t_page_model)
-- 用于定义页面的结构和布局

CREATE TABLE t_page_model (
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
    page_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE UNIQUE INDEX idx_page_model_ukey_tenant ON t_page_model(tenant, ukey);
CREATE INDEX idx_page_model_type ON t_page_model(type);
CREATE INDEX idx_page_model_page_type ON t_page_model(page_type);
CREATE INDEX idx_page_model_app ON t_page_model(app);
CREATE INDEX idx_page_model_namespace ON t_page_model(namespace);
CREATE INDEX idx_page_model_status ON t_page_model(status);
CREATE INDEX idx_page_model_enable ON t_page_model(enable);
CREATE INDEX idx_page_model_visible ON t_page_model(visible);
CREATE INDEX idx_page_model_sys ON t_page_model(sys);

-- 添加注释
COMMENT ON TABLE t_page_model IS '页面模型表，用于定义页面的结构和布局';
COMMENT ON COLUMN t_page_model.id IS '全局唯一标识符';
COMMENT ON COLUMN t_page_model.ukey IS '租户内可见的唯一属性';
COMMENT ON COLUMN t_page_model.tenant IS '租户编码';
COMMENT ON COLUMN t_page_model.name IS '中文名称';
COMMENT ON COLUMN t_page_model.description IS '关于属性的描述';
COMMENT ON COLUMN t_page_model.version IS '版本号';
COMMENT ON COLUMN t_page_model.type IS '描述当前模型的类型';
COMMENT ON COLUMN t_page_model.status IS '描述元素的状态';
COMMENT ON COLUMN t_page_model.enable IS '启用状态，null/1：启用，0：禁用';
COMMENT ON COLUMN t_page_model.visible IS '可见性，null/1：可见，0：不可见';
COMMENT ON COLUMN t_page_model.app IS '所属应用的标识';
COMMENT ON COLUMN t_page_model.sys IS '系统标识，null/1：系统，0：自定义';
COMMENT ON COLUMN t_page_model.namespace IS '模型或组件的命名空间';
COMMENT ON COLUMN t_page_model.created_time IS '创建时间戳';
COMMENT ON COLUMN t_page_model.created_by IS '创建者标识';
COMMENT ON COLUMN t_page_model.updated_time IS '最后更新时间戳';
COMMENT ON COLUMN t_page_model.updated_by IS '最后更新者标识';
COMMENT ON COLUMN t_page_model.page_type IS '页面类型，如：list, form, detail等';
