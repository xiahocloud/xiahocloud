package com.xiahou.yu.paasmetacore.utils;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.util.Map;

/**
 * description: 动态对象
 *
 * @author wanghaoxin
 * date     2022/9/6 10:25
 * @version 1.0
 */
public class DynamicObject<T> {
    private T target;

    private BeanMap beanMap;

    public DynamicObject(Class<T> superclass, Map<String, Class<?>> propertyMap) {
        this.target = generateBean(superclass, propertyMap);
        this.beanMap = BeanMap.create(this.target);
    }

    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

    public Object getValue(String property) {
        return beanMap.get(property);
    }

    public T getTarget() {
        return this.target;
    }

    /**
     * 根据属性生成对象
     */
    private T generateBean(Class<T> superclass, Map<String, Class<?>> propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        if (null != superclass) {
            generator.setSuperclass(superclass);
        }
        BeanGenerator.addProperties(generator, propertyMap);
        return (T) generator.create();
    }
}
