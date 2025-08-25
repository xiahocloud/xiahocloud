package com.xiahou.yu.paasdomincore.runtime.chain;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理器链默认实现
 *
 * @author xiahou
 */
@Slf4j
public class DefaultHandlerChain implements HandlerChain {

    private final List<Handler> handlers;
    private int currentIndex = 0;

    public DefaultHandlerChain() {
        this.handlers = new ArrayList<>();
    }

    public DefaultHandlerChain(List<Handler> handlers) {
        this.handlers = handlers.stream()
                .sorted((h1, h2) -> Integer.compare(h1.getOrder(), h2.getOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean proceed(CommandContext context) {
        if (currentIndex >= handlers.size()) {
            return true;
        }

        Handler handler = handlers.get(currentIndex);
        currentIndex++;

        if (!handler.supports(context)) {
            return proceed(context);
        }

        log.debug("Executing handler: {}", handler.getName());
        return handler.handle(context, this);
    }

    @Override
    public HandlerChain addHandler(Handler handler) {
        this.handlers.add(handler);
        // 重新排序
        this.handlers.sort((h1, h2) -> Integer.compare(h1.getOrder(), h2.getOrder()));
        return this;
    }

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }
}
