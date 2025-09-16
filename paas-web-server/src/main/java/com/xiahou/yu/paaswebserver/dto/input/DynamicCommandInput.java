package com.xiahou.yu.paaswebserver.dto.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.paasdomincore.design.filter.Filter;
import com.xiahou.yu.paasinfracommon.context.RequestContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 动态命令输入DTO
 * 系统级参数(system, module, context, app, aggr)已移至HTTP头传递
 */
@Data
@Slf4j
public class DynamicCommandInput {

    private RequestContext requestContext;

    /**
     * 实体标识
     */
    private String entityName;

    /**
     * 操作类型: CREATE, UPDATE, DELETE
     */
    private String operation;

    /**
     * 数据内容
     */
    private Map<String, Object> data;

    /**
     * 查询/更新/删除条件 - 使用统一的Filter
     * -- SETTER --
     *  设置过滤条件

     */
    private Filter filter;

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
            log.warn("Failed to serialize DynamicCommandInput to JSON", e);
            return super.toString();
        }
    }
}
