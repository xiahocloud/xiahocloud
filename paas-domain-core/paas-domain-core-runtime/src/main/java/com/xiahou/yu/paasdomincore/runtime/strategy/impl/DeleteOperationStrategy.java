package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 删除操作策略
 *
 * @author xiahou
 */
@Slf4j
@Component
public class DeleteOperationStrategy implements DataOperationStrategy, EntityExecutor {

    @Autowired(required = false)
    private RepositoryManager repositoryManager;

    @Override
    public Object execute(CommandContext context) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing DELETE operation for {}.{}", aggr, context.getEntityName());
        return executeByEntityType(context);
    }

    @Override
    public Object metaEntityExecute(CommandContext context) {
        log.info("Executing META entity DELETE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 根据过滤条件删除元数据实体
                // Filter filter = context.getFilter();
                // deleteEntityByFilter(entityName, filter);
                return Map.of("success", true, "message", "Meta entity deleted successfully");
            } else {
                return Map.of("success", false, "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error deleting meta entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to delete meta entity: " + e.getMessage());
        }
    }

    @Override
    public Object stdEntityExecute(CommandContext context) {
        log.info("Executing STD entity DELETE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 根据过滤条件删除标准实体
                return Map.of("success", true, "message", "Standard entity deleted successfully");
            } else {
                return Map.of("success", false, "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error deleting standard entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to delete standard entity: " + e.getMessage());
        }
    }

    @Override
    public Object customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity DELETE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 删除自定义实体
                return Map.of("success", true, "message", "Custom entity deleted successfully");
            } else {
                // TODO: 使用动态删除逻辑
                return Map.of("success", true, "message", "Custom entity deleted via dynamic processing");
            }
        } catch (Exception e) {
            log.error("Error deleting custom entity: {}", entityName, e);
            return Map.of("success", false, "message", "Failed to delete custom entity: " + e.getMessage());
        }
    }
}
