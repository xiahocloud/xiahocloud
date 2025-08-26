package com.xiahou.yu.paasdomincore.design.metaobject;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 元类，提供类的反射信息缓存
 *
 * @author xiahou
 */
@Slf4j
public class MetaClass {

    private static final Map<Class<?>, MetaClass> CLASS_CACHE = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Invoker> getMap = new HashMap<>();
    private final Map<String, Invoker> setMap = new HashMap<>();
    private final Map<String, Class<?>> getTypes = new HashMap<>();
    private final Map<String, Class<?>> setTypes = new HashMap<>();
    private final ReflectionConstructor defaultConstructor;

    private MetaClass(Class<?> type) {
        this.type = type;
        this.defaultConstructor = getDefaultConstructor(type);
        addGetMethods(type);
        addSetMethods(type);
        addFields(type);
    }

    public static MetaClass forClass(Class<?> type) {
        return CLASS_CACHE.computeIfAbsent(type, MetaClass::new);
    }

    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping) {
            name = name.replace("_", "");
        }
        return findProperty(name);
    }

    public String findProperty(String name) {
        StringBuilder prop = buildProperty(name, new StringBuilder());
        return prop.length() > 0 ? prop.toString() : null;
    }

    public String[] getGetterNames() {
        return getMap.keySet().toArray(new String[0]);
    }

    public String[] getSetterNames() {
        return setMap.keySet().toArray(new String[0]);
    }

    public Class<?> getSetterType(String name) {
        return setTypes.get(name);
    }

    public Class<?> getGetterType(String name) {
        return getTypes.get(name);
    }

    public ReflectionConstructor getDefaultConstructor() {
        return defaultConstructor;
    }

    public boolean hasDefaultConstructor() {
        return defaultConstructor != null;
    }

    public Invoker getGetInvoker(String name) {
        Invoker method = getMap.get(name);
        if (method == null) {
            throw new RuntimeException("There is no getter for property named '" + name + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getSetInvoker(String name) {
        Invoker method = setMap.get(name);
        if (method == null) {
            throw new RuntimeException("There is no setter for property named '" + name + "' in '" + type + "'");
        }
        return method;
    }

    public boolean hasSetter(String name) {
        return setMap.containsKey(name);
    }

    public boolean hasGetter(String name) {
        return getMap.containsKey(name);
    }

    public Object getValue(Object object, String name) {
        try {
            Invoker invoker = getGetInvoker(name);
            return invoker.invoke(object, new Object[0]);
        } catch (Exception e) {
            throw new RuntimeException("Could not get property '" + name + "' from " + object.getClass() + ".  Cause: " + e.toString(), e);
        }
    }

    private StringBuilder buildProperty(String name, StringBuilder builder) {
        if (hasGetter(name)) {
            builder.append(name);
        } else {
            final String camelName = name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
            if (hasGetter(camelName)) {
                builder.append(camelName);
            }
        }
        return builder;
    }

    private void addGetMethods(Class<?> clazz) {
        Map<String, List<Method>> conflictingGetters = new HashMap<>();
        Method[] methods = getClassMethods(clazz);
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0 && method.getName().length() > 3) {
                String name;
                if (method.getName().startsWith("get") && Character.isUpperCase(method.getName().charAt(3))) {
                    name = dropCase(method.getName().substring(3));
                } else if (method.getName().startsWith("is") && Character.isUpperCase(method.getName().charAt(2))) {
                    name = dropCase(method.getName().substring(2));
                } else {
                    continue;
                }
                addMethodConflict(conflictingGetters, name, method);
            }
        }
        resolveGetterConflicts(conflictingGetters);
    }

    private void addSetMethods(Class<?> clazz) {
        Map<String, List<Method>> conflictingSetters = new HashMap<>();
        Method[] methods = getClassMethods(clazz);
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && name.length() > 3) {
                if (method.getParameterTypes().length == 1) {
                    name = dropCase(name.substring(3));
                    addMethodConflict(conflictingSetters, name, method);
                }
            }
        }
        resolveSetterConflicts(conflictingSetters);
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                if (!setMap.containsKey(field.getName())) {
                    addSetField(field);
                }
                if (!getMap.containsKey(field.getName())) {
                    addGetField(field);
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            addFields(clazz.getSuperclass());
        }
    }

    private void addSetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            setMap.put(field.getName(), new SetFieldInvoker(field));
            setTypes.put(field.getName(), field.getType());
        }
    }

    private void addGetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            getMap.put(field.getName(), new GetFieldInvoker(field));
            getTypes.put(field.getName(), field.getType());
        }
    }

    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    private Method[] getClassMethods(Class<?> clazz) {
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            currentClass = currentClass.getSuperclass();
        }
        return uniqueMethods.values().toArray(new Method[0]);
    }

    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            if (!currentMethod.isBridge()) {
                String signature = getSignature(currentMethod);
                if (!uniqueMethods.containsKey(signature)) {
                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    private String getSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        sb.append(returnType.getName()).append('#');
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            sb.append(i == 0 ? ':' : ',');
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }

    private static String dropCase(String name) {
        if (name.startsWith("_")) {
            name = name.substring(1);
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }

    private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
        if (isValidPropertyName(name)) {
            List<Method> list = conflictingMethods.computeIfAbsent(name, k -> new ArrayList<>());
            list.add(method);
        }
    }

    private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
        for (Map.Entry<String, List<Method>> entry : conflictingGetters.entrySet()) {
            Method winner = null;
            String propName = entry.getKey();
            boolean isAmbiguous = false;
            for (Method candidate : entry.getValue()) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }
                Class<?> winnerType = winner.getReturnType();
                Class<?> candidateType = candidate.getReturnType();
                if (candidateType.equals(winnerType)) {
                    if (!boolean.class.equals(candidateType)) {
                        isAmbiguous = true;
                        break;
                    } else if (candidate.getName().startsWith("is")) {
                        winner = candidate;
                    }
                } else if (candidateType.isAssignableFrom(winnerType)) {
                    // OK getter type is descendant
                } else if (winnerType.isAssignableFrom(candidateType)) {
                    winner = candidate;
                } else {
                    isAmbiguous = true;
                    break;
                }
            }
            addGetMethod(propName, winner, isAmbiguous);
        }
    }

    private void addGetMethod(String name, Method method, boolean isAmbiguous) {
        MethodInvoker invoker = isAmbiguous
                ? new AmbiguousMethodInvoker(method, "Illegal overloaded getter method with ambiguous type for property "
                + name + " in class " + method.getDeclaringClass()
                + ". This breaks the JavaBeans specification and can cause unpredictable results.")
                : new MethodInvoker(method);
        getMap.put(name, invoker);
        getTypes.put(name, method.getReturnType());
    }

    private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
        for (String propName : conflictingSetters.keySet()) {
            List<Method> setters = conflictingSetters.get(propName);
            Class<?> getterType = getTypes.get(propName);
            boolean isGetterAmbiguous = getMap.get(propName) instanceof AmbiguousMethodInvoker;
            boolean isSetterAmbiguous = false;
            Method match = null;
            for (Method setter : setters) {
                if (!isGetterAmbiguous && setter.getParameterTypes()[0].equals(getterType)) {
                    match = setter;
                    break;
                }
                if (!isSetterAmbiguous) {
                    match = pickBetterSetter(match, setter, propName);
                    isSetterAmbiguous = match == null;
                }
            }
            if (match != null) {
                addSetMethod(propName, match);
            }
        }
    }

    private Method pickBetterSetter(Method setter1, Method setter2, String property) {
        if (setter1 == null) {
            return setter2;
        }
        Class<?> paramType1 = setter1.getParameterTypes()[0];
        Class<?> paramType2 = setter2.getParameterTypes()[0];
        if (paramType1.isAssignableFrom(paramType2)) {
            return setter2;
        } else if (paramType2.isAssignableFrom(paramType1)) {
            return setter1;
        }
        MethodInvoker invoker = new AmbiguousMethodInvoker(setter1,
                "Ambiguous setters defined for property '" + property + "' in class '"
                        + setter2.getDeclaringClass() + "' with types '" + paramType1.getName() + "' and '"
                        + paramType2.getName() + "'.");
        setMap.put(property, invoker);
        setTypes.put(property, paramType1);
        return null;
    }

    private void addSetMethod(String name, Method method) {
        MethodInvoker invoker = new MethodInvoker(method);
        setMap.put(name, invoker);
        setTypes.put(name, method.getParameterTypes()[0]);
    }

    private ReflectionConstructor getDefaultConstructor(Class<?> type) {
        ReflectionConstructor constructor = null;
        try {
            java.lang.reflect.Constructor<?> javaConstructor = type.getDeclaredConstructor();
            constructor = new ReflectionConstructor(javaConstructor);
        } catch (NoSuchMethodException ignored) {
            // ignored
        }
        return constructor;
    }
}
