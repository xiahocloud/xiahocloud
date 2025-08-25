package com.xiahou.yu.paaswebserver.config;

import com.xiahou.yu.paasinfracommon.context.RequestContext;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * 请求上下文过滤器配置
 * 使用Filter方式实现上下文管理
 *
 * @author xiahou
 */
@Configuration
@ConditionalOnProperty(name = "paas.context.use-filter", havingValue = "true", matchIfMissing = true)
@Slf4j
public class RequestContextFilterConfig {

    /**
     * 注册PaaS请求上下文过滤器
     */
    @Bean
    public FilterRegistrationBean<PaasRequestContextFilter> paasRequestContextFilterRegistration() {
        FilterRegistrationBean<PaasRequestContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PaasRequestContextFilter());
        registration.addUrlPatterns("/*"); // 拦截所有URL
        registration.setName("paasRequestContextFilter");
        registration.setOrder(1); // 设置优先级，在Spring Boot内置Filter之前执行
        return registration;
    }

    /**
     * PaaS请求上下文过滤器实现
     */
    public static class PaasRequestContextFilter implements Filter {

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

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            try {
                // 检查是否需要处理该请求
                if (shouldProcessRequest(httpRequest)) {
                    initializeRequestContext(httpRequest, httpResponse);
                }

                // 继续执行后续的Filter和Servlet
                chain.doFilter(request, response);

            } finally {
                // 清理上下文
                cleanupRequestContext();
            }
        }

        /**
         * 判断是否需要处理该请求
         */
        private boolean shouldProcessRequest(HttpServletRequest request) {
            String requestURI = request.getRequestURI();

            // 排除不需要处理的路径
            return !requestURI.startsWith("/actuator/") &&
                    !requestURI.equals("/favicon.ico") &&
                    !requestURI.equals("/error");
        }

        /**
         * 初始化请求上下文
         */
        private void initializeRequestContext(HttpServletRequest request, HttpServletResponse response) {
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

                // 将requestId添加到响应头中
                response.setHeader(HEADER_REQUEST_ID, requestId);

                log.debug("Request context initialized by Filter: {} {} - tenantId={}, userId={}, requestId={}",
                        request.getMethod(), request.getRequestURI(),
                        context.getTenantId(), context.getUserId(), context.getRequestId());

            } catch (Exception e) {
                log.error("Failed to initialize request context in Filter", e);
                // 不抛出异常，避免影响请求处理
            }
        }

        /**
         * 清理请求上下文
         */
        private void cleanupRequestContext() {
            try {
                RequestContextHolder.clearContext();
            } catch (Exception e) {
                log.warn("Failed to clear request context in Filter", e);
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
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (StringUtils.hasText(xRealIp)) {
                return xRealIp.trim();
            }

            return request.getRemoteAddr();
        }
    }
}
