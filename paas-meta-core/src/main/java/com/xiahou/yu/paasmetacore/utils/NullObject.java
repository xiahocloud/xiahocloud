package com.xiahou.yu.paasmetacore.utils;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 空对象
 *
 * @author wanghaoxin
 * date     2022/9/5 23:07
 * @version 1.0
 */
@Slf4j
public class NullObject {

    public static final String IS_NULL = "isNull";

    public static <T> T getNullObject(Class<T> clazz) {
        final T t;
        try {
            t = clazz.newInstance();
            if (t instanceof AbstractNull) {
                final Field isNullField = clazz.getSuperclass().getDeclaredField(IS_NULL);
                isNullField.setAccessible(true);
                isNullField.set(t, true);
            } else {
                Map<String, Object> props = new HashMap<>();
                props.put(IS_NULL, true);
                return getInstanceWithIsNullField(t, props);
            }
        } catch (InstantiationException | NoSuchFieldException | IllegalAccessException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage());
        }
        return t;
    }

    public static <T> boolean isNull(T t) {
        try {
            if (getFieldValue(t, IS_NULL, Boolean.class)) {
                return true;
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.info("当前类字段中不存在isNull属性。");
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage());
        }
        return false;
    }


    private static <T, E> E getFieldValue(T t, String fieldName, Class<E> fieldClazz) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        //通过反射获取值
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + fieldName.substring(1);
        Method method = t.getClass().getDeclaredMethod(getter);
        Object value = method.invoke(t);
        log.info("类型为：{}", fieldClazz);
        return (E) value;
    }


    private static <T> T getInstanceWithIsNullField(T instance, Map<String, Object> props) {
        try {
            instance = ReflectUtil.getObject(instance, props);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage());
        }
        return instance;
    }
}
