package com.xiahou.yu.paasmetacore.constant;

import java.util.Arrays;

/**
 * description: 限界上下文枚举
 *
 * @author wanghaoxin
 * date     2022/7/12 23:43
 * @version 1.0
 */
public enum ContextEnum {
    /**
     * 系统上下文
     */
    SYSTEM("System"),

    APP("App"),

    BOUNDED("Bounded"),

    AGGREGATE("Aggregate"),

    FORM("Entity");

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    ContextEnum(String key) {
        this.key = key;
    }

    public static ContextEnum getInstance(String key) {
        return Arrays.stream(values()).filter(item -> item.name().equals(key)).findFirst().orElse(null);
    }
}
