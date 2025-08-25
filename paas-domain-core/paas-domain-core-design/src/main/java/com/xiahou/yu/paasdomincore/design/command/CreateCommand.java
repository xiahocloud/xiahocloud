package com.xiahou.yu.paasdomincore.design.command;

import lombok.Builder;
import lombok.Data;

/**
 * 创建数据命令
 * 用于封装数据创建操作
 *
 * @author xiahou
 */
@Data
@Builder
public class CreateCommand implements Command<Object> {

    private CommandContext context;

    @Override
    public Object execute() {
        // 具体的执行逻辑由执行器实现
        throw new UnsupportedOperationException("Command execution should be handled by executor");
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.CREATE;
    }

    @Override
    public CommandContext getContext() {
        return this.context;
    }
}
