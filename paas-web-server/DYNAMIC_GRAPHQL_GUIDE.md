# PaaS 动态 GraphQL 接口层使用指南

## 概述

本项目基于 CQRS (Command Query Responsibility Segregation) 模式实现了一个动态的 GraphQL 接口层，专为 PaaS 平台设计。该接口层支持动态 Schema 管理，无需预定义固定的 GraphQL Schema。

## 架构设计

### 核心组件

1. **DynamicQueryHandler** - 处理所有查询操作（CQRS 的 Query 端）
2. **DynamicCommandHandler** - 处理所有变更操作（CQRS 的 Command 端）
3. **DynamicSchemaService** - 管理动态 Schema 元数据
4. **DynamicGraphQLController** - GraphQL 接口控制器
5. **SchemaManagementController** - Schema 管理 REST 接口

### 数据结构

动态查询模式遵循以下结构：
```json
{
  "system": "xia",      // 系统标识
  "module": "hr",       // 模块标识
  "context": "api",     // 上下文
  "app": "invoice",     // 应用标识
  "aggr": "employee",   // 聚合根
  "entity": "user",     // 实体名称
  "filter": "xxx",      // 过滤条件
  "fields": ["field1", "field2"]  // 返回字段
}
```

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
