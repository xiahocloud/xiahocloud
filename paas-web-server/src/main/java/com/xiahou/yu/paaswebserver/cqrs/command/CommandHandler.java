package com.xiahou.yu.paaswebserver.cqrs.command;

/**
 * 命令处理器接口
 */
public interface CommandHandler<T extends Command> {
    void handle(T command);
}
