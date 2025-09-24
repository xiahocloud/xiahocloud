# PaaS 动态 GraphQL 接口层使用指南

## 概述

本项目基于 CQRS (Command Query Responsibility Segregation) 模式实现了一个动态的 GraphQL 接口层，专为 PaaS 平台设计。该接口层支持动态 Schema 管理，无需预定义固定的 GraphQL Schema，并且具备完整的多租户上下文管理能力。

## 新增功能：请求上下文管理

### 1. 自动上下文提取

系统会自动从HTTP请求头中提取以下上下文信息：

- `X-Tenant-Id`: 租户ID
- `X-User-Id`: 用户ID  
- `X-Username`: 用户名
- `X-Request-Id`: 请求ID（如果不提供会自动生成）
- `X-App-Id`: 应用ID
- `X-Org-Id`: 组织ID
- `X-Roles`: 用户角色
- `X-Permissions`: 用户权限

### 2. 线程上下文管理

使用 `RequestContextHolder` 在整个请求处理过程中访问上下文信息：

```java
// 获取当前租户ID
String tenantId = RequestContextHolder.getTenantId();

// 获取当前用户ID
String userId = RequestContextHolder.getUserId();

// 获取完整上下文
RequestContext context = RequestContextHolder.getContext();
```

### 3. 自动租户隔离

- 查询操作会自动应用租户过滤
- 命令操作会自动添加租户和审计信息
- 所有数据操作都确保租户隔离

## 使用示例

### 1. 带上下文的GraphQL查询

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant123" \
  -H "X-User-Id: user456" \
  -H "X-Username: zhangsan" \
  -d '{
    "query": "query($input: DynamicQueryInput!) { dynamicQuery(input: $input) { data total schema } }",
    "variables": {
      "input": {
        "module": "hr",
        "entity": "user", 
        "fields": ["id", "name", "email"]
      }
    }
  }'
```

注意：由于有了上下文管理，`system`字段现在是可选的，系统会自动使用`X-Tenant-Id`作为租户标识。

### 2. 带上下文的GraphQL命令

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant123" \
  -H "X-User-Id: user456" \
  -d '{
    "query": "mutation($input: DynamicCommandInput!) { dynamicCommand(input: $input) { success message data } }",
    "variables": {
      "input": {
        "module": "hr",
        "entity": "user",
        "operation": "CREATE",
        "data": {
          "name": "新用户",
          "email": "newuser@example.com"
        }
      }
    }
  }'
```

系统会自动在数据中添加：
- `tenantId`: 来自请求头
- `creator`: 来自请求头的用户ID
- `createdAt`: 当前时间戳
- `updater`: 来自请求头的用户ID  
- `updatedAt`: 当前时间戳

### 3. 测试上下文功能

```bash
curl -X GET http://localhost:8080/api/context/info \
  -H "X-Tenant-Id: tenant123" \
  -H "X-User-Id: user456" \
  -H "X-Username: zhangsan"
```

返回当前线程上下文中的所有信息。

## 架构设计

### 核心组件

1. **RequestContext** - 请求上下文数据结构
2. **RequestContextHolder** - 线程安全的上下文管理器
3. **RequestContextInterceptor** - HTTP拦截器，自动提取请求头信息
4. **WebConfig** - Web配置，注册上下文拦截器
5. **DynamicQueryHandler** - 查询处理器（已更新支持上下文）
6. **DynamicCommandHandler** - 命令处理器（已更新支持上下文）

### 上下文生命周期

1. **请求开始**: 拦截器从HTTP请求头提取上下文信息
2. **处理过程**: 业务代码通过`RequestContextHolder`访问上下文
3. **请求结束**: 拦截器自动清理线程上下文，防止内存泄漏

### 多租户隔离

- **数据隔离**: 所有查询自动添加租户过滤条件
- **操作审计**: 创建和更新操作自动记录操作人和时间
- **权限控制**: 可基于上下文中的角色和权限信息进行访问控制

## 快速开始

### 1. 启动应用

```bash
./gradlew bootRun
```

应用启动后，可以通过以下地址访问：
- GraphQL Playground: http://localhost:8080/graphiql
- H2 数据库控制台: http://localhost:8080/h2-console

### 2. 注册 Schema 元数据

首先需要通过 REST API 注册 Schema 元数据：

```bash
curl -X POST http://localhost:8080/api/schemas \
  -H "Content-Type: application/json" \
  -d '{
    "system": "xia",
    "module": "hr", 
    "context": "api",
    "app": "invoice",
    "aggr": "employee",
    "entity": "user",
    "fieldTypes": {
      "id": "ID",
      "name": "String",
      "email": "String",
      "age": "Int",
      "department": "String",
      "createdAt": "DateTime"
    },
    "primaryKey": "id"
  }'
```

### 3. 执行动态查询

#### GraphQL 查询示例

```graphql
query GetUsers($input: DynamicQueryInput!) {
  dynamicQuery(input: $input) {
    data
    total
    page
    size
    hasNext
    schema
    fieldTypes
  }
}
```

查询变量：
```json
{
  "input": {
    "system": "xia",
    "module": "hr",
    "context": "api", 
    "app": "invoice",
    "aggr": "employee",
    "entity": "user",
    "fields": ["id", "name", "email"],
    "page": 0,
    "size": 10
  }
}
```

#### 带过滤条件的查询

```json
{
  "input": {
    "system": "xia",
    "module": "hr", 
    "entity": "user",
    "fields": ["id", "name", "email"],
    "filter": "age > :minAge AND department = :dept",
    "filterParams": {
      "minAge": 25,
      "dept": "IT"
    }
  }
}
```

### 4. 执行动态命令

#### 创建数据

```graphql
mutation CreateUser($input: DynamicCommandInput!) {
  dynamicCommand(input: $input) {
    success
    message
    data
    operationType
    affectedRows
  }
}
```

变量：
```json
{
  "input": {
    "system": "xia",
    "module": "hr",
    "entity": "user",
    "operation": "CREATE",
    "data": {
      "name": "张三",
      "email": "zhangsan@company.com",
      "age": 28,
      "department": "IT"
    }
  }
}
```

#### 更新数据

```json
{
  "input": {
    "system": "xia",
    "module": "hr",
    "entity": "user",
    "operation": "UPDATE", 
    "data": {
      "age": 29,
      "department": "R&D"
    },
    "conditions": {
      "id": "user_123"
    }
  }
}
```

#### 删除数据

```json
{
  "input": {
    "system": "xia", 
    "module": "hr",
    "entity": "user",
    "operation": "DELETE",
    "conditions": {
      "id": "user_123"
    }
  }
}
```

## API 参考

### GraphQL 接口

#### 查询接口
- **dynamicQuery**: 执行动态查询
  - 输入: `DynamicQueryInput`
  - 输出: `DynamicQueryResponse`

#### 变更接口  
- **dynamicCommand**: 执行动态命令
  - 输入: `DynamicCommandInput`
  - 输出: `DynamicCommandResponse`

### REST 接口

#### Schema 管理
- `POST /api/schemas` - 注册新 Schema
- `GET /api/schemas` - 获取指定 Schema
- `GET /api/schemas/all` - 获取所有 Schema

## 扩展开发

### 1. 添加自定义查询逻辑

在 `DynamicQueryHandler` 中的 `executeQuery` 方法中实现：

```java
private List<Map<String, Object>> executeQuery(DynamicQueryInput input, DynamicSchemaMetadata metadata) {
    // 实现你的数据库查询逻辑
    // 例如：使用 JPA、MyBatis 或其他 ORM 框架
}
```

### 2. 添加自定义命令处理

在 `DynamicCommandHandler` 中实现具体的 CRUD 操作：

```java
private DynamicCommandResponse handleCreate(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
    // 实现创建逻辑
}
```

### 3. 扩展 Schema 验证

在 `DynamicSchemaService` 中添加更复杂的 Schema 验证和管理逻辑。

## 最佳实践

1. **Schema 设计**: 合理设计你的 Schema 层次结构，确保 system.module.entity 的唯一性
2. **字段验证**: 在命令处理器中添加字段类型和约束验证
3. **权限控制**: 在查询和命令处理前添加权限验证逻辑
4. **缓存策略**: 对于频繁查询的 Schema 元数据使用适当的缓存策略
5. **错误处理**: 提供清晰的错误信息和适当的 HTTP 状态码

## 故障排除

### 常见问题

1. **Schema 未找到**: 确保已通过 REST API 注册了相应的 Schema
2. **字段类型错误**: 检查 fieldTypes 配置是否正确
3. **GraphQL 语法错误**: 使用 GraphiQL 界面进行调试

### 日志调试

应用配置了详细的日志输出，可以通过日志定位问题：

```yaml
logging:
  level:
    com.xiahou.yu.paaswebserver: DEBUG
```

## 性能优化

1. 使用适当的数据库索引
2. 实现分页查询避免大数据集返回
3. 使用 GraphQL DataLoader 避免 N+1 查询问题
4. 考虑使用 Redis 等缓存方案

## 安全考虑

1. 输入验证和 SQL 注入防护
2. GraphQL 查询复杂度限制
3. 接口访问权限控制
4. 敏感数据字段访问控制
