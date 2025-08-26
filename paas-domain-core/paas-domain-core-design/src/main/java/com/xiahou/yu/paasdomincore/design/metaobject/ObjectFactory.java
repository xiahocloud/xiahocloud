package com.xiahou.yu.paasdomincore.design.metaobject;

import java.util.List;
import java.util.Properties;

/**
 * 对象工厂接口
 * 用于创建对象实例
 *
 * @author xiahou
 */
public interface ObjectFactory {

    /**
     * 设置属性
     */
    default void setProperties(Properties properties) {
        // NOP
    }

    /**
     * 创建对象
     */
    <T> T create(Class<T> type);

    /**
     * 创建对象（带构造参数）
     */
    <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

    /**
     * 检查是否为集合类型
     */
    <T> boolean isCollection(Class<T> type);
}
