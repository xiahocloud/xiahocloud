package com.xiahou.yu.paasdomincore.runtime.strategy.impl;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import com.xiahou.yu.paasdomincore.design.metadatamodel.AbstractModel;
import com.xiahou.yu.paasdomincore.design.registry.EntityRegistryManager;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.EntityExecutor;
import com.xiahou.yu.paasinfracommon.utils.ObjectMapperService;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private final EntityRegistryManager entityRegistryManager;

    private final ObjectMapperService objectMapperService;

    @Override
    public EntityRegistryManager getEntityRegistryManager() {
        return entityRegistryManager;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataOperationResult execute(CommandContext context) {
        log.info("Executing CREATE operation for {}", context.getEntityName());
        return executeByEntityType(context);
    }

    private <T> T convertToEntity(String entityName, DynamicDataObject record, Class<T> clazz) {
        try {
            return objectMapperService.convertToEntity(record.getOriginalObject(), clazz);
        } catch (Exception e) {
            log.error("Error converting record to entity {}: {}", entityName, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DataOperationResult metaEntityExecute(CommandContext context) {
        log.info("Executing META entity CREATE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            List<Object> results = new ArrayList<>();
            for (DynamicDataObject record : context.getRecords()) {
                if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                    // 将 Map 数据转换为对应的实体对象
                    Class<?> entityClass = entityRegistryManager.getEntityClass(entityName);
                    Object entity = convertToEntity(entityName, record, entityClass);
                    if (entity != null) {
                        // 使用 RepositoryManager 统一保存接口
                        Object savedEntity = repositoryManager.save(entityName, entity);
                        log.info("Successfully created META {} entity with record: {}", entityName, savedEntity);
                        results.add(savedEntity);
                    } else {
                        log.error("Fail to create META {} entity with record: {}", entityName, record.getOriginalObject());
                    }
                } else {
                    results.add(Map.of("success", false, "message", "No repository found for entity: " + entityName));
                }
            }
            return new DataOperationResult(results);

        } catch (Exception e) {
            log.error("Error creating meta entity: {}", entityName, e);
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public DataOperationResult systemEntityExecute(CommandContext context) {
        log.info("Executing STD entity CREATE for {}", context.getEntityName());
        String entityName = context.getEntityName();

        try {
            List<Object> entities = new ArrayList<>();
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                List<DynamicDataObject> records = context.getRecords();
                if (records.size() == 1) {
                    DynamicDataObject record = records.get(0);
                    // 将 Map 数据转换为对应的实体对象
                    Class<?> entityClass = entityRegistryManager.getEntityClass(entityName);
                    Object entity = convertToEntity(entityName, record, entityClass);
                    Object savedEntity = repositoryManager.save(entityName, entity);
                    AbstractModel abstractModel = (AbstractModel) entity;
                    if (abstractModel != null) {
                        abstractModel.markAsNotNew();
                    }
                    entities.add(savedEntity);
                    log.info("Successfully created {} entity with data: {}", entityName, savedEntity);
                    return new DataOperationResult(entities);
                }

                for (DynamicDataObject record : records) {
                    // 将 Map 数据转换为对应的实体对象
                    Class<?> entityClass = entityRegistryManager.getEntityClass(entityName);
                    Object entity = convertToEntity(entityName, record, entityClass);
                    if (entity != null) {
                        // 保存实体
                        entities.add(entity);
                    } else {
                        log.warn("Entity {} not empty, skip create", entityName);
                        continue;
                    }
                }
                List<Object> results = repositoryManager.saveAll(entityName, entities);
                return new DataOperationResult(results);
            } else {
                throw new PaaSException(ResultStatusEnum.ENTITY_NOT_FOUND, "No repository found for entity: " + entityName);
            }
        } catch (Exception e) {
            log.error("Error creating standard entity: {}", entityName, e);
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, "", e);
        }
    }

    @Override
    public DataOperationResult customEntityExecute(CommandContext context) {
        log.info("Executing CUSTOM entity CREATE for {}", context.getEntityName());
        String entityName = context.getEntityName();
        DynamicDataObject record = null;

        try {
            if (repositoryManager != null && repositoryManager.hasRepository(entityName)) {
                // TODO: 动态处理自定义实体的创建
                return new DataOperationResult();
            } else {
                // TODO: 使用动态数据处理逻辑
                return new DataOperationResult();
            }
        } catch (Exception e) {
            log.error("Error creating custom entity: {}", entityName, e);
            return new DataOperationResult();
        }
    }

}
