package com.xiahou.yu.paasdomincore.design.registry;

import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类型注册中心
 * 管理所有实体的类型信息
 *
 * @author wanghaoxin
 * @date 2025/9/4
 */
@Component
@Slf4j
public class EntityTypeRegister {

    /**
     * 实体名称 -> 实体类型映射
     */
    private static final Map<String, EntityTypeEnum> ENTITY_TYPE_ENUM_MAP = new ConcurrentHashMap<>();


    /**
     * 注册实体
     *
     * @param entityRegister 实体注册信息
     */
    public void register(EntityRegister entityRegister) {
        String entityName = entityRegister.getClass().getName();
        EntityTypeEnum entityType = entityRegister.getEntityType();

        // 注册到各个映射表
        ENTITY_TYPE_ENUM_MAP.put(entityName, entityType);

        log.info("Entity registered: name={}, type={}", entityName, entityType);
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
    }

    /**
     * 获取实体类型
     *
     * @param entityName 实体名称
     * @return 实体类型
     */
    public static EntityTypeEnum getEntityType(String entityName) {
        return ENTITY_TYPE_ENUM_MAP.get(entityName);
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
     * 检查实体是否为标准实体
     *
     * @param entityName 实体名称
     * @return 是否为标准实体
     */
    public boolean isStdEntity(String entityName) {
        EntityTypeEnum entityType = getEntityType(entityName);
        return EntityTypeEnum.STD_ENTITY.equals(entityType);
    }

    /**
     * 检查实体是否为自定义实体
     *
     * @param entityName 实体名称
     * @return 是否为自定义实体
     */
    public boolean isCustomEntity(String entityName) {
        EntityTypeEnum entityType = getEntityType(entityName);
        return EntityTypeEnum.CUSTOM_ENTITY.equals(entityType);
    }

    /**
     * 检查实体是否为元数据实体
     *
     * @param entityName 实体名称
     * @return 是否为元数据实体
     */
    public boolean isMetaEntity(String entityName) {
        EntityTypeEnum entityType = getEntityType(entityName);
        return EntityTypeEnum.META_ENTITY.equals(entityType);
    }

    /**
     * 获取所有已注册的实体名称
     *
     * @return 实体名称集合
     */
    public java.util.Set<String> getAllEntityNames() {
        return ENTITY_TYPE_ENUM_MAP.keySet();
    }

    /**
     * 获取指定类型的所有实体名称
     *
     * @param entityType 实体类型
     * @return 实体名称集合
     */
    public java.util.Set<String> getEntityNamesByType(EntityTypeEnum entityType) {
        return ENTITY_TYPE_ENUM_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityType))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 清空注册信息（主要用于测试）
     */
    public void clear() {
        ENTITY_TYPE_ENUM_MAP.clear();
        log.info("Entity register cleared");
    }
}
