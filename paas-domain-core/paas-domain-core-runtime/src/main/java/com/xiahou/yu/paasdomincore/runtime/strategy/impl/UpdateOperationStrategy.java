package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.design.registry.EntityRegistryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 更新操作策略
 *
 * @author xiahou
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateOperationStrategy implements DataOperationStrategy, EntityExecutor {

    private final RepositoryManager repositoryManager;

    private final EntityRegistryManager entityRegistryManager;

    @Override
    public EntityRegistryManager getEntityRegistryManager() {
        return entityRegistryManager;
    }

    @Override
    public Object execute(CommandContext context) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing UPDATE operation for {}.{}", aggr, context.getEntityName());
        return executeByEntityType(context);
    }

    @Override
    public Object metaEntityExecute(CommandContext context) {
        log.info("Executing META entity UPDATE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 根据过滤条件查找现有实体，然后更新
                // Filter filter = context.getFilter();
                // Object existingEntity = findEntityByFilter(entityName, filter);
                // Object updatedEntity = updateEntityData(existingEntity, record);
                // Object savedEntity = repositoryManager.save(entityName, updatedEntity);
                return Map.of("success", true, "message", "Meta entity updated successfully");
            } else {
                return Map.of("success", false, "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error updating meta entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to update meta entity: " + e.getMessage());
        }
    }

    @Override
    public Object systemEntityExecute(CommandContext context) {
        log.info("Executing STD entity UPDATE for {}", context.getEntityName());
        String entityName = context.getEntityName();
        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 根据过滤条件更新标准实体
                return Map.of("success", true, "message", "Standard entity updated successfully");
            } else {
                return Map.of("success", false, "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error updating standard entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to update standard entity: " + e.getMessage());
        }
    }

    @Override
    public Object customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity UPDATE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 更新自定义实体
                return Map.of("success", true, "message", "Custom entity updated successfully");
            } else {
                // TODO: 使用动态更新逻辑
                return Map.of("success", true, "message", "Custom entity updated via dynamic processing");
            }
        } catch (Exception e) {
            log.error("Error updating custom entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to update custom entity: " + e.getMessage());
        }
    }
}
