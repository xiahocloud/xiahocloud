package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.entity.DynamicSchemaMetadata;
import com.xiahou.yu.paaswebserver.service.DynamicSchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Schema管理REST接口
 */
@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
@Slf4j
public class SchemaManagementController {

    private final DynamicSchemaService schemaService;

    /**
     * 注册新的Schema
     */
    @PostMapping
    public ResponseEntity<String> registerSchema(@RequestBody DynamicSchemaMetadata metadata) {
        try {
            schemaService.registerSchema(metadata);
            return ResponseEntity.ok("Schema registered successfully");
        } catch (Exception e) {
            log.error("Error registering schema", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 获取指定Schema
     */
    @GetMapping
    public ResponseEntity<DynamicSchemaMetadata> getSchema(
            @RequestParam String system,
            @RequestParam String module,
            @RequestParam(required = false) String context,
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String aggr,
            @RequestParam String entity) {

        try {
            DynamicSchemaMetadata metadata = schemaService.getSchema(
                system, module, context, app, aggr, entity);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            log.error("Error getting schema", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取所有Schema
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, DynamicSchemaMetadata>> getAllSchemas() {
        try {
            Map<String, DynamicSchemaMetadata> schemas = schemaService.getAllSchemas();
            return ResponseEntity.ok(schemas);
        } catch (Exception e) {
            log.error("Error getting all schemas", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
