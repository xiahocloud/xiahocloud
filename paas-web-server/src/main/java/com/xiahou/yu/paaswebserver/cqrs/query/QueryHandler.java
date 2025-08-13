package com.xiahou.yu.paaswebserver.cqrs.query;

/**
 * 查询处理器接口
 */
public interface QueryHandler<T extends Query, R> {
    R handle(T query);
}
