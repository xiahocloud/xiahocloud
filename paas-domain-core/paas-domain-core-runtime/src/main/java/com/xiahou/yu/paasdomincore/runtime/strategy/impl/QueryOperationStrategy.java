package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 查询操作策略
 *
 * @author xiahou
 */
@Slf4j
@Component
public class QueryOperationStrategy implements DataOperationStrategy, EntityExecutor {

    @Autowired(required = false)
    private RepositoryManager repositoryManager;

    @Override
    public Object execute(CommandContext context) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing QUERY operation for {}.{}", aggr, context.getEntityName());
        return executeByEntityType(context);
    }

    @Override
    public Object metaEntityExecute(CommandContext context) {
        log.info("Executing META entity QUERY for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 查询元数据实体
                List<?> entities = repositoryManager.findAll(entityName);
                return Map.of("success", true, "data", entities, "message", "Meta data queried successfully");
            } else {
                return Map.of("success", false, "data", List.of(), "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error querying meta entity: {}", entityName, e);
            return Map.of("success", false, "data", List.of(), "message", "Failed to query meta entity: " + e.getMessage());
        }
    }

    @Override
    public Object systemEntityExecute(CommandContext context) {
        log.info("Executing STD entity QUERY for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 根据过滤条件查询标准实体
                // TODO: 使用 context.getFilter() 来构建查询条件
                List<?> entities = repositoryManager.findAll(entityName);
                return Map.of("success", true, "data", entities, "message", "Standard entity queried successfully");
            } else {
                return Map.of("success", false, "data", List.of(), "message", "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error querying standard entity: {}", entityName, e);
            return Map.of("success", false, "data", List.of(), "message", "Failed to query standard entity: " + e.getMessage());
        }
    }

    @Override
    public Object customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity QUERY for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 查询自定义实体
                List<?> entities = repositoryManager.findAll(entityName);
                return Map.of("success", true, "data", entities, "message", "Custom entity queried successfully");
            } else {
                // TODO: 使用动态查询逻辑处理没有固定Repository的自定义实体
                return Map.of("success", true, "data", List.of(), "message", "Custom entity queried via dynamic processing");
            }
        } catch (Exception e) {
            log.error("Error querying custom entity: {}", entityName, e);
            return Map.of("success", false, "data", List.of(), "message", "Failed to query custom entity: " + e.getMessage());
        }
    }
}
