package com.xiahou.yu.paasdomincore.design.chain;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;

/**
 * 处理器接口 - 职责链模式核心接口
 * 用于定义数据操作前后的处理逻辑
 *
 * @author xiahou
 */
public interface Handler {

    /**
     * 处理请求
     * @param context 命令上下文
     * @param chain 处理器链
     * @return 处理结果，返回false表示中断链条
     */
    boolean handle(CommandContext context, HandlerChain chain);

    /**
     * 获取处理器名称
     * @return 处理器名称
     */
    String getName();

    /**
     * 获取处理器优先级，数值越小优先级越高
     * @return 优先级
     */
    int getOrder();

    /**
     * 判断是否支持处理该上下文
     * @param context 命令上下文
     * @return 是否支持
     */
    boolean supports(CommandContext context);
}
