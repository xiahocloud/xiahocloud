package com.xiahou.yu.paaswebserver.entity;

import lombok.Data;
import java.util.Map;

/**
 * 动态Schema元数据
 * @author wanghaoxin
 */
@Data
public class DynamicSchemaMetadata {
    private String system;
    private String module;
    private String context;
    private String app;
    private String aggr;
    private String entity;
    // fieldName -> fieldType
    private Map<String, String> fieldTypes;
    // fieldName -> constraints
    private Map<String, Object> fieldConstraints;
    private String primaryKey;
    // fieldName -> relatedEntity
    private Map<String, String> relationships;
}
