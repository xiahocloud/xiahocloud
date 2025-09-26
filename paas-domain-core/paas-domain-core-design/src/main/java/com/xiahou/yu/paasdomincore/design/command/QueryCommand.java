package com.xiahou.yu.paasdomincore.design.command;

import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import lombok.Builder;
import lombok.Data;

/**
 * 查询数据命令
 * 用于封装数据查询操作
 *
 * @author xiahou
 */
@Data
@Builder
public class QueryCommand implements Command {

    private CommandContext context;

    @Override
    public DataOperationResult execute() {
        // 具体的执行逻辑由执行器实现
        throw new UnsupportedOperationException("Command execution should be handled by executor");
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.QUERY;
    }

    @Override
    public CommandContext getContext() {
        return this.context;
    }
}
