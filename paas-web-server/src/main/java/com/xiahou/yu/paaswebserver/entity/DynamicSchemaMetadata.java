package com.xiahou.yu.paaswebserver.entity;

import lombok.Data;
import java.util.Map;

/**
 * 动态Schema元数据
 */
@Data
public class DynamicSchemaMetadata {
    private String system;
    private String module;
    private String context;
    private String app;
    private String aggr;
    private String entity;
    private Map<String, String> fieldTypes; // fieldName -> fieldType
    private Map<String, Object> fieldConstraints; // fieldName -> constraints
    private String primaryKey;
    private Map<String, String> relationships; // fieldName -> relatedEntity
}
