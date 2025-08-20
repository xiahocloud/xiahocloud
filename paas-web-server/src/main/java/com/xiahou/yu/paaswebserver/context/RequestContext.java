package com.xiahou.yu.paaswebserver.context;

import lombok.Data;

/**
 * 请求上下文信息
 *
 * @author xiahou
 */
@Data
public class RequestContext {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 请求ID - 用于链路追踪
     */
    private String requestId;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 请求时间戳
     */
    private Long timestamp;

    /**
     * 应用标识
     */
    private String appId;

    /**
     * 组织ID
     */
    private String orgId;

    /**
     * 角色信息
     */
    private String roles;

    /**
     * 权限信息
     */
    private String permissions;
}
