package com.xiahou.yu.paasdomincore.runtime.metaobject;

/**
 * 对象包装器工厂接口
 *
 * @author xiahou
 */
public interface ObjectWrapperFactory {

    /**
     * 检查是否有对应的包装器
     */
    boolean hasWrapperFor(Object object);

    /**
     * 获取对象的包装器
     */
    ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);
}
