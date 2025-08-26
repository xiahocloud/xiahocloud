package com.xiahou.yu.paasdomincore.design.metaobject;

/**
 * 调用器接口
 * 用于统一方法和字段的调用
 *
 * @author xiahou
 */
public interface Invoker {

    /**
     * 执行调用
     */
    Object invoke(Object target, Object[] args) throws Exception;

    /**
     * 获取类型
     */
    Class<?> getType();
}
