package com.xiahou.yu.paasmetacore.factory;

import com.xiahou.yu.paasmetacore.models.contextmodel.Context;

/**
 * description: 上下文工厂
 *
 * @author wanghaoxin
 * date     2022/7/12 23:54
 * @version 1.0
 */
public class ContextFactory {

    private static final String DEFAULT_PACKAGE = "com.xiahou.yu.passmetacore.model.contextmodel.context";
    private static final String CONTEXT_SUFFIX = "Context";

    public static Context build(String classNameKey) throws Exception {
        return build(DEFAULT_PACKAGE, classNameKey);
    }

    public static Context build(String packageName, String classNameKey) throws Exception {
        if (packageName == null) {
            packageName = DEFAULT_PACKAGE;
        }
        String clazzName = packageName + "." + classNameKey + CONTEXT_SUFFIX;

        Class<?> clazz = Class.forName(clazzName);
        Object o = clazz.newInstance();
        return (Context) o;
    }

}
