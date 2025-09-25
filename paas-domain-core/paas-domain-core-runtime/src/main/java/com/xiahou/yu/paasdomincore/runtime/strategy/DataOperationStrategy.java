package com.xiahou.yu.paasdomincore.runtime.strategy;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;

/**
 * 数据操作策略接口
 *
 * @author xiahou
 */
public interface DataOperationStrategy {
    /**
     * 执行操作
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    DataOperationResult execute(CommandContext context);
}
