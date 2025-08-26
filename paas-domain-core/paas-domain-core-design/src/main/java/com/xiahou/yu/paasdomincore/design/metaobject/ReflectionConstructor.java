package com.xiahou.yu.paasdomincore.design.metaobject;

import java.lang.reflect.Constructor;

/**
 * 反射构造器封装类
 *
 * @author xiahou
 */
public class ReflectionConstructor {

    private final Constructor<?> constructor;

    public ReflectionConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Object newInstance(Object... args) throws Exception {
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(args);
        }
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }
}
