package com.xiahou.yu.paaswebserver.config;

import com.xiahou.yu.paasinfracommon.context.RequestContext;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 请求上下文拦截器（当使用interceptor模式时）
 * 从HTTP请求头中提取上下文信息并设置到线程上下文中
 *
 * @author xiahou
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "paas.context.use-filter", havingValue = "false", matchIfMissing = false)
public class RequestContextInterceptor implements HandlerInterceptor {

    // HTTP请求头常量
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_APP_ID = "X-App-Id";
    private static final String HEADER_ORG_ID = "X-Org-Id";
    private static final String HEADER_ROLES = "X-Roles";
    private static final String HEADER_PERMISSIONS = "X-Permissions";
    private static final String HEADER_USER_AGENT = "User-Agent";

    // 系统级参数HTTP头常量
    private static final String HEADER_SYSTEM = "X-System";
    private static final String HEADER_MODULE = "X-Module";
    private static final String HEADER_CONTEXT = "X-Context";
    private static final String HEADER_APP = "X-App";
    private static final String HEADER_AGGR = "X-Aggr";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 创建请求上下文
            RequestContext context = new RequestContext();

            // 从请求头中提取上下文信息
            context.setTenantId(getHeaderValue(request, HEADER_TENANT_ID));
            context.setUserId(getHeaderValue(request, HEADER_USER_ID));
            context.setUsername(getHeaderValue(request, HEADER_USERNAME));
            context.setAppId(getHeaderValue(request, HEADER_APP_ID));
            context.setOrgId(getHeaderValue(request, HEADER_ORG_ID));
            context.setRoles(getHeaderValue(request, HEADER_ROLES));
            context.setPermissions(getHeaderValue(request, HEADER_PERMISSIONS));
            context.setUserAgent(getHeaderValue(request, HEADER_USER_AGENT));

            // 从请求头中提取系统级参数
            context.setSystem(getHeaderValue(request, HEADER_SYSTEM));
            context.setModule(getHeaderValue(request, HEADER_MODULE));
            context.setContext(getHeaderValue(request, HEADER_CONTEXT));
            context.setApp(getHeaderValue(request, HEADER_APP));
            context.setAggr(getHeaderValue(request, HEADER_AGGR));

            // 请求ID：如果请求头中没有，则生成一个
            String requestId = getHeaderValue(request, HEADER_REQUEST_ID);
            if (!StringUtils.hasText(requestId)) {
                requestId = UUID.randomUUID().toString().replace("-", "");
            }
            context.setRequestId(requestId);

            // 设置客户端IP
            context.setClientIp(getClientIpAddress(request));

            // 设置请求时间戳
            context.setTimestamp(System.currentTimeMillis());

            // 设置到线程上下文
            RequestContextHolder.setContext(context);

            // 将requestId添加到响应头中，便于前端进行链路追踪
            response.setHeader(HEADER_REQUEST_ID, requestId);

            log.debug("Request context initialized: {} {} - tenantId={}, userId={}, requestId={}",
                    request.getMethod(), request.getRequestURI(),
                    context.getTenantId(), context.getUserId(), context.getRequestId());

            return true;
        } catch (Exception e) {
            log.error("Failed to initialize request context", e);
            // 即使上下文初始化失败，也不阻断请求处理
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        try {
            // 清除线程上下文，防止内存泄漏
            RequestContextHolder.clearContext();
        } catch (Exception e) {
            log.warn("Failed to clear request context", e);
        }
    }

    /**
     * 获取请求头值
     */
    private String getHeaderValue(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp.trim();
        }

        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (StringUtils.hasText(xForwardedProto)) {
            return request.getRemoteAddr();
        }

        return request.getRemoteAddr();
    }
}
