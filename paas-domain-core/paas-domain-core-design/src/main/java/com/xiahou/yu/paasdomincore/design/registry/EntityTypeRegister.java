package com.xiahou.yu.paasdomincore.design.registry;

import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类型注册器
 * 管理实体类型的注册和查询
 *
 * @author xiahou
 */
@Component
@Slf4j
public class EntityTypeRegister {

    private static final Map<String, EntityTypeEnum> ENTITY_TYPE_ENUM_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> ENTITY_CLASS_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> ENTITY_DESCRIPTION_MAP = new ConcurrentHashMap<>();

    /**
     * 注册实体
     *
     * @param entityRegister 实体注册信息
     */
    public void register(EntityRegister entityRegister) {
        String entityName = entityRegister.getEntityName();
        ENTITY_TYPE_ENUM_MAP.put(entityName, entityRegister.getEntityType());
        ENTITY_CLASS_MAP.put(entityName, entityRegister.getEntityClass());
        ENTITY_DESCRIPTION_MAP.put(entityName, entityRegister.getDesc());
        log.debug("Entity registered: {} -> {}", entityName, entityRegister.getEntityType());
    }

    /**
     * 注册实体类
     *
     * @param entityName 实体名称
     * @param entityClass 实体类
     * @param entityType 实体类型
     */
    public void registerClass(String entityName, Class<?> entityClass, EntityTypeEnum entityType) {
        ENTITY_TYPE_ENUM_MAP.put(entityName, entityType);
        ENTITY_CLASS_MAP.put(entityName, entityClass);
        ENTITY_DESCRIPTION_MAP.put(entityName, entityClass.getSimpleName());
        log.debug("Entity class registered: {} -> {} ({})", entityName, entityClass.getSimpleName(), entityType);
    }

    /**
     * 注册实体类（默认为系统实体）
     *
     * @param entityName 实体名称
     * @param entityClass 实体类
     */
    public void registerClass(String entityName, Class<?> entityClass) {
        registerClass(entityName, entityClass, EntityTypeEnum.SYSTEM_ENTITY);
    }

    /**
     * 批量注册实体
     *
     * @param entityRegisters 实体注册信息列表
     */
    public void registerAll(EntityRegister... entityRegisters) {
        for (EntityRegister entityRegister : entityRegisters) {
            register(entityRegister);
        }
        log.info("Batch registered {} entities", entityRegisters.length);
    }

    /**
     * 获取实体类型（静态方法）
     *
     * @param entityName 实体名称
     * @return 实体类型
     */
    public static EntityTypeEnum getEntityType(String entityName) {
        return ENTITY_TYPE_ENUM_MAP.getOrDefault(entityName, EntityTypeEnum.CUSTOM_ENTITY);
    }

    /**
     * 获取实体类（静态方法）
     *
     * @param entityName 实体名称
     * @return 实体类
     */
    public static Class<?> getEntityClass(String entityName) {
        return ENTITY_CLASS_MAP.get(entityName);
    }

    /**
     * 获取实体描述
     *
     * @param entityName 实体名称
     * @return 实体描述
     */
    public String getEntityDescription(String entityName) {
        return ENTITY_DESCRIPTION_MAP.get(entityName);
    }

    /**
     * 根据类获取实体名称
     *
     * @param entityClass 实体类
     * @return 实体名称
     */
    public String getEntityNameByClass(Class<?> entityClass) {
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
    public boolean isRegistered(String entityName) {
        return ENTITY_TYPE_ENUM_MAP.containsKey(entityName);
    }

    /**
     * 检查实体类是否已注册
     *
     * @param entityClass 实体类
     * @return 是否已注册
     */
    public boolean isClassRegistered(Class<?> entityClass) {
        return ENTITY_CLASS_MAP.containsValue(entityClass);
    }

    /**
     * 检查实体是否为系统实体
     *
     * @param entityName 实体名称
     * @return 是否为系统实体
     */
    public boolean isStdEntity(String entityName) {
        return EntityTypeEnum.SYSTEM_ENTITY.equals(getEntityType(entityName));
    }

    /**
     * 检查实体是否为自定义实体
     *
     * @param entityName 实体名称
     * @return 是否为自定义实体
     */
    public boolean isCustomEntity(String entityName) {
        return EntityTypeEnum.CUSTOM_ENTITY.equals(getEntityType(entityName));
    }

    /**
     * 检查实体是否为元数据实体
     *
     * @param entityName 实体名称
     * @return 是否为元数据实体
     */
    public boolean isMetaEntity(String entityName) {
        return EntityTypeEnum.META_ENTITY.equals(getEntityType(entityName));
    }

    /**
     * 获取所有已注册的实体名称
     *
     * @return 实体名称集合
     */
    public Set<String> getAllEntityNames() {
        return new HashSet<>(ENTITY_TYPE_ENUM_MAP.keySet());
    }

    /**
     * 获取所有已注册的实体类
     *
     * @return 实体类集合
     */
    public Collection<Class<?>> getAllEntityClasses() {
        return new ArrayList<>(ENTITY_CLASS_MAP.values());
    }

    /**
     * 根据类型获取实体名称
     *
     * @param entityType 实体类型
     * @return 实体名称集合
     */
    public Set<String> getEntityNamesByType(EntityTypeEnum entityType) {
        return ENTITY_TYPE_ENUM_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityType))
                .map(Map.Entry::getKey)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * 根据类型获取实体类
     *
     * @param entityType 实体类型
     * @return 实体类集合
     */
    public Set<Class<?>> getEntityClassesByType(EntityTypeEnum entityType) {
        return ENTITY_TYPE_ENUM_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityType))
                .map(entry -> ENTITY_CLASS_MAP.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * 创建实体实例
     *
     * @param entityName 实体名称
     * @return 实体实例
     */
    public Object createEntityInstance(String entityName) {
        Class<?> entityClass = getEntityClass(entityName);
        if (entityClass == null) {
            log.warn("No entity class found for: {}", entityName);
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
    public void clear() {
        ENTITY_TYPE_ENUM_MAP.clear();
        ENTITY_CLASS_MAP.clear();
        ENTITY_DESCRIPTION_MAP.clear();
        log.info("Entity type register cleared");
    }
}
