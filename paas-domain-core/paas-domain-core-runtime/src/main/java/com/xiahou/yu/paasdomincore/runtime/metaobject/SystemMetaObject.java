package com.xiahou.yu.paasdomincore.runtime.metaobject;

/**
 * 系统元对象
 * 提供NULL对象和系统级MetaObject
 *
 * @author xiahou
 */
public final class SystemMetaObject {

    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = DefaultObjectFactory.getInstance();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = DefaultObjectWrapperFactory.getInstance();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(new NullObject(), DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);

    private SystemMetaObject() {
        // Prevent Instantiation of Static Class
    }

    /**
     * NULL对象类
     */
    private static class NullObject {
    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
    }
}
