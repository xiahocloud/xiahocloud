package com.xiahou.yu.paasdomincore.design.command;

import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;

/**
 * 命令接口 - 命令模式核心接口
 * 用于封装数据操作请求
 *
 * @author xiahou
 */
public interface Command {

    /**
     * 执行命令
     * @return 执行结果
     */
    DataOperationResult execute();

    /**
     * 获取命令类型
     * @return 命令类型
     */
    CommandType getCommandType();

    /**
     * 获取命令上下文
     * @return 命令上下文
     */
    CommandContext getContext();
}
