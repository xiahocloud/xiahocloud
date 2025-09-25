package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import com.xiahou.yu.paasdomincore.design.registry.EntityRegistryManager;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询操作策略
 *
 * @author xiahou
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QueryOperationStrategy implements DataOperationStrategy, EntityExecutor {

    private final RepositoryManager repositoryManager;
    private final EntityRegistryManager entityRegistryManager;

    @Override
    public EntityRegistryManager getEntityRegistryManager() {
        return entityRegistryManager;
    }

    @Override
    public DataOperationResult execute(CommandContext context) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing QUERY operation for {}.{}", aggr, context.getEntityName());
        return executeByEntityType(context);
    }

    @Override
    public DataOperationResult metaEntityExecute(CommandContext context) {
        log.info("Executing META entity QUERY for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 查询元数据实体
                List<?> entities = repositoryManager.findAll(entityName);
                return new DataOperationResult(entities);
            } else {
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error querying meta entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }

    @Override
    public DataOperationResult systemEntityExecute(CommandContext context) {
        log.info("Executing STD entity QUERY for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 根据过滤条件查询标准实体
                // TODO: 使用 context.getFilter() 来构建查询条件
                List<?> entities = repositoryManager.findAll(entityName);
                return new DataOperationResult(entities);
            } else {
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error querying standard entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }

    @Override
    public DataOperationResult customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity QUERY for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // 查询自定义实体
                List<?> entities = repositoryManager.findAll(entityName);
                return new DataOperationResult();
            } else {
                // TODO: 使用动态查询逻辑处理没有固定Repository的自定义实体
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error querying custom entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }
}
