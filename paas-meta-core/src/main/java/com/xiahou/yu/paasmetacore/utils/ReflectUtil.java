package com.xiahou.yu.paasmetacore.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2022/9/6 10:27
 * @version 1.0
 */
@Slf4j
public class ReflectUtil<T> {
    public static <T> T getObject(T dest, Map<String, Object> newValueMap) throws InvocationTargetException, IllegalAccessException {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

        //1.获取原对象的字段数组
        PropertyDescriptor[] descriptorArr = propertyUtilsBean.getPropertyDescriptors(dest);

        //2.遍历原对象的字段数组，并将其封装到Map
        Map<String, Class<?>> oldKeyMap = new HashMap<>(4);
        for (PropertyDescriptor it : descriptorArr) {
            if (!"class".equalsIgnoreCase(it.getName())) {
                oldKeyMap.put(it.getName(), it.getPropertyType());
                newValueMap.put(it.getName(), it.getReadMethod().invoke(dest));
            }
        }

        //3.将扩展字段Map合并到原字段Map中
        newValueMap.forEach((k, v) -> {
            if(v == null) {
                // 只能复制值非空的字段
            } else {
                oldKeyMap.put(k, v.getClass());
            }
        });

        //4.根据新的字段组合生成子类对象
        DynamicObject<T> dynamicObject = new DynamicObject(dest.getClass(), oldKeyMap);

        //5.放回合并后的属性集合
        newValueMap.forEach((k, v) -> {
            try {
                dynamicObject.setValue(k, v);
            } catch (Exception e) {
                log.error("动态添加字段【值】出错", e);
            }
        });
        return dynamicObject.getTarget();
    }
}
