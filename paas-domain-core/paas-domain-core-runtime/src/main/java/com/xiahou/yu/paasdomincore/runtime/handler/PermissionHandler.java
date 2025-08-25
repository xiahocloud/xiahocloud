package com.xiahou.yu.paasdomincore.runtime.handler;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 权限校验处理器
 * 在数据操作前校验用户权限
 *
 * @author xiahou
 */
@Component
@Slf4j
public class PermissionHandler implements Handler {

    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        String aggr = context.getAttribute("aggr");
        log.info("Checking permissions for {}.{}", aggr, context.getEntity());

        String userId = context.getAttribute("userId");
        String operation = context.getAttribute("commandType");

        if (!hasPermission(userId, context, operation)) {
            log.warn("Permission denied for user {} on {}.{} with operation {}",
                    userId, aggr, context.getEntity(), operation);
            return false;
        }

        log.info("Permission check passed for user {} on {}.{}",
                userId, aggr, context.getEntity());
        return chain.proceed(context);
    }

    @Override
    public String getName() {
        return "PermissionHandler";
    }

    @Override
    public int getOrder() {
        return 110; // 在验证之后，其他操作之前
    }

    @Override
    public boolean supports(CommandContext context) {
        // 支持所有需要权限控制的操作
        return context.getAttribute("userId") != null;
    }

    private boolean hasPermission(String userId, CommandContext context, String operation) {
        // 这里应该调用权限服务进行实际的权限检查
        // 示例实现：检查用户是否有对应的权限

        if (userId == null) {
            return false;
        }

        // 模拟权限检查逻辑
        String system = context.getAttribute("system");
        String aggr = context.getAttribute("aggr");
        String resourceKey = String.format("%s.%s.%s",
                system, aggr, context.getEntity());
        String permissionKey = String.format("%s:%s", resourceKey, operation);

        // 这里应该调用实际的权限服务
        // 例如：return permissionService.hasPermission(userId, permissionKey);

        // 示例：管理员用户拥有所有权限
        Set<String> userRoles = getUserRoles(userId);
        if (userRoles.contains("ADMIN")) {
            return true;
        }

        // 其他用户权限检查逻辑
        return checkUserPermission(userId, permissionKey);
    }

    private Set<String> getUserRoles(String userId) {
        // 模拟获取用户角色
        if ("admin".equals(userId)) {
            return Set.of("ADMIN");
        }
        return Set.of("USER");
    }

    private boolean checkUserPermission(String userId, String permissionKey) {
        // 模拟权限检查
        // 在实际应用中，这里应该查询权限数据库或调用权限服务
        return true; // 简化实现，实际应该进行详细的权限检查
    }
}
