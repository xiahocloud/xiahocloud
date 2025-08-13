package com.xiahou.yu.paaswebserver.cqrs.command;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 命令基类
 */
public abstract class Command {
    private final String commandId;
    private final LocalDateTime timestamp;

    protected Command() {
        this.commandId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public String getCommandId() {
        return commandId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
