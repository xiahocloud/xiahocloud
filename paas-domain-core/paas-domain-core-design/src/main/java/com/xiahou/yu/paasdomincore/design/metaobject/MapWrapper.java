package com.xiahou.yu.paasdomincore.design.metaobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map对象包装器
 * 为Map对象提供统一的属性访问接口
 *
 * @author xiahou
 */
public class MapWrapper implements ObjectWrapper {

    private final MetaObject metaObject;
    private final Map<String, Object> map;

    public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
        this.metaObject = metaObject;
        this.map = map;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        if (prop.getIndex() != null) {
            Object collection = map.get(prop.getName());
            return getCollectionValue(prop, collection);
        } else {
            return map.get(prop.getName());
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.getIndex() != null) {
            Object collection = map.get(prop.getName());
            setCollectionValue(prop, collection, value);
        } else {
            map.put(prop.getName(), value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = MetaObject.forObject(map.get(prop.getName()));
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = MetaObject.forObject(map.get(prop.getName()));
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return Object.class;
            } else {
                return metaValue.getGetterType(prop.getChildren());
            }
        } else {
            if (map.get(name) != null) {
                return map.get(name).getClass();
            } else {
                return Object.class;
            }
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (map.containsKey(prop.getName())) {
                MetaObject metaValue = MetaObject.forObject(map.get(prop.getIndexedName()));
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return true;
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return map.containsKey(prop.getName());
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        HashMap<String, Object> newMap = new HashMap<>();
        set(prop, newMap);
        return MetaObject.forObject(newMap, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory());
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }

    private Object getCollectionValue(PropertyTokenizer prop, Object collection) {
        if (collection instanceof Map) {
            return ((Map) collection).get(prop.getIndex());
        } else if (collection instanceof List) {
            int i = Integer.parseInt(prop.getIndex());
            return ((List) collection).get(i);
        } else if (collection instanceof Object[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((Object[]) collection)[i];
        } else if (collection instanceof char[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((char[]) collection)[i];
        } else if (collection instanceof boolean[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((boolean[]) collection)[i];
        } else if (collection instanceof byte[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((byte[]) collection)[i];
        } else if (collection instanceof double[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((double[]) collection)[i];
        } else if (collection instanceof float[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((float[]) collection)[i];
        } else if (collection instanceof int[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((int[]) collection)[i];
        } else if (collection instanceof long[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((long[]) collection)[i];
        } else if (collection instanceof short[]) {
            int i = Integer.parseInt(prop.getIndex());
            return ((short[]) collection)[i];
        } else {
            throw new RuntimeException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
        }
    }

    @SuppressWarnings("unchecked")
    private void setCollectionValue(PropertyTokenizer prop, Object collection, Object value) {
        if (collection instanceof Map) {
            ((Map) collection).put(prop.getIndex(), value);
        } else if (collection instanceof List) {
            int i = Integer.parseInt(prop.getIndex());
            ((List) collection).set(i, value);
        } else if (collection instanceof Object[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((Object[]) collection)[i] = value;
        } else if (collection instanceof char[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((char[]) collection)[i] = (Character) value;
        } else if (collection instanceof boolean[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((boolean[]) collection)[i] = (Boolean) value;
        } else if (collection instanceof byte[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((byte[]) collection)[i] = (Byte) value;
        } else if (collection instanceof double[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((double[]) collection)[i] = (Double) value;
        } else if (collection instanceof float[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((float[]) collection)[i] = (Float) value;
        } else if (collection instanceof int[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((int[]) collection)[i] = (Integer) value;
        } else if (collection instanceof long[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((long[]) collection)[i] = (Long) value;
        } else if (collection instanceof short[]) {
            int i = Integer.parseInt(prop.getIndex());
            ((short[]) collection)[i] = (Short) value;
        } else {
            throw new RuntimeException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
        }
    }
}
