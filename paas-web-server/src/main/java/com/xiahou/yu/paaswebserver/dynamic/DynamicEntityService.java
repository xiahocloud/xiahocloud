package com.xiahou.yu.paaswebserver.dynamic;

import com.xiahou.yu.paaswebserver.repository.DynamicEntityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态实体服务
 * @author system
 */
@Service
@Transactional
public class DynamicEntityService {

    @Autowired
    private DynamicEntityRepository dynamicEntityRepository;

    @Autowired
    private EntityDefinitionService entityDefinitionService;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> findById(String entityType, String id) {
        Optional<DynamicEntity> entity = dynamicEntityRepository.findById(Long.parseLong(id));

        if (entity.isPresent() && entity.get().getEntityType().equals(entityType)) {
            return convertToMap(entity.get());
        }

        return null;
    }

    public Map<String, Object> findAllWithPagination(String entityType,
                                                     Map<String, Object> paginationInput,
                                                     List<Map<String, Object>> filters,
                                                     List<Map<String, Object>> sort) {

        // 获取实体定义
        EntityDefinition entityDef = entityDefinitionService.getEntityDefinition(entityType)
            .orElseThrow(() -> new RuntimeException("Entity type not found: " + entityType));

        // 处理分页参数
        int page = 0;
        int size = 20;
        if (paginationInput != null) {
            if (paginationInput.containsKey("first")) {
                size = (Integer) paginationInput.get("first");
            }
        }

        // 处理排序
        Sort sortSpec = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (Map<String, Object> sortItem : sort) {
                String field = (String) sortItem.get("field");
                String direction = (String) sortItem.get("direction");
                orders.add(new Sort.Order(
                    "DESC".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "id".equals(field) ? "id" : "attributes." + field
                ));
            }
            sortSpec = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page, size, sortSpec);

        // 获取数据
        List<DynamicEntity> entities = dynamicEntityRepository.findByEntityType(entityType);

        // 应用过滤器
        if (filters != null && !filters.isEmpty()) {
            entities = applyFilters(entities, filters);
        }

        // 转换为连接格式
        List<Map<String, Object>> nodes = entities.stream()
            .map(this::convertToMap)
            .collect(Collectors.toList());

        // 构建分页结果
        Map<String, Object> connection = new HashMap<>();

        List<Map<String, Object>> edges = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            Map<String, Object> edge = new HashMap<>();
            edge.put("node", nodes.get(i));
            edge.put("cursor", Base64.getEncoder().encodeToString(String.valueOf(i).getBytes()));
            edges.add(edge);
        }

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("hasNextPage", entities.size() >= size);
        pageInfo.put("hasPreviousPage", false);
        pageInfo.put("startCursor", edges.isEmpty() ? null : edges.get(0).get("cursor"));
        pageInfo.put("endCursor", edges.isEmpty() ? null : edges.get(edges.size() - 1).get("cursor"));

        connection.put("edges", edges);
        connection.put("pageInfo", pageInfo);
        connection.put("totalCount", entities.size());

        return connection;
    }

    public Map<String, Object> create(String entityType, Map<String, Object> input) {
        // 验证实体类型存在
        EntityDefinition entityDef = entityDefinitionService.getEntityDefinition(entityType)
            .orElseThrow(() -> new RuntimeException("Entity type not found: " + entityType));

        DynamicEntity entity = new DynamicEntity(entityType);

        // 验证和设置属性
        for (FieldDefinition field : entityDef.getFields()) {
            String fieldName = field.getFieldName();
            if ("id".equals(fieldName)) {
                continue;
            }

            Object value = input.get(fieldName);

            // 验证必填字段
            if (field.getRequired() && (value == null || value.toString().trim().isEmpty())) {
                throw new RuntimeException("Required field '" + fieldName + "' is missing");
            }

            // 设置默认值
            if (value == null && field.getDefaultValue() != null) {
                value = field.getDefaultValue();
            }

            if (value != null) {
                entity.setAttribute(fieldName, validateAndConvertValue(value, field));
            }
        }

        DynamicEntity savedEntity = dynamicEntityRepository.save(entity);
        return convertToMap(savedEntity);
    }

    public Map<String, Object> update(String entityType, String id, Map<String, Object> input) {
        DynamicEntity entity = dynamicEntityRepository.findById(Long.parseLong(id))
            .orElseThrow(() -> new RuntimeException("Entity not found with id: " + id));

        if (!entity.getEntityType().equals(entityType)) {
            throw new RuntimeException("Entity type mismatch");
        }

        EntityDefinition entityDef = entityDefinitionService.getEntityDefinition(entityType)
            .orElseThrow(() -> new RuntimeException("Entity type not found: " + entityType));

        // 更新属性
        for (String fieldName : input.keySet()) {
            if ("id".equals(fieldName)) {
                continue;
            }

            FieldDefinition field = entityDef.getFields().stream()
                .filter(f -> f.getFieldName().equals(fieldName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Field '" + fieldName + "' not found in entity definition"));

            Object value = input.get(fieldName);
            if (value != null) {
                entity.setAttribute(fieldName, validateAndConvertValue(value, field));
            }
        }

        DynamicEntity savedEntity = dynamicEntityRepository.save(entity);
        return convertToMap(savedEntity);
    }

    public boolean delete(String entityType, String id) {
        Optional<DynamicEntity> entity = dynamicEntityRepository.findById(Long.parseLong(id));

        if (entity.isPresent() && entity.get().getEntityType().equals(entityType)) {
            dynamicEntityRepository.deleteById(Long.parseLong(id));
            return true;
        }

        return false;
    }

    private Map<String, Object> convertToMap(DynamicEntity entity) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId().toString());
        result.put("entityType", entity.getEntityType());
        result.putAll(entity.getAttributes());
        result.put("createdAt", entity.getCreatedAt());
        result.put("updatedAt", entity.getUpdatedAt());
        return result;
    }

    private List<DynamicEntity> applyFilters(List<DynamicEntity> entities, List<Map<String, Object>> filters) {
        return entities.stream()
            .filter(entity -> {
                for (Map<String, Object> filter : filters) {
                    String field = (String) filter.get("field");
                    String operator = (String) filter.get("operator");
                    String value = (String) filter.get("value");

                    String entityValue = "id".equals(field) ? entity.getId().toString() : entity.getAttribute(field);

                    if (!matchesFilter(entityValue, operator, value)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    private boolean matchesFilter(String entityValue, String operator, String filterValue) {
        if (entityValue == null) {
            return "IS_NULL".equals(operator);
        }

        return switch (operator) {
            case "EQUALS" -> entityValue.equals(filterValue);
            case "NOT_EQUALS" -> !entityValue.equals(filterValue);
            case "CONTAINS" -> entityValue.contains(filterValue);
            case "STARTS_WITH" -> entityValue.startsWith(filterValue);
            case "ENDS_WITH" -> entityValue.endsWith(filterValue);
            case "IS_NOT_NULL" -> true;
            case "IS_NULL" -> false;
            default -> true;
        };
    }

    private Object validateAndConvertValue(Object value, FieldDefinition field) {
        // 根据字段类型验证和转换值
        return switch (field.getFieldType()) {
            case INTEGER -> {
                if (value instanceof Number) {
                    yield ((Number) value).intValue();
                }
                yield Integer.parseInt(value.toString());
            }
            case LONG -> {
                if (value instanceof Number) {
                    yield ((Number) value).longValue();
                }
                yield Long.parseLong(value.toString());
            }
            case DOUBLE -> {
                if (value instanceof Number) {
                    yield ((Number) value).doubleValue();
                }
                yield Double.parseDouble(value.toString());
            }
            case BOOLEAN -> {
                if (value instanceof Boolean) {
                    yield value;
                }
                yield Boolean.parseBoolean(value.toString());
            }
            default -> value.toString();
        };
    }
}
