package com.xiahou.yu.paaswebserver.dto.input;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 动态查询输入DTO
 * 系统级参数(system, module, context, app, aggr)已移至HTTP头传递
 */
@Data
public class DynamicQueryInput {
    // 移除了系统级参数，这些现在通过HTTP头传递：
    // - X-System (system)
    // - X-Module (module)
    // - X-Context (context)
    // - X-App (app)
    // - X-Aggr (aggr)

    /**
     * 实体标识
     */
    private String entity;

    /**
     * 过滤条件表达式
     */
    private String filter;

    /**
     * 需要查询的字段列表
     */
    private List<String> fields;

    /**
     * 过滤参数
     */
    private Map<String, Object> filterParams;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 页大小
     */
    private Integer size;

    /**
     * 排序字段
     */
    private List<String> orderBy;
}
