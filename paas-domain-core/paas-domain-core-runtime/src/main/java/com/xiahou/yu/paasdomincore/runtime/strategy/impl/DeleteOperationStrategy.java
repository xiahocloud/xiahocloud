package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import com.xiahou.yu.paasdomincore.design.registry.EntityRegistryManager;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeleteOperationStrategy implements DataOperationStrategy, EntityExecutor {

    private final RepositoryManager repositoryManager;

    private final EntityRegistryManager entityRegistryManager;

    @Override
    public EntityRegistryManager getEntityRegistryManager() {
        return entityRegistryManager;
    }

    @Override
    public DataOperationResult execute(CommandContext context) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing DELETE operation for {}.{}", aggr, context.getEntityName());
        return executeByEntityType(context);
    }

    @Override
    public DataOperationResult metaEntityExecute(CommandContext context) {
        log.info("Executing META entity DELETE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 根据过滤条件删除元数据实体
                // Filter filter = context.getFilter();
                // deleteEntityByFilter(entityName, filter);
                return new DataOperationResult();
            } else {
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error deleting meta entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }

    @Override
    public DataOperationResult systemEntityExecute(CommandContext context) {
        log.info("Executing STD entity DELETE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 根据过滤条件删除标准实体
                return new DataOperationResult();
            } else {
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error deleting standard entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }

    @Override
    public DataOperationResult customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity DELETE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 删除自定义实体
                return new DataOperationResult();
            } else {
                // TODO: 使用动态删除逻辑
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error deleting custom entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }
}
