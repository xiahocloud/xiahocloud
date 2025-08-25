package com.xiahou.yu.paasdomincore.runtime.metaobject;

import java.util.*;

/**
 * 默认对象工厂实现
 *
 * @author xiahou
 */
public class DefaultObjectFactory implements ObjectFactory {

    private static final DefaultObjectFactory INSTANCE = new DefaultObjectFactory();

    public static DefaultObjectFactory getInstance() {
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type) {
        return create(type, null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        Class<?> classToCreate = resolveInterface(type);
        return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }

    protected Class<?> resolveInterface(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class || type == Iterable.class) {
            classToCreate = ArrayList.class;
        } else if (type == Map.class) {
            classToCreate = HashMap.class;
        } else if (type == SortedSet.class) {
            classToCreate = TreeSet.class;
        } else if (type == Set.class) {
            classToCreate = HashSet.class;
        } else {
            classToCreate = type;
        }
        return classToCreate;
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        try {
            if (constructorArgTypes == null || constructorArgs == null) {
                return type.getDeclaredConstructor().newInstance();
            } else {
                return type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[0]))
                        .newInstance(constructorArgs.toArray());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating " + type + " with invalid types (" + argTypes(constructorArgTypes) +
                    ") or values (" + argValues(constructorArgs) + "). Cause: " + e, e);
        }
    }

    private String argTypes(List<Class<?>> argTypes) {
        StringBuilder argSb = new StringBuilder();
        if (argTypes != null) {
            for (Class<?> argType : argTypes) {
                argSb.append(argType == null ? "null" : argType.getSimpleName());
                argSb.append(",");
            }
            if (argSb.length() > 0) {
                argSb.setLength(argSb.length() - 1);
            }
        }
        return argSb.toString();
    }

    private String argValues(List<Object> argValues) {
        StringBuilder argSb = new StringBuilder();
        if (argValues != null) {
            for (Object argValue : argValues) {
                argSb.append(String.valueOf(argValue));
                argSb.append(",");
            }
            if (argSb.length() > 0) {
                argSb.setLength(argSb.length() - 1);
            }
        }
        return argSb.toString();
    }
}
