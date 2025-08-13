package com.xiahou.yu.paaswebserver.cqrs.query;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 查询基类
 */
public abstract class Query {
    private final String queryId;
    private final LocalDateTime timestamp;

    protected Query() {
        this.queryId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public String getQueryId() {
        return queryId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
