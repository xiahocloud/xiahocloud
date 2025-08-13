package com.xiahou.yu.paasmetacore.constant;

import lombok.Getter;

/**
 * 数据类型枚举
 * @author paas-meta-core
 */
@Getter
public enum DataTypeEnum {
    STRING("String", "文本"),
    INTEGER("Integer", "整数"),
    LONG("Long", "长整数"),
    DOUBLE("Double", "双精度浮点数"),
    BOOLEAN("Boolean", "布尔值"),
    DATE("Date", "日期"),
    DATETIME("DateTime", "日期时间"),
    TEXT("Text", "文本"),
    JSON("JSON", "JSON对象"),
    DECIMAL("Decimal", "小数");

    private final String code;
    private final String description;

    DataTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举值
     */
    public static DataTypeEnum fromCode(String code) {
        for (DataTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        // 默认返回字符串类型
        return STRING;
    }

    /**
     * 检查是否为数值类型
     */
    public boolean isNumeric() {
        return this == INTEGER || this == LONG || this == DOUBLE || this == DECIMAL;
    }

    /**
     * 检查是否为日期类型
     */
    public boolean isDate() {
        return this == DATE || this == DATETIME;
    }
}
