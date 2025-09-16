package com.xiahou.yu.paaswebserver.config;

import com.xiahou.yu.paasinfracommon.context.RequestContext;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import com.xiahou.yu.paaswebserver.filter.PaasRequestContextFilter;
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


}
