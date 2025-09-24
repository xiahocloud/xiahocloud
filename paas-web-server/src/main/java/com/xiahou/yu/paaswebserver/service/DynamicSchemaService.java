package com.xiahou.yu.paaswebserver.service;

import com.xiahou.yu.paaswebserver.entity.DynamicSchemaMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态Schema服务
 * 管理Schema元数据的注册、查询和缓存
 */
@Service
@Slf4j
public class DynamicSchemaService {

    // 使用内存缓存存储Schema元数据，实际项目中应该使用数据库
    private final Map<String, DynamicSchemaMetadata> schemaCache = new ConcurrentHashMap<>();

    /**
     * 注册Schema元数据
     */
    public void registerSchema(DynamicSchemaMetadata metadata) {
        String schemaKey = buildSchemaKey(metadata);
        schemaCache.put(schemaKey, metadata);
        log.info("Registered schema: {}", schemaKey);
    }

    /**
     * 获取Schema元数据
     */
    public DynamicSchemaMetadata getSchema(String system, String module, String context,
                                          String app, String aggr, String entity) {
        String schemaKey = buildSchemaKey(system, module, context, app, aggr, entity);
        DynamicSchemaMetadata metadata = schemaCache.get(schemaKey);

        if (metadata == null) {
            // 如果缓存中没有，返回默认的Schema
            metadata = createDefaultSchema(system, module, context, app, aggr, entity);
            schemaCache.put(schemaKey, metadata);
        }

        return metadata;
    }

    /**
     * 创建默认Schema（用于演示）
     */
    private DynamicSchemaMetadata createDefaultSchema(String system, String module, String context,
                                                     String app, String aggr, String entity) {
        DynamicSchemaMetadata metadata = new DynamicSchemaMetadata();
        metadata.setSystem(system);
        metadata.setModule(module);
        metadata.setContext(context);
        metadata.setApp(app);
        metadata.setAggr(aggr);
        metadata.setEntity(entity);
        metadata.setPrimaryKey("id");

        // 设置默认字段类型
        Map<String, String> fieldTypes = new HashMap<>();
        fieldTypes.put("id", "ID");
        fieldTypes.put("name", "String");
        fieldTypes.put("description", "String");
        fieldTypes.put("createdAt", "DateTime");
        fieldTypes.put("updatedAt", "DateTime");
        metadata.setFieldTypes(fieldTypes);

        // 设置字段约束
        Map<String, Object> constraints = new HashMap<>();
        constraints.put("name", Map.of("required", true, "maxLength", 128));
        constraints.put("description", Map.of("maxLength", 500));
        metadata.setFieldConstraints(constraints);

        return metadata;
    }

    /**
     * 构建Schema缓存键
     */
    private String buildSchemaKey(DynamicSchemaMetadata metadata) {
        return buildSchemaKey(metadata.getSystem(), metadata.getModule(),
                            metadata.getContext(), metadata.getApp(),
                            metadata.getAggr(), metadata.getEntity());
    }

    private String buildSchemaKey(String system, String module, String context,
                                String app, String aggr, String entity) {
        return String.format("%s.%s.%s.%s.%s.%s",
            nullToEmpty(system), nullToEmpty(module), nullToEmpty(context),
            nullToEmpty(app), nullToEmpty(aggr), nullToEmpty(entity));
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * 获取所有已注册的Schema
     */
    public Map<String, DynamicSchemaMetadata> getAllSchemas() {
        return new HashMap<>(schemaCache);
    }
}
