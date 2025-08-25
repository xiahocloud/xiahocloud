package com.xiahou.yu.paasdomincore.runtime.metaobject;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 元对象，提供对对象属性的统一访问接口
 * 参考 MyBatis MetaObject 的设计理念
 *
 * @author xiahou
 */
@Slf4j
public class MetaObject {

    private final Object originalObject;
    private final ObjectWrapper objectWrapper;
    private final ObjectFactory objectFactory;
    private final ObjectWrapperFactory objectWrapperFactory;

    private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;

        if (object instanceof ObjectWrapper) {
            this.objectWrapper = (ObjectWrapper) object;
        } else if (objectWrapperFactory.hasWrapperFor(object)) {
            this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
        } else if (object instanceof Map) {
            this.objectWrapper = new MapWrapper(this, (Map<String, Object>) object);
        } else if (object instanceof Collection) {
            this.objectWrapper = new CollectionWrapper(this, (Collection<Object>) object);
        } else {
            this.objectWrapper = new BeanWrapper(this, object);
        }
    }

    /**
     * 为对象创建MetaObject
     */
    public static MetaObject forObject(Object object) {
        return forObject(object, DefaultObjectFactory.getInstance(), DefaultObjectWrapperFactory.getInstance());
    }

    /**
     * 为对象创建MetaObject（带自定义工厂）
     */
    public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory) {
        if (object == null) {
            return SystemMetaObject.NULL_META_OBJECT;
        } else {
            return new MetaObject(object, objectFactory, objectWrapperFactory);
        }
    }

    /**
     * 获取属性值
     */
    public Object getValue(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return null;
            } else {
                return metaValue.getValue(prop.getChildren());
            }
        } else {
            return objectWrapper.get(prop);
        }
    }

    /**
     * 设置属性值
     */
    public void setValue(String name, Object value) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                if (value == null) {
                    return;
                } else {
                    metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
                }
            }
            metaValue.setValue(prop.getChildren(), value);
        } else {
            objectWrapper.set(prop, value);
        }
    }

    /**
     * 查找属性名
     */
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return objectWrapper.findProperty(name, useCamelCaseMapping);
    }

    /**
     * 获取getter属性名
     */
    public String[] getGetterNames() {
        return objectWrapper.getGetterNames();
    }

    /**
     * 获取setter属性名
     */
    public String[] getSetterNames() {
        return objectWrapper.getSetterNames();
    }

    /**
     * 检查是否有setter
     */
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (objectWrapper.hasSetter(prop.getName())) {
                MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasSetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return objectWrapper.hasSetter(prop.getName());
        }
    }

    /**
     * 检查是否有getter
     */
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (objectWrapper.hasGetter(prop.getName())) {
                MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return objectWrapper.hasGetter(prop.getName());
        }
    }

    /**
     * 获取原始对象
     */
    public Object getOriginalObject() {
        return originalObject;
    }

    /**
     * 检查原始对象是否为null
     */
    public boolean isNull() {
        return originalObject == null;
    }

    /**
     * 转换为Map
     */
    public Map<String, Object> toMap() {
        if (originalObject instanceof Map) {
            return (Map<String, Object>) originalObject;
        }

        Map<String, Object> result = new HashMap<>();
        String[] getterNames = getGetterNames();
        for (String name : getterNames) {
            Object value = getValue(name);
            result.put(name, value);
        }
        return result;
    }

    /**
     * 从Map创建MetaObject
     */
    public static MetaObject fromMap(Map<String, Object> map) {
        return forObject(map);
    }

    /**
     * 创建空的MetaObject
     */
    public static MetaObject empty() {
        return forObject(new HashMap<>());
    }

    /**
     * 为属性创建MetaObject
     */
    private MetaObject metaObjectForProperty(String name) {
        Object value = getValue(name);
        return MetaObject.forObject(value, objectFactory, objectWrapperFactory);
    }

    /**
     * 获取对象工厂
     */
    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    /**
     * 获取对象包装器工厂
     */
    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    /**
     * 获取属性类型（setter）
     */
    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    /**
     * 获取属性类型（getter）
     */
    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }
}
