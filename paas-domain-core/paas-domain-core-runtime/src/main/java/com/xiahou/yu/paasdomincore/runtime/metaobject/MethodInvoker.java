package com.xiahou.yu.paasdomincore.runtime.metaobject;

import java.lang.reflect.Method;

/**
 * 方法调用器
 *
 * @author xiahou
 */
public class MethodInvoker implements Invoker {

    private final Class<?> type;
    private final Method method;

    public MethodInvoker(Method method) {
        this.method = method;

        if (method.getParameterTypes().length == 1) {
            type = method.getParameterTypes()[0];
        } else {
            type = method.getReturnType();
        }
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            if (!method.canAccess(target)) {
                method.setAccessible(true);
            }
            return method.invoke(target, args);
        }
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}


/**
 * 模糊方法调用器
 * 当存在歧义时抛出异常
 */
class AmbiguousMethodInvoker extends MethodInvoker {

    private final String exceptionMessage;

    public AmbiguousMethodInvoker(Method method, String exceptionMessage) {
        super(method);
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        throw new RuntimeException(exceptionMessage);
    }
}


/**
 * 字段获取调用器
 */
class GetFieldInvoker implements Invoker {

    private final java.lang.reflect.Field field;

    public GetFieldInvoker(java.lang.reflect.Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            if (!field.canAccess(target)) {
                field.setAccessible(true);
            }
            return field.get(target);
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}


/**
 * 字段设置调用器
 */
class SetFieldInvoker implements Invoker {

    private final java.lang.reflect.Field field;

    public SetFieldInvoker(java.lang.reflect.Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        try {
            field.set(target, args[0]);
        } catch (IllegalAccessException e) {
            if (!field.canAccess(target)) {
                field.setAccessible(true);
            }
            field.set(target, args[0]);
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
