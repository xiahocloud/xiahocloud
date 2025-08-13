# PaaS 低代码平台：元模型设计与实现文档 (修订版)

### 1. 引言

本文档旨在详细阐述 PaaS 低代码平台中元模型（Metamodel）的设计理念、结构定义、核心逻辑及其在整个平台架构中的作用。元模型作为低代码平台的核心“数据字典”和“蓝图”，承载了对应用所需业务模型、组件及属性的统一抽象和描述，是平台实现自动化代码生成、数据库操作和运行时数据管理的基础。

本修订版文档将基于你提供的具体 XML 结构进行解释，特别关注 `AbstractProperties.xml`、`AbstractModel.xml`、`FieldModel.xml`、`PageModel.xml` 和 `Metamodel.xml` 中的细节。

### 2. 元模型核心概念与文件结构

元模型定义了平台中所有可用的**模型**、**组件**和**属性**的结构和相互关系。

*   **元模型根配置 (`Metamodel.xml`)：** 整个元模型的入口点，定义了所有顶层模型及其元信息。
*   **模型 (`Model`)：** 对应业务实体或抽象结构，是数据存储和业务逻辑操作的基本单位。一个模型可以包含自身的属性和组件。
*   **组件 (`Component`)：** 对应可复用的界面元素或业务功能模块。组件可以继承，也可以引用属性。
*   **属性 (`Property`)：** 最基础的元数据单元，定义了数据类型、名称、描述等。属性的定义和引用是分离的。
*   **属性引用 (`Ref`)：** 在模型或组件中，通过引用（`Ref`）来实际使用已定义的属性。

#### 文件组织结构概览：

*   `Metamodel.xml`: 元模型入口，定义顶层模型列表。
*   `models/`: 存放具体的模型定义文件。
    *   `AbstractModel.xml`: 定义通用的抽象模型及其基础属性引用。
    *   `PageModel.xml`: 定义页面模型及其特有属性和组件。
    *   `DataModel.xml`: (未提供，但根据 Metamodel.xml 存在) 定义数据模型。
    *   `FieldModel.xml`: 定义字段模型，其中包含组件（字段类型）的定义。
*   `models/props/`: 存放抽象属性定义文件。
    *   `AbstractProperties.xml`: 一个集中式的属性库，定义了平台中所有可复用的基础属性。

### 3. 元模型配置 (`Metamodel.xml`)

`Metamodel.xml` 是元模型的总入口。它定义了平台中所有顶层模型的元数据，并指示如何加载这些模型的具体结构。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<MetaModel>
    <Version>0.0.1</Version>
    <Models>
        <Model import="/models/AbstractModel.xml">
            <Id>AbstractModel</Id>
            <Name>抽象模型</Name>
            <Desc>用于定义模型的通用部分</Desc>
        </Model>
        <!-- 其他 Model 定义... -->
    </Models>
</MetaModel>
```

*   **根标签：** `<MetaModel>`
    *   `Version`：元模型的版本号。
*   **模型列表：** `<Models>`
    *   包含多个 `<Model>` 标签，每个标签代表一个顶层模型。
*   **顶层模型定义：** `<Model>`
    *   `import`：**必填**。指定该模型的具体结构定义所在的 XML 文件路径。该路径指向的文件根标签将是 `<Model>` 或 `<Component>`（如果直接引用属性库）。
    *   `Id`：**必填**。模型的唯一标识符。
        *   **映射到 Java：** 作为要创建的 Java 类的类名。
        *   **映射到数据库：** 如果是具体模型，将用于生成表名 `t_{snake_case(Id)}`。
    *   `Name`：模型的显示名称。
    *   `Desc`：模型的描述。
    *   `extends`：**可选**。指定该模型继承的父模型 `Id`。类似于 Java 继承，子模型将拥有父模型中所有被 `Ref` 引用的属性。

**注意：** 这里的 `<Model>` 标签主要用于在 `Metamodel.xml` 中声明和组织整个平台范围内的模型，其 `Id`、`Name`、`Desc` 等属性是该模型的**元数据**，而其内部的具体结构（如包含哪些属性、哪些组件）则由 `import` 属性指向的文件来定义。

### 4. 属性定义 (`AbstractProperties.xml`)

`AbstractProperties.xml` 是一个独立的属性库文件，它集中声明了平台中各种类型的通用属性，包括基础属性、页面样式属性、数据库字段/表属性等。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<Component> <!-- 注意：根标签是 Component，表示这是一个可被其他组件或模型引用的属性集合 -->
    <Propertys>
        <!-- 基础属性 -->
        <Property>
            <Id>id</Id>
            <Name>全局唯一标识符</Name>
            <DataType>Long</DataType>
            <Desc>技术上的唯一属性</Desc>
        </Property>
        <!-- CSS3 页面样式属性 -->
        <Property>
            <Id>display</Id>
            <Name>显示类型</Name>
            <DataType>String</DataType>
            <Scope>PageModel</Scope> <!-- 新增的 Scope 属性，表示该属性适用于 PageModel 及其组件 -->
            <Desc>元素显示类型：block, inline, flex, grid等</Desc>
        </Property>
        <!-- 数据库字段属性 -->
        <Property>
            <Id>columnName</Id>
            <Name>列名</Name>
            <DataType>String</DataType>
            <Scope>DataModel</Scope> <!-- 表示该属性适用于 DataModel 及其组件 -->
            <Desc>数据库字段名</Desc>
        </Property>
        <!-- 其他 Property 声明... -->
    </Propertys>
</Component>
```

*   **根标签：** `<Component>`
    *   **重要说明：** 尽管名称是 `Component`，但在 `AbstractProperties.xml` 中，它充当一个**全局属性定义容器**的角色，其内部的 `<Property>` 标签定义了可在整个平台范围内被复用的属性元数据。
*   **属性列表容器：** `<Propertys>` (注意是 `Propertys` 而非 `Properties`)
    *   包含多个 `<Property>` 标签。
*   **属性定义：** `<Property>`
    *   `Id`：**必填**。属性的唯一标识符，用于在其他模型或组件中通过 `Ref` 引用。
    *   `Name`：属性的显示名称。
    *   `DataType`：**必填**。对应的 Java 数据类型（如 `Long`, `String`, `Integer`, `Float`, `Boolean`, `java.util.Date` 等）。
    *   `Desc`：属性的描述信息。
    *   `Default`：**可选**。属性的默认值（如 `app` 属性的 `std`）。
    *   `Scope`：**可选**。**新增的关键属性。** 表示该属性主要适用于哪种类型的模型或组件。这有助于在可视化设计器中进行过滤和提示，例如 `display` 属性只对 `PageModel` 及其 UI 组件有意义，而 `columnName` 属性只对 `DataModel` 中的字段有意义。

**注意：** 在这里，`Property` 定义中并未直接包含 `dbType`、`javaComment`、`dbComment` 等信息。这意味着：
*   `dbType` 可能需要根据 `DataType` 进行映射推导，或者在实际引用（`Ref`）时进行补充。
*   `javaComment` 和 `dbComment` 可能会直接使用 `Desc` 的内容，或由代码生成器根据 `Desc` 自动生成。

### 5. 模型定义 (`AbstractModel.xml`, `PageModel.xml`)

模型定义文件（如 `AbstractModel.xml`, `PageModel.xml`）是构建应用核心数据结构和页面结构的蓝图。

#### 5.1 抽象模型示例 (`AbstractModel.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Model> <!-- 注意：根标签是 Model，但自身不带 Id、Name 等属性，这些由 Metamodel.xml 中的 Model 标签提供 -->
    <Properties>
        <!-- 定义 AbstractModel 自身特有的属性，或覆盖 AbstractProperties 中同名属性的某些定义 -->
        <!-- 示例中这些 Property 与 AbstractProperties.xml 中的同名 Property 重复，需要解析时处理优先级 -->
        <Property><Id>id</Id><Name>全局唯一标识符</Name><DataType>Long</DataType><Desc>技术上的唯一属性</Desc></Property>
        <!-- 其他 Property 声明... -->
    </Properties>
    <Refs>
        <!-- AbstractModel 实际包含的属性引用，这些 Id 必须在当前 Model 的 Properties 或其导入的属性库中声明过 -->
        <Ref>id</Ref>
        <Ref>key</Ref>
        <Ref>tenant</Ref>
        <!-- 其他 Ref 引用... -->
    </Refs>
</Model>
```

*   **根标签：** `<Model>` (此处的 `Id`、`Name`、`Desc` 由 `Metamodel.xml` 中的 `<Model>` 标签提供)。
*   **模型内属性声明：** `<Properties>`
    *   包含模型自身特有的 `<Property>` 声明。这里的 `Property` 结构与 `AbstractProperties.xml` 中的相同。如果存在同 `Id` 的 `Property`，则这里的定义可能会覆盖导入属性库中的定义，或作为补充。
*   **模型属性引用：** `<Refs>`
    *   包含多个 `<Ref>` 标签。
    *   `Ref`：**仅仅包含属性的 `Id`。** 这表示该模型实际“拥有”哪些属性。解析器需要根据这个 `Id` 到当前模型自身的 `<Properties>` 或所有已加载的属性库（如 `AbstractProperties.xml`）中查找对应的属性定义。
    *   **映射到 Java：** `Ref` 的 `Id` 将作为生成的 Java 类中的成员变量名。
    *   **映射到数据库：** `Ref` 的 `Id` 将作为生成的数据库表中的列名（转换为 `snake_case` 格式）。

#### 5.2 页面模型示例 (`PageModel.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Model>
    <Properties>
        <!-- PageModel 自身特有的样式属性 -->
        <Property><Id>width</Id><Name>宽度</Name><DataType>String</DataType><Desc>页面宽度</Desc></Property>
        <!-- 其他 PageModel 特有属性... -->
    </Properties>
    <Components>
        <!-- PageModel 中包含的页面组件 -->
        <Component>
            <Id>flex</Id>
            <Name>Flex面板</Name>
            <Desc>Flex布局容器</Desc>
        </Component>
        <Component>
            <Id>form</Id>
            <Name>Form表单</Name>
            <Desc>表单容器</Desc>
        </Component>
    </Components>
</Model>
```

*   **模型内组件容器：** `<Components>`
    *   包含多个 `<Component>` 标签，定义了该模型内部可用的组件。这些组件可以是容器，也可以是更小的 UI 元素。

### 6. 组件定义 (`FieldModel.xml`, `PageModel.xml`内部组件)

组件是可复用的 UI 元素或业务逻辑单元。它们可以有自己的属性，也可以继承其他组件。

#### 6.1 字段模型中的组件示例 (`FieldModel.xml`)

`FieldModel.xml` 定义了各种“字段”类型的组件，这些字段可以被页面模型中的表单组件引用。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Model> <!-- FieldModel 也是一个 Model，但它主要用于聚合 Component -->
    <Components>
        <Component import="/models/props/AbstractProperties.xml"> <!-- 该组件导入属性库 -->
            <Id>Text</Id>
            <Name>文本</Name>
            <Desc>文本类型</Desc>
            <Refs>
                <!-- Text 组件实际引用的属性。这些属性来自 AbstractProperties.xml -->
                <Ref>id</Ref>
                <Ref>key</Ref>
                <Ref>tenant</Ref>
                <Ref>name</Ref>
                <Ref>width</Ref>
                <Ref>height</Ref>
                <Ref>columnName</Ref>
                <Ref>columnType</Ref>
                <Ref>columnLength</Ref>
                <Ref>nullable</Ref>
            </Refs>
        </Component>

        <Component extends="Text"> <!-- ShortText 组件继承 Text 组件 -->
            <Id>ShortText</Id>
            <Name>单行文本</Name>
            <Desc>单行文本字段</Desc>
        </Component>
        <!-- 其他 Component 定义... -->
    </Components>
    <Properties>
        <!-- FieldModel 自身没有 Property 声明 -->
    </Properties>
</Model>
```

*   **组件定义：** `<Component>`
    *   `Id`：**必填**。组件的唯一标识符。
    *   `Name`：组件的显示名称。
    *   `Desc`：组件的描述。
    *   `import`：**可选**。指定该组件要导入的属性库文件（例如 `AbstractProperties.xml`），这样组件就可以引用这些库中定义的属性。
    *   `extends`：**可选**。指定该组件继承的父组件 `Id`。继承的组件将拥有父组件中所有被 `Ref` 引用的属性。
    *   **组件属性引用：** `<Refs>` (位于 `<Component>` 内部)
        *   包含多个 `<Ref>` 标签，其逻辑与模型中的 `Refs` 标签相同，用于引用已声明的属性。

### 7. 属性解析与引用逻辑

元模型的核心在于如何将分散的 `Property` 声明与 `Ref` 引用关联起来。

1.  **全局属性池：** `paas-meta-core` 在解析时，会首先加载 `AbstractProperties.xml`，将其中的所有 `<Property>` 定义放入一个全局可访问的“属性池”中。
2.  **模型/组件内部属性：** 当解析模型或组件自身的 `<Properties>` 时，这些属性也会被加入到其自身的属性作用域中，并可能覆盖全局属性池中同 `Id` 的属性定义（如果存在优先级规则）。
3.  **`Ref` 查找：** 当遇到 `<Ref>{propertyId}</Ref>` 标签时，解析器会按以下顺序查找对应的 `Property` 定义：
    *   首先在当前模型/组件自身的 `<Properties>` 中查找。
    *   如果未找到，则在其 `import` 的属性库（如 `AbstractProperties.xml`）中查找。
    *   如果仍未找到，则在其 `extends` 的父模型/组件中查找（递归向上）。
    *   如果最终未找到，则报告错误。

### 8. 元模型到代码与数据库的映射逻辑 (修订)

#### 8.1 Java 类生成规则

*   **`Model` 对应 Java 类：**
    *   **类名：** 由 `Metamodel.xml` 中 `<Model>` 标签的 `Id` 属性确定。
    *   **继承：** `extends` 属性决定 Java 类的继承关系。
    *   **成员变量：** 由模型自身的 `<Refs>` 以及所有继承而来的 `<Ref>` 对应的属性生成。
        *   **变量名：** `Ref` 标签的 `Id` 属性值。
        *   **变量类型：** `Ref` 标签所引用 `Property` 的 `DataType` 属性值。
        *   **注释：** `Ref` 标签或其引用 `Property` 的 `Desc` 属性值（作为 Java Doc 或行注释）。
        *   **默认值：** `Ref` 标签所引用 `Property` 的 `Default` 属性值。
*   **`Component` 对应 Java 类：** (假设组件也生成 Java 类，例如 UI 组件的配置类)
    *   **类名：** 由 `<Component>` 标签的 `Id` 属性确定。
    *   **继承：** `extends` 属性决定 Java 类的继承关系。
    *   **成员变量：** 由组件自身的 `<Refs>` 以及所有继承而来的 `<Ref>` 对应的属性生成，规则同模型。

#### 8.2 数据库表生成规则

*   **表名：** 仅为 **非抽象模型** 创建数据库表。表名规则为 `t_{snake_case(ModelId)}`。
*   **表字段：**
    *   由模型（非抽象）自身的 `<Refs>` 以及所有继承而来的 `<Ref>` 对应的属性生成。
    *   **字段名：** `Ref` 标签的 `Id` 属性值转换为 `snake_case` 格式。
    *   **字段类型：** `Ref` 标签所引用 `Property` 的 `DataType` 属性值（需要通过映射转换为具体的数据库类型，例如 `Long` -> `BIGINT`, `String` -> `VARCHAR(255)`。**请注意，你提供的 Property 定义中没有 `dbType`，这需要生成器进行智能推导或有额外的配置。**
    *   **注释：** `Ref` 标签所引用 `Property` 的 `Desc` 属性值。
    *   **其他数据库约束：** `nullable`, `primaryKey`, `unique`, `defaultValue`, `autoIncrement` 等属性将根据 `Ref` 引用的 `Property` 定义（特别是那些 `Scope="DataModel"` 的属性）来生成相应的 DDL。

### 9. 元模型在 PaaS 数据核心模块中的集成 (与之前一致，但更精确)

整个 PaaS 平台架构采用洋葱模型，`paas-meta-core` 作为核心层，承载着元模型解析和抽象的核心职责。

```mermaid
graph TD
    subgraph PaaS 数据核心 (paas-data-core 父工程)
        A[paas-meta-core (核心层)] --> B[paas-data-core-design (设计时)]
        B --> C[paas-data-core-runtime (运行时)]
    end

    D[Metamodel.xml / 各模型与属性定义文件] --> A
    E[开发人员 / 设计器] --> B
    F[低代码应用运行时] --> C
    C -- "数据库操作" --> G[数据库]
```

#### 9.1 `paas-meta-core` (核心层)

*   **职责：**
    *   **全面解析：** 负责读取并解析 `Metamodel.xml`，以及所有通过 `import` 引用的模型、组件和属性定义文件（包括 `AbstractProperties.xml`）。
    *   **属性池构建：** 将所有声明的 `Property` 放入一个统一的、可按 `Id` 查找的内存池中，同时记录其 `Scope` 等元数据。
    *   **元数据模型构建：** 构建复杂的内存对象图，表示模型、组件及其引用的属性之间的关系，处理继承、引用查找和属性覆盖逻辑。
    *   **提供 API：** 提供一套清晰的 API 接口，供其他模块查询和获取元模型中的模型、组件、属性及其关联信息。

#### 9.2 `paas-data-core` (父工程)

*   **职责：**
    *   **模块聚合：** 作为所有数据相关子模块的父工程。
    *   **依赖引用：** 引用 `paas-meta-core`，以便其子模块能利用元模型服务。

#### 9.3 `paas-data-core-design` (设计时)

*   **职责：**
    *   **代码生成：** 根据 `paas-meta-core` 解析的元模型信息，自动生成 `paas-data-core-runtime` 所需的 Java 源代码（实体类、DAO、Service 等）。此阶段需要根据 `Ref` 引用的 `Property` 的 `DataType` 来生成正确的 Java 字段类型，并根据 `Desc` 生成 Java 注释。
    *   **数据库 DDL 生成：** 根据元模型中非抽象模型及其引用属性的定义（尤其是带有 `DataModel` `Scope` 的属性），生成数据库表的 DDL 脚本。此阶段需要将 `DataType` 映射到具体的 `dbType`，并根据 `Desc` 生成字段注释，处理 `nullable`, `primaryKey`, `unique` 等约束。
    *   **元模型校验：** 对元模型定义进行深层校验，确保属性引用有效、继承关系合法、数据类型一致性等。

#### 9.4 `paas-data-core-runtime` (运行时)

*   **职责：**
    *   **模型实例化：** 加载并使用由 `paas-data-core-design` 生成的 Java 实体类。
    *   **数据操作：** 在应用运行时，通过生成的 DAO 层执行实际的数据库 CRUD 操作。
    *   **统一数据访问：** 提供基于生成实体类的统一数据访问接口，供低代码平台生成的应用调用。

### 10. 总结

新的元模型设计，特别是引入了独立的属性库 (`AbstractProperties.xml`)、明确的 `Ref` 引用机制和 `Scope` 属性，以及模型和组件的继承能力，极大地增强了平台的灵活性和可扩展性。这种分层、模块化的元模型定义方式，使得属性管理更加集中和复用，同时为不同领域（如前端 UI 样式、后端数据存储）的属性提供了明确的归属和管理方式。`paas-meta-core` 作为元模型的解析核心，将这些抽象定义转化为可操作的元数据，驱动后续的代码生成和运行时行为，是实现低代码平台高效、灵活开发的关键。