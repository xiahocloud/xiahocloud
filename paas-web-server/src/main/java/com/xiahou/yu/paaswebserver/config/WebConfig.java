package com.xiahou.yu.paaswebserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册请求上下文拦截器（当使用interceptor模式时）
 *
 * @author xiahou
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "paas.context.use-filter", havingValue = "false", matchIfMissing = false)
public class WebConfig implements WebMvcConfigurer {

    private final RequestContextInterceptor requestContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestContextInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(
                    "/actuator/**",      // 排除健康检查
                    "/favicon.ico",      // 排除图标请求
                    "/error"             // 排除错误页面
                );
    }
}
