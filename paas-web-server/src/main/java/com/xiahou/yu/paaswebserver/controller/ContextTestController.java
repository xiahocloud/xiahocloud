package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.context.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文测试控制器
 * 用于测试请求上下文功能
 *
 * @author xiahou
 */
@RestController
@RequestMapping("/api/context")
@Slf4j
public class ContextTestController {

    /**
     * 获取当前请求上下文信息
     */
    @GetMapping("/info")
    public Map<String, Object> getContextInfo() {
        Map<String, Object> contextInfo = new HashMap<>();

        contextInfo.put("tenantId", RequestContextHolder.getTenantId());
        contextInfo.put("userId", RequestContextHolder.getUserId());
        contextInfo.put("username", RequestContextHolder.getUsername());
        contextInfo.put("requestId", RequestContextHolder.getRequestId());
        contextInfo.put("clientIp", RequestContextHolder.getClientIp());
        contextInfo.put("appId", RequestContextHolder.getAppId());
        contextInfo.put("orgId", RequestContextHolder.getOrgId());
        contextInfo.put("hasContext", RequestContextHolder.hasContext());

        log.info("Context info requested: {}", contextInfo);

        return contextInfo;
    }
}
