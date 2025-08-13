# 元模型（MetaModel）说明文档

本项目中的 `metamodel.xml` 文件用于定义和约束所有的模型，是整个元数据建模体系的基础。其结构和内容如下：

## 1. MetaModel 根节点
- 作为所有模型定义的容器。
- 包含版本信息（Version）和所有模型（Models）。

## 2. Models 节点
- 用于包裹所有的模型（Model）。

## 3. Model 节点
每个 Model 节点描述一个具体的模型类型，支持继承（通过 `extends` 属性）。主要字段如下：
- `id`：模型唯一标识。
- `Name`：模型名称。
- `Desc`：模型描述。
- `ResourcePath`：模型定义的资源文件路径。
- `extends`（可选）：继承的父模型。

### 主要模型示例
- **AbstractModel**：抽象模型，定义模型的通用部分。
- **FieldModel**（继承自 AbstractModel）：字段模型，定义字段的基础。
- **PageModel**（继承自 AbstractModel）：页面模型，定义页面内容。
- **EntityModel**（继承自 AbstractModel）：实体模型，定义持久化内容。

## 4. 示例结构
```xml
<MetaModel>
    <Version>0.0.1</Version>
    <Models>
        <Model>
            <id>AbstractModel</id>
            <Name>抽象模型</Name>
            <Desc>用于定义模型的通用部分</Desc>
            <ResourcePath>/models/AbstractModel.xml</ResourcePath>
        </Model>
        <Model extends="AbstractModel">
            <id>FieldModel</id>
            <Name>字段模型</Name>
            <Desc>用于定义字段的基础</Desc>
            <ResourcePath>/models/FieldModel.xml</ResourcePath>
        </Model>
        ...
    </Models>
</MetaModel>
```

## 5. 设计意义
- 通过元模型统一描述所有模型类型，便于扩展和约束。
- 支持模型继承，提升复用性和灵活性。
- 通过资源路径关联具体模型定义文件。

---

## AbstractModel.xml 结构说明

`AbstractModel.xml` 是所有模型的基础定义，描述了通用的属性集合。其主要内容如下：

| 属性名   | 中文名称   | 数据类型 | 说明 |
|----------|------------|----------|------|
| id       | 全局唯一标识符 | Long     | 技术上的唯一属性 |
| key      | 标识         | String   | 租户内可见的唯一属性 |
| tenant   | 租户编码     | String   | 租户唯一编码 |
| name     | 名称         | String   | 中文名称 |
| description     | 描述         | String   | 关于属性的描述 |
| version  | 版本         | String   | 版本号 |
| type     | 类型         | String   | 描述当前模型的类型 |
| status   | 状态         | Integer  | 描述元素的状态 |
| enable   | 启用         | Integer  | 启用状态，null/1：启用，0：禁用 |
| visible  | 可见         | Integer  | 可见性，null/1：可见，0：不可见 |
| app      | 应用标识     | String   | 默认值 std |
| sys      | 系统标识     | Integer  | null/1：系统，0：自定义 |

### 示例片段
```xml
<Property>
    <Id>id</Id>
    <Name>全局唯一标识符</Name>
    <DataType>Long</DataType>
    <Desc>技术上的唯一属性</Desc>
</Property>
```

这些属性为所有模型提供了统一的基础字段，便于模型的扩展和管理。

---
如需扩展模型，只需在 `metamodel.xml` 中新增 Model 节点并指定相关属性。
