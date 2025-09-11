package com.xiahou.yu.paasdomincore.design.command;

import com.xiahou.yu.paasdomincore.design.filter.Filter;
import lombok.Data;
import lombok.Builder;
import java.util.Map;
import java.util.HashMap;

/**
 * 命令上下文
 * 用于在命令执行过程中传递数据和状态
 * 系统级参数(system, module, context, app, aggr)已移至HTTP头传递
 *
 * @author xiahou
 */
@Data
@Builder
public class CommandContext {

    /**
     * 实体标识
     */
    private String entityName;

    /**
     * 数据载荷
     */
    private Map<String, Object> data;

    /**
     * 查询/更新/删除条件 - 使用统一的Filter
     * -- SETTER --
     *  设置过滤条件

     */
    private Filter filter;

    /**
     * 扩展属性，用于插件传递自定义数据
     */
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 添加扩展属性
     */
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.attributes.get(key);
    }

    /**
     * 获取有效的过滤条件
     */
    public Filter getEffectiveFilter() {
        return filter != null ? filter : Filter.empty();
    }
}

