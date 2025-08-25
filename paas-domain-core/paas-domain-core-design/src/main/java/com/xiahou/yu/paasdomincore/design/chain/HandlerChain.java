package com.xiahou.yu.paasdomincore.design.chain;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;

/**
 * 处理器链接口
 * 用于管理处理器的执行流程
 *
 * @author xiahou
 */
public interface HandlerChain {

    /**
     * 继续执行下一个处理器
     * @param context 命令上下文
     * @return 处理结果
     */
    boolean proceed(CommandContext context);

    /**
     * 添加处理器到链条
     * @param handler 处理器
     * @return 当前链条实例
     */
    HandlerChain addHandler(Handler handler);

    /**
     * 获取当前处理器索引
     * @return 索引
     */
    int getCurrentIndex();
}
