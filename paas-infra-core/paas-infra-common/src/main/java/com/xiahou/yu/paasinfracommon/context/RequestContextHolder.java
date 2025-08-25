package com.xiahou.yu.paasinfracommon.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程上下文管理器
 * 用于在整个请求处理过程中传递上下文信息
 * 使用Alibaba TransmittableThreadLocal支持多线程间的上下文传递
 *
 * @author xiahou
 */
@Slf4j
public class RequestContextHolder {

    private static final TransmittableThreadLocal<RequestContext> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

    /**
     * 设置请求上下文
     */
    public static void setContext(RequestContext context) {
        CONTEXT_HOLDER.set(context);
        log.debug("Set request context: tenantId={}, userId={}, requestId={}",
                 context.getTenantId(), context.getUserId(), context.getRequestId());
    }

    /**
     * 获取请求上下文
     */
    public static RequestContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        RequestContext context = getContext();
        return context != null ? context.getTenantId() : null;
    }

    /**
     * 获取用户ID
     */
    public static String getUserId() {
        RequestContext context = getContext();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        RequestContext context = getContext();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取请求ID
     */
    public static String getRequestId() {
        RequestContext context = getContext();
        return context != null ? context.getRequestId() : null;
    }

    /**
     * 获取客户端IP
     */
    public static String getClientIp() {
        RequestContext context = getContext();
        return context != null ? context.getClientIp() : null;
    }

    /**
     * 获取应用ID
     */
    public static String getAppId() {
        RequestContext context = getContext();
        return context != null ? context.getAppId() : null;
    }

    /**
     * 获取组织ID
     */
    public static String getOrgId() {
        RequestContext context = getContext();
        return context != null ? context.getOrgId() : null;
    }

    // ============ 系统级参数获取方法 ============

    /**
     * 获取系统标识
     */
    public static String getSystem() {
        RequestContext context = getContext();
        return context != null ? context.getSystem() : null;
    }

    /**
     * 获取模块标识
     */
    public static String getModule() {
        RequestContext context = getContext();
        return context != null ? context.getModule() : null;
    }

    /**
     * 获取上下文标识
     */
    public static String getContextId() {
        RequestContext context = getContext();
        return context != null ? context.getContext() : null;
    }

    /**
     * 获取应用标识
     */
    public static String getApp() {
        RequestContext context = getContext();
        return context != null ? context.getApp() : null;
    }

    /**
     * 获取聚合根标识
     */
    public static String getAggr() {
        RequestContext context = getContext();
        return context != null ? context.getAggr() : null;
    }

    /**
     * 清除请求上下文
     */
    public static void clearContext() {
        RequestContext context = CONTEXT_HOLDER.get();
        if (context != null) {
            log.debug("Clear request context: tenantId={}, requestId={}",
                     context.getTenantId(), context.getRequestId());
        }
        CONTEXT_HOLDER.remove();
    }

    /**
     * 检查是否有上下文
     */
    public static boolean hasContext() {
        return CONTEXT_HOLDER.get() != null;
    }
}
