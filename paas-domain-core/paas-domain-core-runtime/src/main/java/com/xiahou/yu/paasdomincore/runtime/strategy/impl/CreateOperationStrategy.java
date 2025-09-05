package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 创建操作策略
 *
 * @author xiahou
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreateOperationStrategy implements DataOperationStrategy, EntityExecutor {

    private final RepositoryManager repositoryManager;

    @Override
    public Object execute(CommandContext context) {
        log.info("Executing CREATE operation for {}", context.getEntityName());
        return executeByEntityType(context);
    }

    @Override
    public Object metaEntityExecute(CommandContext context) {
        log.info("Executing META entity CREATE for {}", context.getEntityName());
        String entityName = context.getEntityName();
        Map<String, Object> data = context.getData();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 将 Map 数据转换为对应的实体对象
                Object entity = convertToEntity(entityName, data);
                if (entity != null) {
                    // 使用 RepositoryManager 统一保存接口
                    Object savedEntity = repositoryManager.save(entityName, entity);
                    log.info("Successfully created META {} entity with data: {}", entityName, savedEntity);
                    return Map.of("success", true, "data", savedEntity, "message", "Meta entity created successfully");
                } else {
                    return Map.of("success", false, "message", "Failed to convert data to entity: " + entityName);
                }
            } else {
                return Map.of("success", false, "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error creating meta entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to create meta entity: " + e.getMessage());
        }
    }

    @Override
    public Object stdEntityExecute(CommandContext context) {
        log.info("Executing STD entity CREATE for {}", context.getEntityName());
        String entityName = context.getEntityName();
        Map<String, Object> data = context.getData();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 将 Map 数据转换为对应的实体对象
                Object entity = convertToEntity(entityName, data);
                if (entity != null) {
                    // 保存实体
                    Object savedEntity = repositoryManager.save(entityName, entity);
                    log.info("Successfully created {} entity with data: {}", entityName, savedEntity);
                    return Map.of("success", true, "data", savedEntity, "message", "Standard entity created successfully");
                } else {
                    return Map.of("success", false, "message", "Failed to convert data to entity: " + entityName);
                }
            } else {
                return Map.of("success", false, "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error creating standard entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to create standard entity: " + e.getMessage());
        }
    }

    @Override
    public Object customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity CREATE for {}", context.getEntityName());
        String entityName = context.getEntityName();
        Map<String, Object> data = context.getData();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 动态处理自定义实体的创建
                return Map.of("success", true, "message", "Custom entity created successfully");
            } else {
                // TODO: 使用动态数据处理逻辑
                return Map.of("success", true, "message", "Custom entity created via dynamic processing");
            }
        } catch (Exception e) {
            log.error("Error creating custom entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to create custom entity: " + e.getMessage());
        }
    }

    /**
     * 将 Map 数据转换为对应的实体对象
     * 这里使用简单的反射机制，也可以集成 ObjectMapper 等工具
     */
    private Object convertToEntity(String entityName, Map<String, Object> data) {
        try {
            // 根据实体名称获取对应的实体类
            String entityClassName = "com.xiahou.yu.paaswebserver.entity." + entityName;
            Class<?> entityClass = Class.forName(entityClassName);

            // 创建实体实例
            Object entity = entityClass.getDeclaredConstructor().newInstance();

            // 设置字段值（这里使用简单的反射，实际项目中可以使用 BeanUtils 或 ObjectMapper）
            java.lang.reflect.Field[] fields = entityClass.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = data.get(field.getName());
                if (value != null) {
                    // 类型转换处理
                    if (field.getType() == java.time.LocalDateTime.class && value instanceof String) {
                        value = java.time.LocalDateTime.parse((String) value);
                    } else if (field.getType() == Long.class && value instanceof Integer) {
                        value = ((Integer) value).longValue();
                    }
                    field.set(entity, value);
                }
            }

            return entity;
        } catch (Exception e) {
            log.error("Error converting data to entity {}: {}", entityName, e.getMessage());
            return null;
        }
    }
}
