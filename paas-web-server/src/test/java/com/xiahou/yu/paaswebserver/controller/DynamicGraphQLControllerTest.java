package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.service.DynamicSchemaService;
import com.xiahou.yu.paaswebserver.entity.DynamicSchemaMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureGraphQlTester
public class DynamicGraphQLControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private DynamicSchemaService schemaService;

    @BeforeEach
    void setUp() {
        // 注册测试Schema
        DynamicSchemaMetadata metadata = new DynamicSchemaMetadata();
        metadata.setSystem("xia");
        metadata.setModule("hr");
        metadata.setContext("api");
        metadata.setApp("invoice");
        metadata.setAggr("employee");
        metadata.setEntity("user");

        Map<String, String> fieldTypes = new HashMap<>();
        fieldTypes.put("id", "ID");
        fieldTypes.put("name", "String");
        fieldTypes.put("email", "String");
        fieldTypes.put("age", "Int");
        metadata.setFieldTypes(fieldTypes);

        schemaService.registerSchema(metadata);
    }

    @Test
    void testDynamicQuery() {
        String query = """
            query($input: DynamicQueryInput!) {
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
            """;

        Map<String, Object> input = Map.of(
            "entityName", "user",
            "fields", List.of("id", "name", "email"),
            "page", 1,
            "size", 10
        );

        graphQlTester.document(query)
            .variable("input", input)
            .execute()
            .path("dynamicQuery.total")
            .entity(Long.class)
            .isEqualTo(2L)
            .path("dynamicQuery.schema")
            .entity(String.class)
            .isEqualTo("xia.hr.api.invoice.employee.user");
    }

    @Test
    void testDynamicCommand() {
        String mutation = """
            mutation($input: DynamicCommandInput!) {
                dynamicCommand(input: $input) {
                    success
                    message
                    operationType
                    affectedRows
                    data
                }
            }
            """;

        Map<String, Object> input = Map.of(
            "system", "xia",
            "module", "hr",
            "entity", "user",
            "operation", "CREATE",
            "data", Map.of(
                "name", "测试用户",
                "email", "test@example.com",
                "age", 25
            )
        );

        graphQlTester.document(mutation)
            .variable("input", input)
            .execute()
            .path("dynamicCommand.success")
            .entity(Boolean.class)
            .isEqualTo(true)
            .path("dynamicCommand.operationType")
            .entity(String.class)
            .isEqualTo("CREATE");
    }
}
