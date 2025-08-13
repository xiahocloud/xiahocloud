package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dynamic.DynamicEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/entities")
public class DynamicEntityController {

    @Autowired
    private DynamicEntityService dynamicEntityService;

    @GetMapping("/{entityType}/{id}")
    public ResponseEntity<Map<String, Object>> getEntity(
            @PathVariable String entityType,
            @PathVariable String id) {
        Map<String, Object> entity = dynamicEntityService.findById(entityType, id);
        if (entity != null) {
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{entityType}")
    public ResponseEntity<Map<String, Object>> getEntities(
            @PathVariable String entityType,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        // 构建分页参数
        Map<String, Object> pagination = null;
        if (size != null) {
            pagination = Map.of("first", size);
        }

        Map<String, Object> result = dynamicEntityService.findAllWithPagination(
            entityType, pagination, null, null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{entityType}")
    public ResponseEntity<Map<String, Object>> createEntity(
            @PathVariable String entityType,
            @RequestBody Map<String, Object> data) {
        try {
            Map<String, Object> entity = dynamicEntityService.create(entityType, data);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{entityType}/{id}")
    public ResponseEntity<Map<String, Object>> updateEntity(
            @PathVariable String entityType,
            @PathVariable String id,
            @RequestBody Map<String, Object> data) {
        try {
            Map<String, Object> entity = dynamicEntityService.update(entityType, id, data);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{entityType}/{id}")
    public ResponseEntity<Map<String, Object>> deleteEntity(
            @PathVariable String entityType,
            @PathVariable String id) {
        boolean deleted = dynamicEntityService.delete(entityType, id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.notFound().build();
    }
}
