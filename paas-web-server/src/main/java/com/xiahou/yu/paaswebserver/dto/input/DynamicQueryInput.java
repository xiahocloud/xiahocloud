package com.xiahou.yu.paaswebserver.dto.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.paasdomincore.design.filter.Filter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 动态查询输入DTO
 * 系统级参数(system, module, context, app, aggr)已移至HTTP头传递
 */
@Data
@Slf4j
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
    private String entityName;

    /**
     * 过滤条件 - 使用统一的Filter对象
     * -- SETTER --
     *  设置过滤条件

     */
    private Filter filter;

    /**
     * 需要查询的字段列表
     */
    private List<String> fields;

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

    /**
     * 获取有效的过滤条件
     */
    public Filter getEffectiveFilter() {
        return filter != null ? filter : Filter.empty();
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String toString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize DynamicQueryInput to JSON", e);
            return super.toString();
        }
    }
}
