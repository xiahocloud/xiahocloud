# 请求上下文管理配置说明

## 概述

系统提供了两种请求上下文管理方式：
1. **Filter模式**（默认启用）
2. **HandlerInterceptor模式**（可选）

## 当前配置状态

系统当前使用 **Filter模式** 来处理请求上下文，配置如下：

```yaml
paas:
  context:
    use-filter: true  # 使用Filter模式（默认）
```

## Filter模式 vs HandlerInterceptor模式

### Filter模式（当前使用）

**优势：**
- 在Servlet容器级别执行，处理所有HTTP请求
- 执行时机更早，在Spring MVC框架之前
- 对异常处理更加健壮
- 适合处理跨框架的通用逻辑

**执行顺序：**
```
HTTP Request → Filter → DispatcherServlet → Controller
```

**特点：**
- 使用 `@Order(1)` 确保优先执行
- 通过 `shouldProcessRequest()` 方法过滤不需要处理的请求
- 在 `finally` 块中确保上下文清理

### HandlerInterceptor模式（备选）

**优势：**
- 更好的Spring集成，支持依赖注入
- 精确的路径匹配控制
- 丰富的生命周期方法
- 更适合Spring MVC特定的逻辑

**执行顺序：**
```
HTTP Request → Filter → DispatcherServlet → HandlerInterceptor → Controller
```

## 配置切换

### 切换到HandlerInterceptor模式

```yaml
paas:
  context:
    use-filter: false  # 禁用Filter，启用HandlerInterceptor
```

### 切换到Filter模式（默认）

```yaml
paas:
  context:
    use-filter: true   # 启用Filter（默认值）
```

或者直接删除该配置项（系统默认使用Filter模式）。

## 功能对比

| 功能特性 | Filter模式 | HandlerInterceptor模式 |
|----------|------------|----------------------|
| 执行时机 | Servlet容器级别 | Spring MVC框架内 |
| 路径过滤 | 手动判断 | Spring路径匹配 |
| 依赖注入 | 有限支持 | 完全支持 |
| 异常处理 | finally块保证 | afterCompletion方法 |
| 性能开销 | 轻微 | 更轻 |
| 框架耦合 | 低 | 高 |

## 实际使用效果

无论使用哪种模式，对业务代码都是透明的：

```java
// 在任何地方都可以这样使用
String tenantId = RequestContextHolder.getTenantId();
String userId = RequestContextHolder.getUserId();
RequestContext context = RequestContextHolder.getContext();
```

## 支持的请求头

两种模式都支持以下请求头：

- `X-Tenant-Id`: 租户ID
- `X-User-Id`: 用户ID
- `X-Username`: 用户名
- `X-Request-Id`: 请求ID（自动生成）
- `X-App-Id`: 应用ID
- `X-Org-Id`: 组织ID
- `X-Roles`: 用户角色
- `X-Permissions`: 用户权限

## 排除路径

两种模式都会排除以下路径：

- `/actuator/**` - 健康检查端点
- `/h2-console/**` - H2数据库控制台
- `/favicon.ico` - 网站图标
- `/error` - 错误页面

## 建议

**推荐使用Filter模式**，因为：
1. 执行更早，能捕获更多类型的请求
2. 异常处理更加健壮
3. 对于PaaS平台的多租户场景更适合
4. 减少了与Spring框架的耦合

如果有特殊需求需要深度集成Spring特性，可以考虑切换到HandlerInterceptor模式。
