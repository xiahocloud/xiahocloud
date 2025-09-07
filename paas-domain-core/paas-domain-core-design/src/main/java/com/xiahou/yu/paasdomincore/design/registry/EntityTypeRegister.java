package com.xiahou.yu.paasdomincore.design.registry;

import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类型注册中心（内部实现）
 * 由 EntityRegistryManager 统一管理，不建议直接使用
 *
 * @author wanghaoxin
 * @date 2025/9/4
 */
@Component
@Slf4j
class EntityTypeRegister {

    /**
     * 实体名称 -> 实体类型映射
     */
    private static final Map<String, EntityTypeEnum> ENTITY_TYPE_ENUM_MAP = new ConcurrentHashMap<>();

    /**
     * 实体名称 -> 实体类映射
     */
    private static final Map<String, Class<?>> ENTITY_CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * 实体名称 -> 描述映射
     */
    private static final Map<String, String> ENTITY_DESCRIPTION_MAP = new ConcurrentHashMap<>();

    /**
     * 注册实体
     *
     * @param entityRegister 实体注册信息
     */
    void register(EntityRegister entityRegister) {
        String entityName = entityRegister.getEntityName();
        EntityTypeEnum entityType = entityRegister.getEntityType();
        Class<?> entityClass = entityRegister.getEntityClass();
        String description = entityRegister.getDescription();

        // 注册到各个映射表
        ENTITY_TYPE_ENUM_MAP.put(entityName, entityType);
        ENTITY_CLASS_MAP.put(entityName, entityClass);
        ENTITY_DESCRIPTION_MAP.put(entityName, description);

        log.info("Entity registered: name={}, type={}, class={}, description={}",
                entityName, entityType, entityClass.getSimpleName(), description);
    }

    /**
     * 注册实体类
     *
     * @param entityName 实体名称
     * @param entityClass 实体类
     * @param entityType 实体类型
     */
    void registerClass(String entityName, Class<?> entityClass, EntityTypeEnum entityType) {
        ENTITY_TYPE_ENUM_MAP.put(entityName, entityType);
        ENTITY_CLASS_MAP.put(entityName, entityClass);
        ENTITY_DESCRIPTION_MAP.put(entityName, entityName + " - " + entityType.getDescription());

        log.info("Entity class registered: name={}, type={}, class={}",
                entityName, entityType, entityClass.getSimpleName());
    }

    /**
     * 注册实体类（默认为标准实体）
     *
     * @param entityName 实体名称
     * @param entityClass 实体类
     */
    void registerClass(String entityName, Class<?> entityClass) {
        registerClass(entityName, entityClass, EntityTypeEnum.SYSTEM_ENTITY);
    }

    /**
     * 批量注册实体
     *
     * @param entityRegisters 实体注册信息列表
     */
    void registerAll(EntityRegister... entityRegisters) {
        for (EntityRegister entityRegister : entityRegisters) {
            register(entityRegister);
        }
    }

    /**
     * 获取实体类型
     *
     * @param entityName 实体名称
     * @return 实体类型
     */
    static EntityTypeEnum getEntityType(String entityName) {
        return ENTITY_TYPE_ENUM_MAP.get(entityName);
    }

    /**
     * 获取实体类
     *
     * @param entityName 实体名称
     * @return 实体类
     */
    static Class<?> getEntityClass(String entityName) {
        return ENTITY_CLASS_MAP.get(entityName);
    }

    /**
     * 获取实体描述
     *
     * @param entityName 实体名称
     * @return 实体描述
     */
    String getEntityDescription(String entityName) {
        return ENTITY_DESCRIPTION_MAP.get(entityName);
    }

    /**
     * 根据类获取实体名称
     *
     * @param entityClass 实体类
     * @return 实体名称
     */
    String getEntityNameByClass(Class<?> entityClass) {
        return ENTITY_CLASS_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityClass))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * 检查实体是否已注册
     *
     * @param entityName 实体名称
     * @return 是否已注册
     */
    boolean isRegistered(String entityName) {
        return ENTITY_TYPE_ENUM_MAP.containsKey(entityName);
    }

    /**
     * 检查实体类是否已注册
     *
     * @param entityClass 实体类
     * @return 是否已注册
     */
    boolean isClassRegistered(Class<?> entityClass) {
        return ENTITY_CLASS_MAP.containsValue(entityClass);
    }

    /**
     * 检查实体是否为标准实体
     *
     * @param entityName 实体名称
     * @return 是否为标准实体
     */
    boolean isStdEntity(String entityName) {
        EntityTypeEnum entityType = getEntityType(entityName);
        return EntityTypeEnum.STD_ENTITY.equals(entityType);
    }

    /**
     * 检查实体是否为自定义实体
     *
     * @param entityName 实体名称
     * @return 是否为自定义实体
     */
    boolean isCustomEntity(String entityName) {
        EntityTypeEnum entityType = getEntityType(entityName);
        return EntityTypeEnum.CUSTOM_ENTITY.equals(entityType);
    }

    /**
     * 检查实体是否为元数据实体
     *
     * @param entityName 实体名称
     * @return 是否为元数据实体
     */
    boolean isMetaEntity(String entityName) {
        EntityTypeEnum entityType = getEntityType(entityName);
        return EntityTypeEnum.META_ENTITY.equals(entityType);
    }

    /**
     * 获取所有已注册的实体名称
     *
     * @return 实体名称集合
     */
    java.util.Set<String> getAllEntityNames() {
        return ENTITY_TYPE_ENUM_MAP.keySet();
    }

    /**
     * 获取所有已注册的实体类
     *
     * @return 实体类集合
     */
    java.util.Collection<Class<?>> getAllEntityClasses() {
        return ENTITY_CLASS_MAP.values();
    }

    /**
     * 获取指定类型的所有实体名称
     *
     * @param entityType 实体类型
     * @return 实体名称集合
     */
    java.util.Set<String> getEntityNamesByType(EntityTypeEnum entityType) {
        return ENTITY_TYPE_ENUM_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityType))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 获取指定类型的所有实体类
     *
     * @param entityType 实体类型
     * @return 实体类集合
     */
    java.util.Set<Class<?>> getEntityClassesByType(EntityTypeEnum entityType) {
        return ENTITY_TYPE_ENUM_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityType))
                .map(entry -> ENTITY_CLASS_MAP.get(entry.getKey()))
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 创建实体实例
     *
     * @param entityName 实体名称
     * @return 实体实例
     */
    Object createEntityInstance(String entityName) {
        Class<?> entityClass = getEntityClass(entityName);
        if (entityClass == null) {
            log.warn("Entity class not found for: {}", entityName);
            return null;
        }

        try {
            return entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Failed to create instance for entity: {}", entityName, e);
            return null;
        }
    }

    /**
     * 清空注册信息（主要用于测试）
     */
    void clear() {
        ENTITY_TYPE_ENUM_MAP.clear();
        ENTITY_CLASS_MAP.clear();
        ENTITY_DESCRIPTION_MAP.clear();
        log.info("Entity register cleared");
    }
}
