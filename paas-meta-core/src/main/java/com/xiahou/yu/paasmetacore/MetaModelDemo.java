package com.xiahou.yu.paasmetacore;

import com.xiahou.yu.paasmetacore.core.GlobalPropertyPool;
import com.xiahou.yu.paasmetacore.manager.MetaModelManager;
import com.xiahou.yu.paasmetacore.metadata.ModelDefinition;
import com.xiahou.yu.paasmetacore.metadata.PropertyDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 元模型核心系统演示类
 * 专注于展示元模型解析、管理和查询功能
 * 不包含代码生成相关功能
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Slf4j
public class MetaModelDemo {

    public static void main(String[] args) {
        try {
            log.info("=== PaaS Meta Core 元模型核心系统演示 ===");

            // 1. 获取元模型管理器
            MetaModelManager manager = MetaModelManager.getInstance();
            log.info("元模型版本: {}", manager.getVersion());

            // 2. 展示全局属性池信息
            demonstrateGlobalPropertyPool(manager);

            // 3. 展示模型信息和属性引用分离
            demonstrateModelInformation(manager);

            // 4. 展示作用域功能
            demonstrateScopeFeature(manager);

            // 5. 验证属性引用
            validatePropertyReferences(manager);

            // 6. 展示元模型查询功能
            demonstrateModelQuery(manager);

            log.info("=== 元模型核心系统演示完成 ===");

        } catch (Exception e) {
            log.error("演示过程中发生错误", e);
        }
    }

    /**
     * 展示全局属性池功能
     */
    private static void demonstrateGlobalPropertyPool(MetaModelManager manager) {
        log.info("\n=== 全局属性池信息 ===");

        GlobalPropertyPool.PropertyPoolStats stats = manager.getPropertyPoolStats();
        log.info("全局属性池统计: {}", stats);

        // 展示一些典型属性
        log.info("\n典型属性示例:");
        PropertyDefinition idProperty = manager.getProperty("id");
        if (idProperty != null) {
            log.info("- {} ({}): {} [作用域: {}]",
                    idProperty.getId(), idProperty.getName(),
                    idProperty.getDataType(), idProperty.getScope());
        }

        PropertyDefinition displayProperty = manager.getProperty("display");
        if (displayProperty != null) {
            log.info("- {} ({}): {} [作用域: {}]",
                    displayProperty.getId(), displayProperty.getName(),
                    displayProperty.getDataType(), displayProperty.getScope());
        }
    }

    /**
     * 展示模型信息和属性引用分离的核心概念
     */
    private static void demonstrateModelInformation(MetaModelManager manager) {
        log.info("\n=== 模型信息与属性引用���离演示 ===");

        // 显示所有模型
        log.info("所有模型:");
        for (ModelDefinition model : manager.getAllModels()) {
            log.info("- {} ({}): {}", model.getId(), model.getName(),
                    model.isAbstract() ? "抽象模型" : "实体模型");
            if (model.getExtendsModel() != null) {
                log.info("  继承自: {}", model.getExtendsModel());
            }
            if (model.getTableName() != null) {
                log.info("  数据库表: {}", model.getTableName());
            }
        }

        // 展示属性引用分离的核心逻辑
        log.info("\n属性引用分离演示 - 以AbstractModel为例:");
        ModelDefinition abstractModel = manager.getModel("AbstractModel");
        if (abstractModel != null) {
            log.info("模型声明的属性引用: {}", abstractModel.getReferencedProperties());

            List<PropertyDefinition> actualProperties = manager.getModelProperties("AbstractModel");
            log.info("从全局属性池解析得到的实际属性 ({} 个):", actualProperties.size());
            for (PropertyDefinition prop : actualProperties) {
                log.info("  - {} ({}): {} -> {} [作用域: {}]",
                        prop.getId(), prop.getName(), prop.getDataType(),
                        prop.getColumnName(), prop.getScope());
            }
        }
    }

    /**
     * 展示作用域功能
     */
    private static void demonstrateScopeFeature(MetaModelManager manager) {
        log.info("\n=== 作用域功能演示 ===");

        // 展示PageModel作用域的属性
        List<PropertyDefinition> pageProperties = manager.getPropertiesByScope("PageModel");
        log.info("PageModel作用域属性 ({} 个):", pageProperties.size());
        for (PropertyDefinition prop : pageProperties) {
            log.info("  - {} ({}): {}", prop.getId(), prop.getName(), prop.getDescription());
        }

        // 展示EntityModel作用域的属性
        List<PropertyDefinition> dataProperties = manager.getPropertiesByScope("EntityModel");
        log.info("\nEntityModel作用域属性 ({} 个):", dataProperties.size());
        for (PropertyDefinition prop : dataProperties) {
            log.info("  - {} ({}): {}", prop.getId(), prop.getName(), prop.getDescription());
        }

        // 展示适用于特定模型的属性
        List<PropertyDefinition> applicableToPage = manager.getApplicableProperties("PageModel");
        log.info("\n适用于PageModel的所有属性 ({} 个，包含通用属性):", applicableToPage.size());
    }

    /**
     * 验证属性引用
     */
    private static void validatePropertyReferences(MetaModelManager manager) {
        log.info("\n=== 属性引用验证 ===");

        for (ModelDefinition model : manager.getAllModels()) {
            MetaModelManager.ValidationResult result = manager.validateModel(model.getId());
            if (!result.isValid()) {
                log.warn("模型 {} 验证失败: {}", model.getId(), result.getMessage());
            } else {
                log.debug("模型 {} 验证通过", model.getId());
            }
        }

        log.info("属性引用验证完成");
    }

    /**
     * 展示元模型查询功能
     */
    private static void demonstrateModelQuery(MetaModelManager manager) {
        log.info("\n=== 元模型查询功能演示 ===");

        // 查询��体模型
        List<ModelDefinition> concreteModels = manager.getConcreteModels().stream().toList();
        log.info("具体模型数量: {}", concreteModels.size());
        for (ModelDefinition model : concreteModels) {
            log.info("- {}: {} (表名: {})", model.getId(), model.getName(), model.getTableName());
        }

        // 查询抽象模型
        List<ModelDefinition> abstractModels = manager.getAllModels().stream()
                .filter(ModelDefinition::isAbstract)
                .toList();
        log.info("\n抽象模型数量: {}", abstractModels.size());
        for (ModelDefinition model : abstractModels) {
            log.info("- {}: {}", model.getId(), model.getName());
        }

        // 查询继承关系
        log.info("\n继承关系:");
        for (ModelDefinition model : manager.getAllModels()) {
            if (model.getExtendsModel() != null) {
                log.info("- {} 继承自 {}", model.getId(), model.getExtendsModel());
            }
        }

        // 展示属性统计
        GlobalPropertyPool.PropertyPoolStats stats = manager.getPropertyPoolStats();
        log.info("\n属性统计: {}", stats);
    }
}
