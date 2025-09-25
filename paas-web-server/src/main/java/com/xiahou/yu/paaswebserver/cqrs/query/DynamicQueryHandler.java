package com.xiahou.yu.paaswebserver.cqrs.query;

import com.xiahou.yu.paaswebserver.dto.DynamicQueryResponse;
import com.xiahou.yu.paaswebserver.dto.input.DynamicQueryInput;

/**
 * 动态查询处理器接口
 * 定义处理动态查询的标准接口
 *
 * @author xiahou
 */
public interface DynamicQueryHandler {

    /**
     * 处理动态查询
     * @param input 查询输入
     * @return 查询响应
     */
    DynamicQueryResponse handle(DynamicQueryInput input);
}