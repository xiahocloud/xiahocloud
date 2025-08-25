package com.xiahou.yu.paaswebserver.cqrs.command;

import com.xiahou.yu.paaswebserver.dto.DynamicCommandResponse;
import com.xiahou.yu.paaswebserver.dto.input.DynamicCommandInput;

/**
 * 动态命令处理器接口
 * 定义处理动态命令的标准接口
 *
 * @author xiahou
 */
public interface DynamicCommandHandler {

    /**
     * 处理动态命令
     * @param input 命令输入
     * @return 命令响应
     */
    DynamicCommandResponse handle(DynamicCommandInput input);
}
