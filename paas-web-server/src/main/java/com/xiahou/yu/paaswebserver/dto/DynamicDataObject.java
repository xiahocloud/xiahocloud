package com.xiahou.yu.paaswebserver.dto;

import com.xiahou.yu.paasdomincore.runtime.metaobject.MetaObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据传输对象
 * 使用MetaObject提供优雅的数据访问接口，替代Map<String, Object>
 *
 * @author xiahou
 */
@Slf4j
public class DynamicDataObject {

    private final MetaObject metaObject;

    private DynamicDataObject(Object data) {
        this.metaObject = MetaObject.forObject(data);
    }

    /**
     * 从Map创建动态数据对象
     */
    public static DynamicDataObject fromMap(Map<String, Object> map) {
        return new DynamicDataObject(map != null ? map : new HashMap<>());
    }

    /**
     * 从任意对象创建动态数据对象
     */
    public static DynamicDataObject fromObject(Object object) {
        return new DynamicDataObject(object);
    }

    /**
     * 创建空的动态数据对象
     */
    public static DynamicDataObject empty() {
        return new DynamicDataObject(new HashMap<>());
    }

    /**
     * 获取属性值（支持嵌套属性，如 "user.name"）
     */
    public Object getValue(String property) {
        try {
            return metaObject.getValue(property);
        } catch (Exception e) {
            log.warn("Failed to get property '{}': {}", property, e.getMessage());
            return null;
        }
    }

    /**
     * 获取字符串类型属性值
     */
    public String getString(String property) {
        Object value = getValue(property);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取整数类型属性值
     */
    public Integer getInteger(String property) {
        Object value = getValue(property);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Cannot convert '{}' to Integer for property '{}'", value, property);
            return null;
        }
    }

    /**
     * 获取长整数类型属性值
     */
    public Long getLong(String property) {
        Object value = getValue(property);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Cannot convert '{}' to Long for property '{}'", value, property);
            return null;
        }
    }

    /**
     * 获取布尔类型属性值
     */
    public Boolean getBoolean(String property) {
        Object value = getValue(property);
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.valueOf(value.toString());
    }

    /**
     * 设置属性值（支持嵌套属性）
     */
    public DynamicDataObject setValue(String property, Object value) {
        try {
            metaObject.setValue(property, value);
        } catch (Exception e) {
            log.warn("Failed to set property '{}' to '{}': {}", property, value, e.getMessage());
        }
        return this;
    }

    /**
     * 检查是否包含指定属性
     */
    public boolean hasProperty(String property) {
        return metaObject.hasGetter(property);
    }

    /**
     * 检查属性值是否为null
     */
    public boolean isNull(String property) {
        return getValue(property) == null;
    }

    /**
     * 检查属性值是否不为null
     */
    public boolean isNotNull(String property) {
        return getValue(property) != null;
    }

    /**
     * 获取所有属性名
     */
    public String[] getPropertyNames() {
        return metaObject.getGetterNames();
    }

    /**
     * 转换为Map
     */
    public Map<String, Object> toMap() {
        return metaObject.toMap();
    }

    /**
     * 获取原始对象
     */
    public Object getOriginalObject() {
        return metaObject.getOriginalObject();
    }

    /**
     * 检查是否为空对象
     */
    public boolean isEmpty() {
        String[] properties = getPropertyNames();
        if (properties.length == 0) return true;

        for (String property : properties) {
            if (isNotNull(property)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建副本
     */
    public DynamicDataObject copy() {
        Map<String, Object> copyMap = new HashMap<>(toMap());
        return fromMap(copyMap);
    }

    /**
     * 合并另一个动态数据对象
     */
    public DynamicDataObject merge(DynamicDataObject other) {
        if (other != null) {
            Map<String, Object> otherMap = other.toMap();
            for (Map.Entry<String, Object> entry : otherMap.entrySet()) {
                setValue(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "DynamicDataObject{" + toMap() + '}';
    }
}
