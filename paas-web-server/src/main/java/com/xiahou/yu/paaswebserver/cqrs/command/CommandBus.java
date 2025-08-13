package com.xiahou.yu.paaswebserver.cqrs.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令总线 - 负责分发命令到对应的处理器
 */
@Service
public class CommandBus {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Command> void send(T command) {
        CommandHandler<T> handler = (CommandHandler<T>) getHandler(command.getClass());
        if (handler == null) {
            throw new RuntimeException("No handler found for command: " + command.getClass().getSimpleName());
        }
        handler.handle(command);
    }

    @SuppressWarnings("unchecked")
    private <T extends Command> CommandHandler<T> getHandler(Class<T> commandClass) {
        if (!handlers.containsKey(commandClass)) {
            // 查找处理器
            Map<String, CommandHandler> handlerBeans = applicationContext.getBeansOfType(CommandHandler.class);
            for (CommandHandler handler : handlerBeans.values()) {
                Class<?> handlerCommandType = getGenericType(handler.getClass());
                if (handlerCommandType != null && handlerCommandType.equals(commandClass)) {
                    handlers.put(commandClass, handler);
                    break;
                }
            }
        }
        return (CommandHandler<T>) handlers.get(commandClass);
    }

    private Class<?> getGenericType(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (parameterizedType.getRawType().equals(CommandHandler.class)) {
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length > 0) {
                        return (Class<?>) typeArguments[0];
                    }
                }
            }
        }
        return null;
    }
}
