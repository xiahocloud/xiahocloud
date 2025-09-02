package com.xiahou.yu.paasdomincore.design.register;

import com.xiahou.yu.paasdomincore.design.util.EntityTypeEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/1 00:32
 * @version 1.0
 */
@Component
public class EntityTypeRegister {

    private final static Map<String, EntityTypeEnum> ENTITY_TYPE_MAP = new HashMap<>();
    public void registerEntityType(EntityTypeEnum entityType, String entityName) {
        ENTITY_TYPE_MAP.put(entityName, entityType);
    }

    public static EntityTypeEnum getEntityType(String entityName) {
        return ENTITY_TYPE_MAP.get(entityName);
    }
}
