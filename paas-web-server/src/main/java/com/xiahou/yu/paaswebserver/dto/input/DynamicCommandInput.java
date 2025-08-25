package com.xiahou.yu.paaswebserver.dto.input;

import lombok.Data;
import java.util.Map;

/**
 * 动态命令输入DTO
 * 系统级参数(system, module, context, app, aggr)已移至HTTP头传递
 */
@Data
public class DynamicCommandInput {

    /**
     * 实体标识
     */
    private String entity;

    /**
     * 操作类型: CREATE, UPDATE, DELETE
     */
    private String operation;

    /**
     * 数据内容
     */
    private Map<String, Object> data;

    /**
     * 查询/更新/删除条件
     */
    private Map<String, Object> conditions;
}
