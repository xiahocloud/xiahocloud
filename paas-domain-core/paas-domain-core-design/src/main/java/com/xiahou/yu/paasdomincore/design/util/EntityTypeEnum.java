package com.xiahou.yu.paasdomincore.design.util;

import com.xiahou.yu.paasdomincore.design.enumgetter.EnumGetter;
import lombok.Getter;

/**
 * description
 *
 * @author wanghaoxin
 * date     2025/9/1 08:46
 * @version 1.0
 */
@Getter
public enum EntityTypeEnum implements EnumGetter<EntityTypeEnum> {
    CUSTOM_ENTITY("custom_entity", "自定义实体"),
    STD_ENTITY("system_entity", "系统实体"),
    META_ENTITY("meta_entity", "元数据实体");

    private final String key;
    private final String description;
    EntityTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
