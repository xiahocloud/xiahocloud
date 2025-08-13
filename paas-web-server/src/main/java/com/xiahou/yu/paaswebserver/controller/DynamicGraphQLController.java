package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dynamic.DynamicGraphQLSchemaBuilder;
import com.xiahou.yu.paaswebserver.dynamic.EntityDefinition;
import com.xiahou.yu.paaswebserver.dynamic.EntityDefinitionService;
import com.xiahou.yu.paaswebserver.dynamic.FieldDefinition;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class DynamicGraphQLController {

    @Autowired
    private EntityDefinitionService entityDefinitionService;

    @Autowired
    private DynamicGraphQLSchemaBuilder schemaBuilder;

    @GetMapping("/entities")
    public ResponseEntity<List<EntityDefinition>> getAllEntityDefinitions() {
        List<EntityDefinition> entities = entityDefinitionService.getAllEntityDefinitions();
        return ResponseEntity.ok(entities);
    }

    @PostMapping("/entities")
    public ResponseEntity<EntityDefinition> createEntityDefinition(@RequestBody Map<String, Object> request) {
        String entityName = (String) request.get("entityName");
        String displayName = (String) request.get("displayName");
        String description = (String) request.get("description");

        EntityDefinition entity = entityDefinitionService.createEntityDefinition(entityName, displayName, description);
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/entities/{entityId}/fields")
    public ResponseEntity<FieldDefinition> addFieldToEntity(
            @PathVariable Long entityId,
            @RequestBody Map<String, Object> request) {

        String fieldName = (String) request.get("fieldName");
        String fieldTypeStr = (String) request.get("fieldType");
        Boolean required = (Boolean) request.get("required");
        String defaultValue = (String) request.get("defaultValue");
        String displayName = (String) request.get("displayName");
        String description = (String) request.get("description");

        FieldDefinition.FieldType fieldType = FieldDefinition.FieldType.valueOf(fieldTypeStr.toUpperCase());

        FieldDefinition field = entityDefinitionService.addFieldToEntity(
            entityId, fieldName, fieldType, required, defaultValue, displayName, description);

        return ResponseEntity.ok(field);
    }

    @GetMapping("/schema")
    public ResponseEntity<String> getCurrentSchema() {
        try {
            GraphQLSchema schema = schemaBuilder.buildSchema();
            return ResponseEntity.ok("Schema generated successfully. Total types: " + schema.getAllTypesAsList().size());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating schema: " + e.getMessage());
        }
    }

    @PostMapping("/schema/refresh")
    public ResponseEntity<String> refreshSchema() {
        try {
            // 重新构建schema
            GraphQLSchema schema = schemaBuilder.buildSchema();
            return ResponseEntity.ok("Schema refreshed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error refreshing schema: " + e.getMessage());
        }
    }
}
