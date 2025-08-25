package com.xiahou.yu.paasdomincore.runtime.metaobject;

/**
 * 默认对象包装器工厂实现
 *
 * @author xiahou
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

    private static final DefaultObjectWrapperFactory INSTANCE = new DefaultObjectWrapperFactory();

    public static DefaultObjectWrapperFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        throw new RuntimeException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
