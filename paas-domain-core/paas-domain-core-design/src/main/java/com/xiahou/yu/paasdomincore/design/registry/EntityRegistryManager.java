package com.xiahou.yu.paasdomincore.design.registry;

import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * 实体注册管理器
 * 统一管理实体的注册、查询和操作
 *
 * @author wanghaoxin
 * @date 2025/9/8
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityRegistryManager {

    private final EntityTypeRegister entityTypeRegister;

    /**
     * 注册实体
     *
     * @param entityRegister 实体注册信息
     */
    public void register(EntityRegister entityRegister) {
        entityTypeRegister.register(entityRegister);
        log.debug("Entity registered through manager: {}", entityRegister.getEntityName());
    }

    /**
     * 注册实体类
     *
     * @param entityName 实体名称
     * @param entityClass 实体类
     * @param entityType 实体类型
     */
    public void registerClass(String entityName, Class<?> entityClass, EntityTypeEnum entityType) {
        entityTypeRegister.registerClass(entityName, entityClass, entityType);
        log.debug("Entity class registered through manager: {} -> {}", entityName, entityClass.getSimpleName());
    }

    /**
     * 注册实体类（默认为标准实体）
     *
     * @param entityName 实体名称
     * @param entityClass 实体类
     */
    public void registerClass(String entityName, Class<?> entityClass) {
        entityTypeRegister.registerClass(entityName, entityClass);
        log.debug("Standard entity registered through manager: {} -> {}", entityName, entityClass.getSimpleName());
    }

    /**
     * 批量注册实体
     *
     * @param entityRegisters 实体注册信息列表
     */
    public void registerAll(EntityRegister... entityRegisters) {
        entityTypeRegister.registerAll(entityRegisters);
        log.info("Batch registered {} entities through manager", entityRegisters.length);
    }

    /**
     * 获取实体类型
     *
     * @param entityName 实体名称
     * @return 实体类型
     */
    public EntityTypeEnum getEntityType(String entityName) {
        return EntityTypeRegister.getEntityType(entityName);
    }

    /**
     * 获取实体类
     *
     * @param entityName 实体名称
     * @return 实体类
     */
    public Class<?> getEntityClass(String entityName) {
        return EntityTypeRegister.getEntityClass(entityName);
    }

    /**
     * 获取实体描述
     *
     * @param entityName 实体名称
     * @return 实体描述
     */
    public String getEntityDescription(String entityName) {
        return entityTypeRegister.getEntityDescription(entityName);
    }

    /**
     * 根据类获取实体名称
     *
     * @param entityClass 实体类
     * @return 实体名称
     */
    public String getEntityNameByClass(Class<?> entityClass) {
        return entityTypeRegister.getEntityNameByClass(entityClass);
    }

    /**
     * 检查实体是否已注册
     *
     * @param entityName 实体名称
     * @return 是否已注册
     */
    public boolean isRegistered(String entityName) {
        return entityTypeRegister.isRegistered(entityName);
    }

    /**
     * 检查实体类是否已注册
     *
     * @param entityClass 实体类
     * @return 是否已注册
     */
    public boolean isClassRegistered(Class<?> entityClass) {
        return entityTypeRegister.isClassRegistered(entityClass);
    }

    /**
     * 检查实体是否为标准实体
     *
     * @param entityName 实体名称
     * @return 是否为标准实体
     */
    public boolean isStdEntity(String entityName) {
        return entityTypeRegister.isStdEntity(entityName);
    }

    /**
     * 检查实体是否为自定义实体
     *
     * @param entityName 实体名称
     * @return 是否为自定义实体
     */
    public boolean isCustomEntity(String entityName) {
        return entityTypeRegister.isCustomEntity(entityName);
    }

    /**
     * 检查实体是否为元数据实体
     *
     * @param entityName 实体名称
     * @return 是否为元数据实体
     */
    public boolean isMetaEntity(String entityName) {
        return entityTypeRegister.isMetaEntity(entityName);
    }

    /**
     * 获取所有已注册的实体名称
     *
     * @return 实体名称集合
     */
    public Set<String> getAllEntityNames() {
        return entityTypeRegister.getAllEntityNames();
    }

    /**
     * 获取所有已注册的实体类
     *
     * @return 实体类集合
     */
    public Collection<Class<?>> getAllEntityClasses() {
        return entityTypeRegister.getAllEntityClasses();
    }

    /**
     * 获取指定类型的所有实体名称
     *
     * @param entityType 实体类型
     * @return 实体名称集合
     */
    public Set<String> getEntityNamesByType(EntityTypeEnum entityType) {
        return entityTypeRegister.getEntityNamesByType(entityType);
    }

    /**
     * 获取指定类型的所有实体类
     *
     * @param entityType 实体类型
     * @return 实体类集合
     */
    public Set<Class<?>> getEntityClassesByType(EntityTypeEnum entityType) {
        return entityTypeRegister.getEntityClassesByType(entityType);
    }

    /**
     * 创建实体实例
     *
     * @param entityName 实体名称
     * @return 实体实例
     */
    public Object createEntityInstance(String entityName) {
        Object instance = entityTypeRegister.createEntityInstance(entityName);
        if (instance != null) {
            log.debug("Created entity instance for: {}", entityName);
        }
        return instance;
    }

    /**
     * 创建指定类型的实体实例
     *
     * @param entityClass 实体类
     * @return 实体实例
     */
    public <T> T createEntityInstance(Class<T> entityClass) {
        String entityName = getEntityNameByClass(entityClass);
        if (entityName == null) {
            log.warn("Entity not registered for class: {}", entityClass.getSimpleName());
            return null;
        }

        Object instance = createEntityInstance(entityName);
        return entityClass.cast(instance);
    }

    /**
     * 获取实体的完整信息
     *
     * @param entityName 实体名称
     * @return 实体信息
     */
    public EntityInfo getEntityInfo(String entityName) {
        if (!isRegistered(entityName)) {
            return null;
        }

        return EntityInfo.builder()
                .entityName(entityName)
                .entityType(getEntityType(entityName))
                .entityClass(getEntityClass(entityName))
                .description(getEntityDescription(entityName))
                .build();
    }

    /**
     * 统计信息
     *
     * @return 注册统计信息
     */
    public RegistryStatistics getStatistics() {
        Set<String> allEntities = getAllEntityNames();

        return RegistryStatistics.builder()
                .totalEntities(allEntities.size())
                .systemEntities(getEntityNamesByType(EntityTypeEnum.SYSTEM_ENTITY).size())
                .customEntities(getEntityNamesByType(EntityTypeEnum.CUSTOM_ENTITY).size())
                .metaEntities(getEntityNamesByType(EntityTypeEnum.META_ENTITY).size())
                .build();
    }

    /**
     * 清空注册信息（主要用于测试）
     */
    public void clear() {
        entityTypeRegister.clear();
        log.info("Entity registry cleared through manager");
    }

    /**
     * 实体信息数据类
     */
    @lombok.Data
    @lombok.Builder
    public static class EntityInfo {
        private String entityName;
        private EntityTypeEnum entityType;
        private Class<?> entityClass;
        private String description;
    }

    /**
     * 注册统计信息数据类
     */
    @lombok.Data
    @lombok.Builder
    public static class RegistryStatistics {
        private int totalEntities;
        private int systemEntities;
        private int customEntities;
        private int metaEntities;
    }
}
