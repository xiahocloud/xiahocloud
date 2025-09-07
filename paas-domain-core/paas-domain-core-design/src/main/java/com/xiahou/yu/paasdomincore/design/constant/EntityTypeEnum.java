package com.xiahou.yu.paasdomincore.design.constant;

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
    /**
     * 直接传入map 的形式存储的实体信息
     */
    CUSTOM_ENTITY("custom_entity", "自定义实体"),

    /**
     * 系统实体, 预定义的标准实体信息
     */
    SYSTEM_ENTITY("system_entity", "系统实体"),

    /**
     * 元数据实体, 通过元数据方式存入的实体信息
     */
    META_ENTITY("meta_entity", "元数据实体");

    private final String key;
    private final String description;
    EntityTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
