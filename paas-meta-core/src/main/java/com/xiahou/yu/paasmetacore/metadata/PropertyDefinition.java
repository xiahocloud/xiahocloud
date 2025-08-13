package com.xiahou.yu.paasmetacore.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性定义
 * 对应 XML 中的 Property 标签
 * 根据文档设计，属性定义与引用是分离的
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PropertyDefinition {

    /**
     * 属性唯一标识，用于在Ref中引用
     */
    private String id;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性描述
     */
    private String description;

    /**
     * Java数据类型
     */
    private String dataType;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 作用域，表示该属性主要适用于哪种类型的模型或组件
     * 如：PageModel, DataModel, FieldModel等
     */
    private String scope;

    /**
     * 数据库字段长度（用于生成DDL时推导）
     */
    private Integer length;

    /**
     * 是否可为空（用于生成DDL时推导）
     */
    private Boolean nullable;

    /**
     * 是否为主键（用于生成DDL时推导）
     */
    private Boolean primaryKey;

    /**
     * 是否为索引（用于生成DDL时推导）
     */
    private Boolean indexed;

    /**
     * 获取数据库列名
     * 规则：属性ID转snake_case
     */
    public String getColumnName() {
        return camelToSnakeCase(id);
    }

    /**
     * 获取数据库字段类型（根据DataType推导）
     */
    public String getDbType() {
        return mapJavaTypeToDbType(dataType);
    }

    /**
     * 将驼峰命名转换为下划线命名
     */
    private String camelToSnakeCase(String camelCase) {
        if (camelCase == null) return null;
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 映射Java类型到数据库类型（默认MySQL）
     */
    private String mapJavaTypeToDbType(String javaType) {
        if (javaType == null) return "VARCHAR(255)";

        return switch (javaType) {
            case "String" -> length != null ? "VARCHAR(" + length + ")" : "VARCHAR(255)";
            case "Integer" -> "INT";
            case "Long" -> "BIGINT";
            case "Boolean" -> "TINYINT(1)";
            case "Double" -> "DOUBLE";
            case "Float" -> "FLOAT";
            case "BigDecimal" -> "DECIMAL(19,2)";
            case "Date", "LocalDateTime" -> "DATETIME";
            case "LocalDate" -> "DATE";
            default -> "VARCHAR(255)";
        };
    }

    /**
     * 获取Java类型的完整类名
     */
    public String getJavaTypeFullName() {
        if (dataType == null) return "java.lang.String";

        return switch (dataType) {
            case "String" -> "java.lang.String";
            case "Integer" -> "java.lang.Integer";
            case "Long" -> "java.lang.Long";
            case "Boolean" -> "java.lang.Boolean";
            case "Double" -> "java.lang.Double";
            case "Float" -> "java.lang.Float";
            case "BigDecimal" -> "java.math.BigDecimal";
            case "Date" -> "java.util.Date";
            case "LocalDateTime" -> "java.time.LocalDateTime";
            case "LocalDate" -> "java.time.LocalDate";
            default -> dataType;
        };
    }

    /**
     * 判断属性是否适用于指定的作用域
     */
    public boolean isApplicableToScope(String targetScope) {
        if (scope == null) return true; // 无作用域限制的属性适用于所有模型
        return scope.equals(targetScope);
    }
}
