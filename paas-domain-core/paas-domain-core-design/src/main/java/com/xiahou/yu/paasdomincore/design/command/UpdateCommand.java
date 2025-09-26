package com.xiahou.yu.paasdomincore.design.command;

import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import lombok.Builder;
import lombok.Data;

/**
 * 更新数据命令
 * 用于封装数据更新操作
 *
 * @author xiahou
 */
@Data
@Builder
public class UpdateCommand implements Command {

    private CommandContext context;

    @Override
    public DataOperationResult execute() {
        // 具体的执行逻辑由执行器实现
        throw new UnsupportedOperationException("Command execution should be handled by executor");
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.UPDATE;
    }

    @Override
    public CommandContext getContext() {
        return this.context;
    }
}
