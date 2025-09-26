package com.xiahou.yu.paasdomincore.design.executor;

import com.xiahou.yu.paasdomincore.design.command.Command;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;

/**
 * 数据操作执行器接口
 * 微内核架构的核心接口，整合命令模式和职责链模式
 *
 * @author xiahou
 */
public interface DataOperationExecutor {

    /**
     * 执行数据操作命令
     * @param command 命令对象
     * @return 执行结果
     */
    DataOperationResult execute(Command command);

    /**
     * 执行数据操作
     * @param context 命令上下文
     * @param commandType 命令类型
     * @param <T> 返回类型
     * @return 执行结果
     */
    DataOperationResult execute(CommandContext context, CommandType commandType);

    /**
     * 注册前置处理器
     * @param handler 处理器
     */
    void registerPreHandler(String handlerName, Object handler);

    /**
     * 注册后置处理器
     * @param handler 处理器
     */
    void registerPostHandler(String handlerName, Object handler);

    /**
     * 移除处理器
     * @param handlerName 处理器名称
     */
    void removeHandler(String handlerName);
}
