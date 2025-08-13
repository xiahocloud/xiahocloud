package com.xiahou.yu.paasmetacore.models.props;

import com.xiahou.yu.paasmetacore.constant.DataTypeEnum;

/**
 * 属性记录类，使用Java 21 record语法
 */
public record Property(
        String id,
        String name,
        String dataType,
        String description,
        String defaultValue,
        boolean required
) {

    // 紧凑构造器进行验证
    public Property {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Property ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Property name cannot be null or blank");
        }
    }

    /**
     * 创建属性的便捷方法
     */
    public static Property of(String id, String name, String dataType) {
        return new Property(id, name, dataType, null, null, false);
    }

    public static Property of(String id, String name, String dataType, String description) {
        return new Property(id, name, dataType, description, null, false);
    }

    /**
     * 检查是否为指定数据类型
     */
    public boolean isDataType(DataTypeEnum type) {
        return type.name().equalsIgnoreCase(dataType);
    }

    /**
     * 获取显示信息
     */
    public String getDisplayInfo() {
        return name + "(" + id + ") - " + dataType + (required ? " *" : "");
    }
}
