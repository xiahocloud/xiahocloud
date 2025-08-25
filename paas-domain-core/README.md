# 微内核架构数据操作系统

## 架构概述

本系统采用微内核架构设计，结合命令模式和职责链模式，为PaaS平台提供统一的数据操作接口，支持插件化扩展。

## 核心设计模式

### 1. 微内核架构 (Microkernel Architecture)
- **内核层**: `DataOperationExecutor` - 负责协调命令执行和处理器链
- **插件层**: 各种 `Handler` - 可插拔的业务逻辑处理器

### 2. 命令模式 (Command Pattern)
- `Command` 接口封装数据操作请求
- `CommandContext` 携带操作上下文信息
- 支持 CREATE、UPDATE、DELETE、QUERY 四种操作类型

### 3. 职责链模式 (Chain of Responsibility)
- `Handler` 接口定义处理器规范
- `HandlerChain` 管理处理器执行顺序
- 支持前置和后置处理器

## 模块结构

```
paas-domain-core/
├── paas-domain-core-design/     # 设计时模块（接口定义）
│   └── src/main/java/com/xiahou/yu/paasdomincore/design/
│       ├── command/             # 命令模式相关接口
│       ├── chain/               # 职责链模式相关接口
│       ├── executor/            # 执行器接口
│       └── service/             # 服务接口
└── paas-domain-core-runtime/    # 运行时模块（具体实现）
    └── src/main/java/com/xiahou/yu/paasdomincore/runtime/
        ├── executor/            # 执行器实现
        ├── chain/               # 职责链实现
        ├── handler/             # 内置处理器
        ├── service/             # 服务实现
        ├── adapter/             # 适配器
        └── integration/         # 集成工具
```

## 核心组件

### 1. 数据操作执行器 (DataOperationExecutor)
负责整个数据操作流程的协调：
- 执行前置处理器链（验证、权限、审计等）
- 执行核心业务逻辑
- 执行后置处理器链（缓存、通知等）

### 2. 命令上下文 (CommandContext)
携带操作相关的所有信息：
```java
CommandContext context = CommandContext.builder()
    .system("paas")
    .module("user")
    .aggr("user")
    .entity("user_info")
    .data(userData)
    .conditions(queryConditions)
    .build();
```

### 3. 内置处理器
- **DataValidationHandler**: 数据验证（优先级100）
- **PermissionHandler**: 权限校验（优先级110）
- **DataAutoFillHandler**: 自动填充系统字段（优先级150）
- **AuditLogHandler**: 审计日志（优先级200）
- **CacheHandler**: 缓存处理（优先级300）

## 使用示例

### 1. 基本使用
```java
@Autowired
private DataOperationService dataOperationService;

// 创建数据
CommandContext context = dataOperationService.buildContext(
    "paas", "user", "web", "admin", "user", "user_info",
    userData, null
);
context.setAttribute("userId", "admin");
Object result = dataOperationService.create(context);
```

### 2. 集成到现有GraphQL控制器
```java
@Autowired
private MicroKernelCommandAdapter commandAdapter;

public DynamicCommandResponse handle(DynamicCommandInput input) {
    MicroKernelCommandResult result = commandAdapter.handleCommand(
        input.getSystem(), input.getModule(), input.getContext(),
        input.getApp(), input.getAggr(), input.getEntity(),
        input.getOperation(), input.getData(), input.getConditions(),
        getCurrentUserId()
    );
    return convertToResponse(result);
}
```

## 插件扩展

### 1. 创建自定义处理器
```java
@Component
public class CustomBusinessHandler implements Handler {
    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        // 自定义业务逻辑
        return chain.proceed(context);
    }
    
    @Override
    public String getName() {
        return "CustomBusinessHandler";
    }
    
    @Override
    public int getOrder() {
        return 250; // 设置优先级
    }
    
    @Override
    public boolean supports(CommandContext context) {
        return "custom_entity".equals(context.getEntity());
    }
}
```

### 2. 动态注册处理器
```java
@Autowired
private DataOperationExecutor executor;

// 注册前置处理器
executor.registerPreHandler("customHandler", new CustomHandler());

// 注册后置处理器
executor.registerPostHandler("notificationHandler", new NotificationHandler());
```

## 配置说明

### 1. 自动配置
系统会自动扫描并注册所有实现了 `Handler` 接口的Spring Bean：
- 前置处理器：验证、权限、审计等
- 后置处理器：缓存、通知等

### 2. 处理器优先级
- 100-199: 核心验证和权限处理器
- 200-299: 业务逻辑处理器  
- 300-399: 缓存和性能优化处理器

## 扩展点

### 1. 数据访问层集成
在 `DataOperationStrategy` 中集成您的JPA Repository或其他数据访问层：
```java
private static class CreateOperationStrategy implements DataOperationStrategy {
    @Override
    public Object execute(CommandContext context) {
        // 调用实际的数据访问层
        return userRepository.save(buildUserEntity(context));
    }
}
```

### 2. 事件发布
在后置处理器中发布领域事件：
```java
public class DomainEventHandler implements Handler {
    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        boolean result = chain.proceed(context);
        if (result) {
            publishDomainEvent(context);
        }
        return result;
    }
}
```

## 优势

1. **插件化**: 支持动态添加/移除处理器
2. **可扩展**: 微内核架构便于功能扩展
3. **可测试**: 每个处理器可独立测试
4. **松耦合**: 通过接口隔离，降低耦合度
5. **统一性**: 提供统一的数据操作接口

## 后续规划

1. 集成分布式事务支持
2. 添加性能监控和指标收集
3. 支持异步处理和事件驱动
4. 集成配置中心，支持动态配置
5. 添加更多内置处理器（限流、熔断等）
