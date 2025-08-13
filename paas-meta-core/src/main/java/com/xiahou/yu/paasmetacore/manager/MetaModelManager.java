package com.xiahou.yu.paasmetacore.manager;

import com.xiahou.yu.paasmetacore.core.GlobalPropertyPool;
import com.xiahou.yu.paasmetacore.metadata.*;
import com.xiahou.yu.paasmetacore.parser.EnhancedMetaModelParser;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重构后的元模型管理器
 * 根据文档设计，集成全局属性池和增强解析器
 * 提供统一的元模型查询和管理服务
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Slf4j
public class MetaModelManager {

    private static volatile MetaModelManager instance;
    private final Map<String, ModelDefinition> allModels = new ConcurrentHashMap<>();
    private final Map<String, ComponentDefinition> allComponents = new ConcurrentHashMap<>();
    private final GlobalPropertyPool propertyPool;
    private MetaModel metaModel;

    private MetaModelManager() {
        this.propertyPool = GlobalPropertyPool.getInstance();
        initialize();
    }

    public static MetaModelManager getInstance() {
        if (instance == null) {
            synchronized (MetaModelManager.class) {
                if (instance == null) {
                    instance = new MetaModelManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化元模型管理器
     * 使用增强的解析器，按照文档要求的顺序加载
     */
    private void initialize() {
        try {
            log.info("初始化元模型管理器...");

            // 使用增强的解析器
            EnhancedMetaModelParser parser = new EnhancedMetaModelParser();
            metaModel = parser.parseMetaModel();

            // 缓存所有模型和组件
            cacheAllDefinitions();

            log.info("元模型管理器初始化完成，版本: {}", metaModel.getVersion());

        } catch (Exception e) {
            log.error("初始化元模型管理器失败", e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "初始化元模型管理器失败", e);
        }
    }

    /**
     * 缓存所有定义到内存中
     */
    private void cacheAllDefinitions() {
        for (ModelDefinition model : metaModel.getModels().values()) {
            allModels.put(model.getId(), model);

            // 缓存模型中的组件
            if (model.getDeclaredComponents() != null) {
                allComponents.putAll(model.getDeclaredComponents());
            }
        }

        log.info("缓存完成: {} 个模型, {} 个组件",
                 allModels.size(), allComponents.size());
    }

    /**
     * 获取模型定义
     */
    public ModelDefinition getModel(String modelId) {
        return allModels.get(modelId);
    }

    /**
     * 获取属性定义（从全局属性池）
     */
    public PropertyDefinition getProperty(String propertyId) {
        return propertyPool.getProperty(propertyId);
    }

    /**
     * 获取组件定义
     */
    public ComponentDefinition getComponent(String componentId) {
        return allComponents.get(componentId);
    }

    /**
     * 获取所有模型
     */
    public Collection<ModelDefinition> getAllModels() {
        return Collections.unmodifiableCollection(allModels.values());
    }

    /**
     * 获取所有非抽象模型（需要生成数据表的模型）
     */
    public Collection<ModelDefinition> getConcreteModels() {
        return allModels.values().stream()
                .filter(model -> !model.isAbstract())
                .toList();
    }

    /**
     * 获取模型的所有有效属性（根据Refs引用，从全局属性池解析）
     * 这是文档中强调的核心逻辑：属性定义与引用分离
     */
    public List<PropertyDefinition> getModelProperties(String modelId) {
        ModelDefinition model = getModel(modelId);
        if (model == null || model.getReferencedProperties() == null) {
            return Collections.emptyList();
        }

        return propertyPool.getProperties(model.getReferencedProperties());
    }

    /**
     * 获取指定作用域的属性（根据文档的Scope概念）
     */
    public List<PropertyDefinition> getPropertiesByScope(String scope) {
        return propertyPool.getPropertiesByScope(scope);
    }

    /**
     * 获取适用于指定模型的属性（���据模型类型过滤作用域）
     */
    public List<PropertyDefinition> getApplicableProperties(String modelId) {
        ModelDefinition model = getModel(modelId);
        if (model == null) {
            return Collections.emptyList();
        }

        // 获取模型类型对应的作用域属性
        String modelType = extractModelType(model.getId());
        List<PropertyDefinition> scopedProperties = propertyPool.getPropertiesByScope(modelType);

        // 合并通用属性（无作用域限制的属性）
        List<PropertyDefinition> universalProperties = propertyPool.getAllProperties().stream()
                .filter(prop -> prop.getScope() == null)
                .toList();

        List<PropertyDefinition> result = new ArrayList<>();
        result.addAll(scopedProperties);
        result.addAll(universalProperties);

        return result;
    }

    /**
     * 从��型ID提取模型类型（用于作用域匹配）
     */
    private String extractModelType(String modelId) {
        if (modelId.endsWith("Model")) {
            return modelId;
        }
        return modelId + "Model";
    }

    /**
     * 获取模型的数据库表结构信息
     */
    public TableStructure getTableStructure(String modelId) {
        ModelDefinition model = getModel(modelId);
        if (model == null || model.isAbstract()) {
            return null;
        }

        TableStructure table = new TableStructure();
        table.setTableName(model.getTableName());
        table.setModelId(modelId);
        table.setModelName(model.getName());
        table.setDescription(model.getDescription());

        List<PropertyDefinition> properties = getModelProperties(modelId);
        table.setColumns(properties);

        return table;
    }

    /**
     * 验证模型的属性引用是否有效
     */
    public ValidationResult validateModel(String modelId) {
        ModelDefinition model = getModel(modelId);
        if (model == null) {
            return new ValidationResult(false, "模型不存在: " + modelId);
        }

        List<String> errors = new ArrayList<>();

        // 验证属性引用
        if (model.getReferencedProperties() != null) {
            List<String> invalidRefs = propertyPool.validatePropertyReferences(model.getReferencedProperties());
            if (!invalidRefs.isEmpty()) {
                errors.add("无效的属性引用: " + invalidRefs);
            }
        }

        // 验证继承关系
        if (model.getExtendsModel() != null && !allModels.containsKey(model.getExtendsModel())) {
            errors.add("继承的父模型不存在: " + model.getExtendsModel());
        }

        return new ValidationResult(errors.isEmpty(), String.join("; ", errors));
    }

    /**
     * 获取元模型版本
     */
    public String getVersion() {
        return metaModel != null ? metaModel.getVersion() : "unknown";
    }

    /**
     * 获取全局属性池统计信息
     */
    public GlobalPropertyPool.PropertyPoolStats getPropertyPoolStats() {
        return propertyPool.getStats();
    }

    /**
     * 数据库表结构信息
     */
    public static class TableStructure {
        private String tableName;
        private String modelId;
        private String modelName;
        private String description;
        private List<PropertyDefinition> columns;

        // Getters and Setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }

        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<PropertyDefinition> getColumns() { return columns; }
        public void setColumns(List<PropertyDefinition> columns) { this.columns = columns; }
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
