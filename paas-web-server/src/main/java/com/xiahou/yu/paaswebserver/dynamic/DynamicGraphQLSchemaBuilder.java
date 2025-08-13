package com.xiahou.yu.paaswebserver.dynamic;

import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 动态GraphQL模式构建器
 * @author system
 */
@Service
public class DynamicGraphQLSchemaBuilder {

    @Autowired
    private EntityDefinitionService entityDefinitionService;

    @Autowired
    private DynamicEntityService dynamicEntityService;

    public GraphQLSchema buildSchema() {
        List<EntityDefinition> entityDefinitions = entityDefinitionService.getAllEntityDefinitions();

        StringBuilder schemaBuilder = new StringBuilder();

        // 构建基础类型
        schemaBuilder.append(buildBaseTypes());

        // 构建动态实体类型
        for (EntityDefinition entityDef : entityDefinitions) {
            schemaBuilder.append(buildEntityType(entityDef));
            schemaBuilder.append(buildInputTypes(entityDef));
        }

        // 构建Query和Mutation根类型
        schemaBuilder.append(buildQueryType(entityDefinitions));
        schemaBuilder.append(buildMutationType(entityDefinitions));

        // 解析schema
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schemaBuilder.toString());

        // 构建运行时绑定
        RuntimeWiring runtimeWiring = buildRuntimeWiring(entityDefinitions);

        // 生成GraphQL Schema
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    private String buildBaseTypes() {
        return """
            scalar DateTime
            scalar JSON
            
            interface Node {
                id: ID!
            }
            
            type PageInfo {
                hasNextPage: Boolean!
                hasPreviousPage: Boolean!
                startCursor: String
                endCursor: String
            }
            
            input PaginationInput {
                first: Int
                after: String
                last: Int
                before: String
            }
            
            input FilterInput {
                field: String!
                operator: FilterOperator!
                value: String
            }
            
            enum FilterOperator {
                EQUALS
                NOT_EQUALS
                CONTAINS
                STARTS_WITH
                ENDS_WITH
                GREATER_THAN
                LESS_THAN
                GREATER_THAN_OR_EQUAL
                LESS_THAN_OR_EQUAL
                IN
                NOT_IN
                IS_NULL
                IS_NOT_NULL
            }
            
            input SortInput {
                field: String!
                direction: SortDirection!
            }
            
            enum SortDirection {
                ASC
                DESC
            }
            
            """;
    }

    private String buildEntityType(EntityDefinition entityDef) {
        StringBuilder typeBuilder = new StringBuilder();

        typeBuilder.append("type ").append(capitalize(entityDef.getEntityName())).append(" implements Node {\n");
        typeBuilder.append("  id: ID!\n");

        for (FieldDefinition field : entityDef.getFields()) {
            typeBuilder.append("  ").append(field.getFieldName()).append(": ");
            typeBuilder.append(mapFieldTypeToGraphQL(field));
            typeBuilder.append("\n");
        }

        typeBuilder.append("  createdAt: DateTime!\n");
        typeBuilder.append("  updatedAt: DateTime!\n");
        typeBuilder.append("}\n\n");

        // 构建连接类型用于分页
        typeBuilder.append("type ").append(capitalize(entityDef.getEntityName())).append("Connection {\n");
        typeBuilder.append("  edges: [").append(capitalize(entityDef.getEntityName())).append("Edge!]!\n");
        typeBuilder.append("  pageInfo: PageInfo!\n");
        typeBuilder.append("  totalCount: Int!\n");
        typeBuilder.append("}\n\n");

        typeBuilder.append("type ").append(capitalize(entityDef.getEntityName())).append("Edge {\n");
        typeBuilder.append("  node: ").append(capitalize(entityDef.getEntityName())).append("!\n");
        typeBuilder.append("  cursor: String!\n");
        typeBuilder.append("}\n\n");

        return typeBuilder.toString();
    }

    private String buildInputTypes(EntityDefinition entityDef) {
        StringBuilder inputBuilder = new StringBuilder();
        String entityName = capitalize(entityDef.getEntityName());

        // Create Input
        inputBuilder.append("input Create").append(entityName).append("Input {\n");
        for (FieldDefinition field : entityDef.getFields()) {
            if (!field.getFieldName().equals("id")) {
                inputBuilder.append("  ").append(field.getFieldName()).append(": ");
                inputBuilder.append(mapFieldTypeToGraphQLInput(field));
                inputBuilder.append("\n");
            }
        }
        inputBuilder.append("}\n\n");

        // Update Input
        inputBuilder.append("input Update").append(entityName).append("Input {\n");
        for (FieldDefinition field : entityDef.getFields()) {
            if (!field.getFieldName().equals("id")) {
                inputBuilder.append("  ").append(field.getFieldName()).append(": ");
                inputBuilder.append(mapFieldTypeToGraphQLInput(field, false)); // 更新时字段可选
                inputBuilder.append("\n");
            }
        }
        inputBuilder.append("}\n\n");

        return inputBuilder.toString();
    }

    private String buildQueryType(List<EntityDefinition> entityDefinitions) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("type Query {\n");

        for (EntityDefinition entityDef : entityDefinitions) {
            String entityName = entityDef.getEntityName();
            String capitalizedName = capitalize(entityName);

            // 单个实体查询
            queryBuilder.append("  ").append(entityName).append("(id: ID!): ").append(capitalizedName).append("\n");

            // 列表查询（带分页、过滤、排序）
            queryBuilder.append("  ").append(entityName).append("s(")
                       .append("pagination: PaginationInput, ")
                       .append("filters: [FilterInput!], ")
                       .append("sort: [SortInput!]")
                       .append("): ").append(capitalizedName).append("Connection!\n");
        }

        // 通用查询
        queryBuilder.append("  entity(type: String!, id: ID!): JSON\n");
        queryBuilder.append("  entities(type: String!, pagination: PaginationInput, filters: [FilterInput!], sort: [SortInput!]): JSON!\n");
        queryBuilder.append("  entityDefinitions: [EntityDefinition!]!\n");

        queryBuilder.append("}\n\n");

        // EntityDefinition类型定义
        queryBuilder.append("type EntityDefinition {\n");
        queryBuilder.append("  id: ID!\n");
        queryBuilder.append("  entityName: String!\n");
        queryBuilder.append("  displayName: String\n");
        queryBuilder.append("  description: String\n");
        queryBuilder.append("  fields: [FieldDefinition!]!\n");
        queryBuilder.append("}\n\n");

        queryBuilder.append("type FieldDefinition {\n");
        queryBuilder.append("  id: ID!\n");
        queryBuilder.append("  fieldName: String!\n");
        queryBuilder.append("  fieldType: String!\n");
        queryBuilder.append("  required: Boolean!\n");
        queryBuilder.append("  list: Boolean!\n");
        queryBuilder.append("  defaultValue: String\n");
        queryBuilder.append("  displayName: String\n");
        queryBuilder.append("  description: String\n");
        queryBuilder.append("  referenceEntity: String\n");
        queryBuilder.append("}\n\n");

        return queryBuilder.toString();
    }

    private String buildMutationType(List<EntityDefinition> entityDefinitions) {
        StringBuilder mutationBuilder = new StringBuilder();
        mutationBuilder.append("type Mutation {\n");

        for (EntityDefinition entityDef : entityDefinitions) {
            String entityName = entityDef.getEntityName();
            String capitalizedName = capitalize(entityName);

            // CRUD操作
            mutationBuilder.append("  create").append(capitalizedName)
                          .append("(input: Create").append(capitalizedName).append("Input!): ")
                          .append(capitalizedName).append("!\n");

            mutationBuilder.append("  update").append(capitalizedName)
                          .append("(id: ID!, input: Update").append(capitalizedName).append("Input!): ")
                          .append(capitalizedName).append("!\n");

            mutationBuilder.append("  delete").append(capitalizedName)
                          .append("(id: ID!): Boolean!\n");
        }

        // 通用操作
        mutationBuilder.append("  createEntity(type: String!, data: JSON!): JSON!\n");
        mutationBuilder.append("  updateEntity(type: String!, id: ID!, data: JSON!): JSON!\n");
        mutationBuilder.append("  deleteEntity(type: String!, id: ID!): Boolean!\n");

        // 实体定义管理
        mutationBuilder.append("  createEntityDefinition(entityName: String!, displayName: String, description: String): EntityDefinition!\n");
        mutationBuilder.append("  updateEntityDefinition(id: ID!, displayName: String, description: String): EntityDefinition!\n");
        mutationBuilder.append("  deleteEntityDefinition(id: ID!): Boolean!\n");
        mutationBuilder.append("  addFieldToEntity(entityId: ID!, fieldName: String!, fieldType: String!, required: Boolean, defaultValue: String): FieldDefinition!\n");

        mutationBuilder.append("}\n\n");

        return mutationBuilder.toString();
    }

    private RuntimeWiring buildRuntimeWiring(List<EntityDefinition> entityDefinitions) {
        RuntimeWiring.Builder wiringBuilder = RuntimeWiring.newRuntimeWiring();

        // 注册Query字段
        wiringBuilder.type("Query", builder -> {
            for (EntityDefinition entityDef : entityDefinitions) {
                String entityName = entityDef.getEntityName();
                builder.dataFetcher(entityName, env -> dynamicEntityService.findById(entityName, env.getArgument("id")));
                builder.dataFetcher(entityName + "s", env -> dynamicEntityService.findAllWithPagination(
                    entityName,
                    env.getArgument("pagination"),
                    env.getArgument("filters"),
                    env.getArgument("sort")
                ));
            }

            builder.dataFetcher("entity", env -> dynamicEntityService.findById(env.getArgument("type"), env.getArgument("id")));
            builder.dataFetcher("entities", env -> dynamicEntityService.findAllWithPagination(
                env.getArgument("type"),
                env.getArgument("pagination"),
                env.getArgument("filters"),
                env.getArgument("sort")
            ));
            builder.dataFetcher("entityDefinitions", env -> entityDefinitionService.getAllEntityDefinitions());

            return builder;
        });

        // 注册Mutation字段
        wiringBuilder.type("Mutation", builder -> {
            for (EntityDefinition entityDef : entityDefinitions) {
                String entityName = entityDef.getEntityName();
                String capitalizedName = capitalize(entityName);

                builder.dataFetcher("create" + capitalizedName, env ->
                    dynamicEntityService.create(entityName, env.getArgument("input")));
                builder.dataFetcher("update" + capitalizedName, env ->
                    dynamicEntityService.update(entityName, env.getArgument("id"), env.getArgument("input")));
                builder.dataFetcher("delete" + capitalizedName, env ->
                    dynamicEntityService.delete(entityName, env.getArgument("id")));
            }

            // 通用操作
            builder.dataFetcher("createEntity", env ->
                dynamicEntityService.create(env.getArgument("type"), env.getArgument("data")));
            builder.dataFetcher("updateEntity", env ->
                dynamicEntityService.update(env.getArgument("type"), env.getArgument("id"), env.getArgument("data")));
            builder.dataFetcher("deleteEntity", env ->
                dynamicEntityService.delete(env.getArgument("type"), env.getArgument("id")));

            // 实体定义管理
            builder.dataFetcher("createEntityDefinition", env ->
                entityDefinitionService.createEntityDefinition(
                    env.getArgument("entityName"),
                    env.getArgument("displayName"),
                    env.getArgument("description")));

            return builder;
        });

        // 注册标量类型
        wiringBuilder.scalar(GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime scalar")
            .coercing(new DateTimeCoercing())
            .build());

        wiringBuilder.scalar(GraphQLScalarType.newScalar()
            .name("JSON")
            .description("JSON scalar")
            .coercing(new JsonCoercing())
            .build());

        return wiringBuilder.build();
    }

    private String mapFieldTypeToGraphQL(FieldDefinition field) {
        String baseType = mapBaseType(field.getFieldType());

        if (field.getFieldType() == FieldDefinition.FieldType.REFERENCE && field.getReferenceEntity() != null) {
            baseType = capitalize(field.getReferenceEntity());
        }

        if (field.getList()) {
            baseType = "[" + baseType + "!]";
        }

        if (field.getRequired()) {
            baseType += "!";
        }

        return baseType;
    }

    private String mapFieldTypeToGraphQLInput(FieldDefinition field) {
        return mapFieldTypeToGraphQLInput(field, field.getRequired());
    }

    private String mapFieldTypeToGraphQLInput(FieldDefinition field, boolean required) {
        String baseType = mapBaseType(field.getFieldType());

        if (field.getFieldType() == FieldDefinition.FieldType.REFERENCE) {
            baseType = "ID"; // 引用类型在输入时使用ID
        }

        if (field.getList()) {
            baseType = "[" + baseType + "!]";
        }

        if (required) {
            baseType += "!";
        }

        return baseType;
    }

    private String mapBaseType(FieldDefinition.FieldType fieldType) {
        return switch (fieldType) {
            case STRING, EMAIL, URL -> "String";
            case INTEGER -> "Int";
            case LONG -> "String"; // GraphQL没有Long类型
            case DOUBLE -> "Float";
            case BOOLEAN -> "Boolean";
            case DATE, DATETIME -> "DateTime";
            case TEXT -> "String";
            case JSON -> "JSON";
            case REFERENCE -> "String"; // 会在上层处理
            case FILE -> "String"; // 文件路径
        };
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
