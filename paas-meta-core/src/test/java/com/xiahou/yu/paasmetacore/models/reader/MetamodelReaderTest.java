package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.MetaModel;
import com.xiahou.yu.paasmetacore.models.FieldModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 元模型读取器测试类，使用Java 21语法
 */
class MetamodelReaderTest {

    private static final Logger logger = LoggerFactory.getLogger(MetamodelReaderTest.class);

    @Test
    @DisplayName("测试加载元模型")
    void testLoadMetamodel() {
        // 获取元模型实例
        var metaModel = MetaModel.getInstance();

        assertNotNull(metaModel, "元模型实例不应为空");
        assertNotNull(metaModel.getVersion(), "版本信息不应为空");

        logger.info("元模型版本: {}", metaModel.getVersion());

        // 检查模型数量
        var models = metaModel.getAllModels();
        assertFalse(models.isEmpty(), "应该至少有一个模型");

        logger.info("加载的模型数量: {}", models.size());

        // 打印所有模型信息
        models.forEach(model -> {
            logger.info("模型: {} - {}", model.getId(), model.getDisplayInfo());

            // 打印属性信息
            var properties = model.getAllProperties();
            logger.info("  属性数量: {}", properties.size());
            properties.values().forEach(prop ->
                logger.info("    {}", prop.getDisplayInfo()));

            // 如果是字段模型，打印组件信息
            if (model instanceof FieldModel fieldModel) {
                var components = fieldModel.getComponents();
                logger.info("  组件数量: {}", components.size());
                components.forEach(comp ->
                    logger.info("    组件: {} - {}", comp.id(), comp.name()));
            }
        });
    }

    @Test
    @DisplayName("测试模型继承关系")
    void testModelInheritance() {
        var metaModel = MetaModel.getInstance();

        // 检查继承关系
        var pageModel = metaModel.getModel("PageModel");
        var dataModel = metaModel.getModel("DataModel");
        var fieldModel = metaModel.getModel("FieldModel");
        var abstractModel = metaModel.getModel("AbstractModel");

        if (pageModel != null && abstractModel != null) {
            assertEquals("AbstractModel", pageModel.getExtendsModel(),
                "PageModel应该继承自AbstractModel");
        }

        if (dataModel != null && abstractModel != null) {
            assertEquals("AbstractModel", dataModel.getExtendsModel(),
                "DataModel应该继承自AbstractModel");
        }

        if (fieldModel != null && abstractModel != null) {
            assertEquals("AbstractModel", fieldModel.getExtendsModel(),
                "FieldModel应该继承自AbstractModel");
        }
    }

    @Test
    @DisplayName("测试属性访问")
    void testPropertyAccess() {
        var metaModel = MetaModel.getInstance();
        var abstractModel = metaModel.getModel("AbstractModel");

        if (abstractModel != null) {
            // 检查基础属性
            assertTrue(abstractModel.hasProperty("id"), "AbstractModel应该有id属性");
            assertTrue(abstractModel.hasProperty("name"), "AbstractModel应该有name属性");

            var idProperty = abstractModel.getProperty("id");
            assertTrue(idProperty.isPresent(), "id属性应该存在");

            logger.info("id属性信息: {}", idProperty.get().getDisplayInfo());
        }
    }
}