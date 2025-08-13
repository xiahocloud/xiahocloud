# PaaS Data Core Design 模块

## 概述

`paas-data-core-design` 是 PaaS 平台数据核心的设计时模块，负责根据元模型定义生成各种代码和脚本。该模块遵循洋葱架构设计理念，是统一的代码生成中心。

## 新架构依赖关系

```
paas-data-core-runtime (运行时层)
    ↓ 依赖
paas-data-core-design (设计时层) ⬅️ 当前模块 - 统一代码生成中心
    ↓ 依赖  
paas-meta-core (核心层)
```

根据您的要求，所有代码生成功能现在都集中在 `paas-data-core-design` 模块中：

## 主要功能

### 🎯 核心功能：统一代码生成
- **Java实体类生成** (从 paas-meta-core 迁移而来)
- **数据库脚本生成** (支持5种数据库)
- **完整项目代码包生成** (一键生成所有代码)
- **元模型分析和验证工具**

### 1. Java实体类生成
- 根据元模型生成JPA实体类
- 支持继承关系和属性引用
- 自动生成Lombok注解
- 包含完整的字段注解

### 2. 数据库脚本生成
- 支持多种数据库类型：MySQL、PostgreSQL、Oracle、H2、SQLite
- 根据元模型自动生成 DDL 脚本
- 包含表结构、索引、约束等完整定义
- 支持时间戳文件命名防冲突

### 3. 完整项目代码包生成 ⭐ 新核心功能
- 一键生成Java实体类 + 数据库脚本
- 并发生成多种数据库脚本
- 统一的输出目录结构
- 完整的项目就绪代码

## 核心类说明

### DesignTimeService (统一入口)
设计时服务管理器，提供所有代码生成功能的统一入口：

```java
// 创建设计时服务
DesignTimeService service = new DesignTimeService();

// 🎯 核心功能：生成完整项目代码包
service.generateFullProject("com.example.entity", "./output");

// 单独生成Java实体类
service.generateJavaEntities("com.example.entity", "./java");

// 生成所有数据库脚本
service.generateAllDatabaseScripts("./sql");

// 分析元模型
ModelAnalysisResult analysis = service.analyzeMetaModel();

// 验证设计
ValidationReport report = service.validateMetaModelDesign();
```

### JavaEntityGenerator (从 paas-meta-core 迁移)
Java实体类生成器：

```java
JavaEntityGenerator generator = new JavaEntityGenerator(
    "com.example.entity", "./output/java");
generator.generateAllEntities();
```

### DatabaseScriptGenerator (已有功能)
数据库脚本生成器，支持多种数据库类型：

```java
DatabaseScriptGenerator generator = new DatabaseScriptGenerator(
    "./output/sql", DatabaseScriptGenerator.DatabaseType.MYSQL);
generator.generateAllTableScripts();
```

## 使用示例

### 🚀 推荐用法：完整项目代码包生成

```java
public class Example {
    public static void main(String[] args) {
        // 创建设计时服务
        DesignTimeService service = new DesignTimeService();
        
        // 一键生成完整项目代码包
        service.generateFullProject(
            "com.xiahou.yu.paas.runtime.entity",  // Java包名
            "./generated/project"                  // 输出目录
        );
        
        // 生成结果：
        // ./generated/project/java/ - Java实体类
        // ./generated/project/sql/  - 所有数据库脚本
    }
}
```

### 基本分析和验证

```java
DesignTimeService service = new DesignTimeService();

// 分析元模型
ModelAnalysisResult analysis = service.analyzeMetaModel();
System.out.println("模型总数: " + analysis.totalModels);
System.out.println("数据表数: " + analysis.concreteModels);

// 验证设计
ValidationReport report = service.validateMetaModelDesign();
if (!report.isOverallValid) {
    System.out.println("发现 " + report.errorCount + " 个错误");
}
```

### 单独生成特定内容

```java
DesignTimeService service = new DesignTimeService();

// 仅生成Java实体类
service.generateJavaEntities("com.example.entity", "./java");

// 仅生成MySQL脚本
service.generateDatabaseScripts(
    DatabaseScriptGenerator.DatabaseType.MYSQL, "./mysql");
```

## 生成的代码结构

### Java实体类特性
- 使用Lombok注解简化代码
- JPA注解支持
- 继承关系正确映射
- 字段注释完整

### 数据库脚本特性
- 时间戳文件命名：`create_tables_mysql_20241205_143022.sql`
- 完整的DDL语句
- 索引和约束自动生成
- 支持多数据库方言

## 依赖关系配置

```groovy
// paas-data-core-design/build.gradle
dependencies {
    // 核心元模型依赖
    implementation 'com.xiahou.yu:paas-meta-core'
    
    // 运行时模块依赖
    implementation project(':paas-data-core-runtime')
    
    // 其他依赖...
}
```

```groovy
// paas-data-core-runtime/build.gradle (需要更新)
dependencies {
    // 设计时模块依赖
    implementation project(':paas-data-core-design')
    
    // 其他依赖...
}
```

## 运行演示

运行 `DesignTimeDemo` 类查看完整功能演示：

```bash
cd paas-data-core/paas-data-core-design
./gradlew run -PmainClass="com.xiahou.yu.paasdatacore.design.generator.DesignTimeDemo"
```

## 架构优势

✅ **统一代码生成中心**：所有生成功能集中管理  
✅ **依赖关系清晰**：runtime → design → meta-core  
✅ **职责分离明确**：核心解析与代码生成分离  
✅ **扩展性强**：易于添加新的生成器  
✅ **并发支持**：多种输出并行生成  

## 模块间职责

| 模块 | 职责 | 主要功能 |
|------|------|----------|
| **paas-meta-core** | 核心层 | 元模型解析、属性池管理、核心逻辑 |
| **paas-data-core-design** | 设计时层 | 代码生成、脚本生成、分析验证 |
| **paas-data-core-runtime** | 运行时层 | 数据操作、业务逻辑、运行时服务 |

## 版本历史

- v1.0.0: 初始版本，数据库脚本生成
- v1.1.0: 增加多数据库类型支持  
- v1.2.0: 添加设计验证和分析功能
- v2.0.0: **架构重构**，集成Java实体类生成，成为统一代码生成中心
