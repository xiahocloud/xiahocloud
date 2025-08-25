package com.xiahou.yu.paasdomincore.runtime.metaobject;

/**
 * 对象包装器接口
 * 提供对对象属性的统一访问
 *
 * @author xiahou
 */
public interface ObjectWrapper {

    /**
     * 获取属性值
     */
    Object get(PropertyTokenizer prop);

    /**
     * 设置属性值
     */
    void set(PropertyTokenizer prop, Object value);

    /**
     * 查找属性名
     */
    String findProperty(String name, boolean useCamelCaseMapping);

    /**
     * 获取getter属性名
     */
    String[] getGetterNames();

    /**
     * 获取setter属性名
     */
    String[] getSetterNames();

    /**
     * 获取setter属性类型
     */
    Class<?> getSetterType(String name);

    /**
     * 获取getter属性类型
     */
    Class<?> getGetterType(String name);

    /**
     * 是否有setter
     */
    boolean hasSetter(String name);

    /**
     * 是否有getter
     */
    boolean hasGetter(String name);

    /**
     * 实例化属性值
     */
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    /**
     * 是否为集合
     */
    boolean isCollection();

    /**
     * 添加元素到集合
     */
    void add(Object element);

    /**
     * 添加所有元素到集合
     */
    <E> void addAll(java.util.List<E> element);
}
