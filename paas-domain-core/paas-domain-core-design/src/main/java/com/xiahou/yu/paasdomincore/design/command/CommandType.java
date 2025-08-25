package com.xiahou.yu.paasdomincore.design.command;

/**
 * 命令类型枚举
 * 定义支持的数据操作类型
 *
 * @author xiahou
 */
public enum CommandType {
    CREATE("CREATE", "创建"),
    UPDATE("UPDATE", "更新"),
    DELETE("DELETE", "删除"),
    QUERY("QUERY", "查询");

    private final String code;
    private final String description;

    CommandType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
