# 系统级参数HTTP头传递方案

## 概述

系统级参数（system、module、context、app、aggr）现在通过HTTP头传递，不再需要在每个命令请求中重复传递这些参数。这样可以让API更加简洁，避免重复。

## HTTP头定义

| HTTP头名称 | 对应参数 | 说明 | 示例值 |
|-----------|---------|------|--------|
| X-System | system | 系统标识 | `paas` |
| X-Module | module | 模块标识 | `user` |
| X-Context | context | 上下文标识 | `web` |
| X-App | app | 应用标识 | `admin` |
| X-Aggr | aggr | 聚合根标识 | `user` |

## 使用示例

### 1. 原来的方式（已弃用）

```json
// POST /graphql
{
  "query": "mutation dynamicCommand($input: DynamicCommandInput!) { dynamicCommand(input: $input) { success message } }",
  "variables": {
    "input": {
      "system": "paas",
      "module": "user", 
      "context": "web",
      "app": "admin",
      "aggr": "user",
      "entity": "user_info",
      "operation": "CREATE",
      "data": {
        "name": "张三",
        "email": "zhangsan@example.com"
      }
    }
  }
}
```

### 2. 新的方式

```bash
# HTTP Headers
X-System: paas
X-Module: user
X-Context: web
X-App: admin
X-Aggr: user

# Request Body
{
  "query": "mutation dynamicCommand($input: DynamicCommandInput!) { dynamicCommand(input: $input) { success message } }",
  "variables": {
    "input": {
      "entity": "user_info",
      "operation": "CREATE",
      "data": {
        "name": "张三",
        "email": "zhangsan@example.com"
      }
    }
  }
}
```

## 优势

1. **API简洁性**: 请求体更加简洁，避免重复传递系统级参数
2. **一致性**: 在同一个会话中，系统级参数通常是固定的，通过HTTP头传递更符合HTTP协议语义
3. **缓存友好**: 系统级参数可以作为HTTP缓存的Key的一部分
4. **安全性**: 可以在网关层面对这些系统级参数进行验证和路由

## 实现细节

### 1. HTTP头处理

```java
// 在RequestContextInterceptor中自动提取HTTP头
context.setSystem(getHeaderValue(request, "X-System"));
context.setModule(getHeaderValue(request, "X-Module"));
context.setContext(getHeaderValue(request, "X-Context"));
context.setApp(getHeaderValue(request, "X-App"));
context.setAggr(getHeaderValue(request, "X-Aggr"));
```

### 2. 线程上下文访问

```java
// 在业务代码中通过静态方法获取
String system = RequestContextHolder.getSystem();
String module = RequestContextHolder.getModule();
String contextId = RequestContextHolder.getContextId();
String app = RequestContextHolder.getApp();
String aggr = RequestContextHolder.getAggr();
```

### 3. 自动参数注入

```java
// 在命令处理器中自动从线程上下文获取参数
@Override
public DynamicCommandResponse handle(DynamicCommandInput input) {
    // 系统级参数自动从HTTP头获取
    String system = RequestContextHolder.getSystem();
    String module = RequestContextHolder.getModule();
    // ...
    
    // 调用数据操作适配器
    DynamicDataOperationAdapter.DataOperationResult result = dataOperationAdapter.handleCommand(
        system, module, context, app, aggr,
        input.getEntity(), input.getOperation(), 
        input.getData(), input.getConditions(), 
        userId
    );
}
```

## 客户端集成

### JavaScript示例

```javascript
// 设置默认的HTTP头
const headers = {
  'X-System': 'paas',
  'X-Module': 'user',
  'X-Context': 'web', 
  'X-App': 'admin',
  'X-Aggr': 'user',
  'Content-Type': 'application/json'
};

// 发起请求
fetch('/graphql', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify({
    query: `mutation dynamicCommand($input: DynamicCommandInput!) { 
      dynamicCommand(input: $input) { 
        success message data 
      } 
    }`,
    variables: {
      input: {
        entity: "user_info",
        operation: "CREATE",
        data: {
          name: "张三",
          email: "zhangsan@example.com"
        }
      }
    }
  })
});
```

### Java客户端示例

```java
// 使用Spring RestTemplate
HttpHeaders headers = new HttpHeaders();
headers.set("X-System", "paas");
headers.set("X-Module", "user");
headers.set("X-Context", "web");
headers.set("X-App", "admin");
headers.set("X-Aggr", "user");

HttpEntity<GraphQLRequest> request = new HttpEntity<>(graphqlRequest, headers);
ResponseEntity<GraphQLResponse> response = restTemplate.postForEntity(
    "/graphql", request, GraphQLResponse.class
);
```

## 注意事项

1. **必填参数**: `X-System` 和 `X-Module` 是必填的HTTP头
2. **参数验证**: 如果缺少必要的系统级参数，系统会返回错误响应
3. **线程安全**: 使用了TransmittableThreadLocal确保多线程环境下的上下文传递
4. **兼容性**: 旧的API调用方式暂时仍然支持，但建议迁移到新方式

## 迁移指南

1. **更新客户端代码**: 将系统级参数从请求体移到HTTP头
2. **添加HTTP头**: 在所有API调用中添加必要的X-*头信息
3. **测试验证**: 确保所有功能正常工作
4. **移除旧参数**: 从DTO类中移除已不需要的字段（已完成）
