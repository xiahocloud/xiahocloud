package com.xiahou.yu.paasmetacore.parser;

import com.xiahou.yu.paasmetacore.core.GlobalPropertyPool;
import com.xiahou.yu.paasmetacore.metadata.*;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

/**
 * 增强的元模型解析器
 * 根据文档设计，实现属性定义与引用分离的解析逻辑
 * 1. 首先加载全局属性库(AbstractProperties.xml)
 * 2. 解析各模型和组件的结构定义
 * 3. 处理继承关系和属性引用
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Slf4j
public class EnhancedMetaModelParser {

    private static final String METAMODEL_XML = "Metamodel.xml";
    private final GlobalPropertyPool propertyPool;

    public EnhancedMetaModelParser() {
        this.propertyPool = GlobalPropertyPool.getInstance();
    }

    /**
     * 解析完整的元模型
     * 根据架构优化，不再需要单独的全局属性库文件
     */
    public MetaModel parseMetaModel() {
        try {
            log.info("开始解析元模型...");

            // 第一步：解析主元模型配置
            MetaModel metaModel = parseMainMetaModel();

            // 第二步：解析各模型��详细定义（包含属性定义）
            parseModelDetails(metaModel);

            // 第三步：构建继承关系
            buildInheritanceRelationships(metaModel);

            // 第四步：验证属性引用
            validatePropertyReferences(metaModel);

            log.info("元模型解析完成，版本: {}", metaModel.getVersion());
            logParsingStatistics(metaModel);

            return metaModel;

        } catch (Exception e) {
            log.error("解析元模型失败", e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "解析元模型失败", e);
        }
    }

    /**
     * 第二步：解析主元模型配置
     */
    private MetaModel parseMainMetaModel() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(METAMODEL_XML);
            if (inputStream == null) {
                throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "找不到元模型配置文件: " + METAMODEL_XML);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            MetaModel metaModel = new MetaModel();
            Element root = document.getDocumentElement();

            // 解析版本号
            String version = getElementTextContent(root, "Version");
            metaModel.setVersion(version);

            // 解析模型基本信息
            Map<String, ModelDefinition> models = parseModelBasicInfo(root);
            metaModel.setModels(models);

            return metaModel;

        } catch (Exception e) {
            log.error("解析主元模型配置失败", e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "解析主元模型配置失败", e);
        }
    }

    /**
     * 解析模型基本信息（从Metamodel.xml）
     */
    private Map<String, ModelDefinition> parseModelBasicInfo(Element root) {
        Map<String, ModelDefinition> models = new HashMap<>();

        NodeList modelsNodeList = root.getElementsByTagName("Models");
        if (modelsNodeList.getLength() > 0) {
            Element modelsElement = (Element) modelsNodeList.item(0);
            NodeList modelNodeList = modelsElement.getElementsByTagName("Model");

            for (int i = 0; i < modelNodeList.getLength(); i++) {
                Element modelElement = (Element) modelNodeList.item(i);
                ModelDefinition model = parseModelMetadata(modelElement);
                models.put(model.getId(), model);
                log.debug("解析模型元数据: {} - {}", model.getId(), model.getName());
            }
        }

        return models;
    }

    /**
     * 解析单个模型的元数据
     */
    private ModelDefinition parseModelMetadata(Element modelElement) {
        ModelDefinition model = new ModelDefinition();

        // 解析属性
        String importPath = modelElement.getAttribute("import");
        String extendsModel = modelElement.getAttribute("extends");

        model.setImportPath(importPath);
        model.setExtendsModel(extendsModel.isEmpty() ? null : extendsModel);

        // 解析子元素
        model.setId(getElementTextContent(modelElement, "Id"));
        model.setName(getElementTextContent(modelElement, "Name"));
        model.setDescription(getElementTextContent(modelElement, "Desc"));

        // 判断是否为抽象模型
        model.setAbstract(model.getId().contains("Abstract"));

        return model;
    }

    /**
     * 第三步：解析各模型的详细定义
     */
    private void parseModelDetails(MetaModel metaModel) {
        for (ModelDefinition model : metaModel.getModels().values()) {
            if (model.getImportPath() != null && !model.getImportPath().isEmpty()) {
                parseModelDetailFromFile(model);
            }
        }
    }

    /**
     * 从文件解析模型详细定义
     */
    private void parseModelDetailFromFile(ModelDefinition model) {
        try {
            String filePath = model.getImportPath();
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }

            log.debug("解析模型详细定义: {} 从文件: {}", model.getId(), filePath);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                log.warn("找不到模型文件: {}", filePath);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element root = document.getDocumentElement();

            // 解析模型内部的属性声明
            List<PropertyDefinition> localProperties = parsePropertiesFromElement(root);
            Map<String, PropertyDefinition> localPropertyMap = new HashMap<>();
            for (PropertyDefinition prop : localProperties) {
                localPropertyMap.put(prop.getId(), prop);
                // 同时注册到全局属性池（可能覆盖全局定义）
                propertyPool.registerProperty(prop);
            }
            model.setDeclaredProperties(localPropertyMap);

            // 解析组件定义
            Map<String, ComponentDefinition> components = parseComponentsFromElement(root);
            model.setDeclaredComponents(components);

            // 解析属性引用列表（Refs）
            List<String> referencedProperties = parseRefsFromElement(root);
            model.setReferencedProperties(referencedProperties);

            log.debug("模型 {} 解析完成: {} 个属性声明, {} 个组件, {} 个属性引用",
                     model.getId(), localProperties.size(), components.size(), referencedProperties.size());

        } catch (Exception e) {
            log.error("解析模型文件失败: " + model.getImportPath(), e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "解析模型文件失败: " + model.getImportPath(), e);
        }
    }

    /**
     * 解析Properties元素
     */
    private List<PropertyDefinition> parsePropertiesFromElement(Element root) {
        List<PropertyDefinition> properties = new ArrayList<>();

        // 尝试两种可能的标签名：Properties 和 Propertys
        NodeList propertiesNodeList = root.getElementsByTagName("Properties");
        if (propertiesNodeList.getLength() == 0) {
            propertiesNodeList = root.getElementsByTagName("Propertys");
        }

        if (propertiesNodeList.getLength() > 0) {
            Element propertiesElement = (Element) propertiesNodeList.item(0);
            NodeList propertyNodeList = propertiesElement.getElementsByTagName("Property");

            for (int i = 0; i < propertyNodeList.getLength(); i++) {
                Element propertyElement = (Element) propertyNodeList.item(i);
                PropertyDefinition property = parsePropertyDefinition(propertyElement);
                if (property != null) {
                    properties.add(property);
                }
            }
        }

        return properties;
    }

    /**
     * 解析单个属性定义
     */
    private PropertyDefinition parsePropertyDefinition(Element propertyElement) {
        PropertyDefinition property = new PropertyDefinition();

        property.setId(getElementTextContent(propertyElement, "Id"));
        property.setName(getElementTextContent(propertyElement, "Name"));
        property.setDescription(getElementTextContent(propertyElement, "Desc"));
        property.setDataType(getElementTextContent(propertyElement, "DataType"));
        property.setDefaultValue(getElementTextContent(propertyElement, "Default"));
        property.setScope(getElementTextContent(propertyElement, "Scope"));

        // 解析数字类型的属性
        String lengthStr = getElementTextContent(propertyElement, "Length");
        if (lengthStr != null && !lengthStr.isEmpty()) {
            try {
                property.setLength(Integer.valueOf(lengthStr));
            } catch (NumberFormatException e) {
                log.warn("属性 {} 的长度值无效: {}", property.getId(), lengthStr);
            }
        }

        // 解析布尔类型的属性
        property.setNullable(parseBooleanAttribute(propertyElement, "Nullable"));
        property.setPrimaryKey(parseBooleanAttribute(propertyElement, "PrimaryKey"));
        property.setIndexed(parseBooleanAttribute(propertyElement, "Indexed"));

        return property;
    }

    /**
     * 解析组件定义
     */
    private Map<String, ComponentDefinition> parseComponentsFromElement(Element root) {
        Map<String, ComponentDefinition> components = new HashMap<>();

        NodeList componentsNodeList = root.getElementsByTagName("Components");
        if (componentsNodeList.getLength() > 0) {
            Element componentsElement = (Element) componentsNodeList.item(0);
            NodeList componentNodeList = componentsElement.getElementsByTagName("Component");

            for (int i = 0; i < componentNodeList.getLength(); i++) {
                Element componentElement = (Element) componentNodeList.item(i);
                ComponentDefinition component = parseComponentDefinition(componentElement);
                if (component != null) {
                    components.put(component.getId(), component);
                }
            }
        }

        return components;
    }

    /**
     * 解析单个组件定义
     */
    private ComponentDefinition parseComponentDefinition(Element componentElement) {
        ComponentDefinition component = new ComponentDefinition();

        // 解析基本属性
        component.setId(getElementTextContent(componentElement, "Id"));
        component.setName(getElementTextContent(componentElement, "Name"));
        component.setDescription(getElementTextContent(componentElement, "Desc"));
        component.setType(getElementTextContent(componentElement, "Type"));

        // 解析继承和导入属性
        String extendsComponent = componentElement.getAttribute("extends");
        String importPath = componentElement.getAttribute("import");
        component.setExtendsComponent(extendsComponent.isEmpty() ? null : extendsComponent);
        component.setImportPath(importPath.isEmpty() ? null : importPath);

        // 解析组件的属性引用
        List<String> referencedProperties = parseRefsFromElement(componentElement);
        component.setReferencedProperties(referencedProperties);

        return component;
    }

    /**
     * 解析Refs元素
     */
    private List<String> parseRefsFromElement(Element root) {
        List<String> refs = new ArrayList<>();

        NodeList refsNodeList = root.getElementsByTagName("Refs");
        if (refsNodeList.getLength() > 0) {
            Element refsElement = (Element) refsNodeList.item(0);
            NodeList refNodeList = refsElement.getElementsByTagName("Ref");

            for (int i = 0; i < refNodeList.getLength(); i++) {
                Element refElement = (Element) refNodeList.item(i);
                String refValue = refElement.getTextContent().trim();
                if (!refValue.isEmpty()) {
                    refs.add(refValue);
                }
            }
        }

        return refs;
    }

    /**
     * 第四步：构建继承关系
     */
    private void buildInheritanceRelationships(MetaModel metaModel) {
        log.info("构建模型继承关系...");

        for (ModelDefinition model : metaModel.getModels().values()) {
            if (model.getExtendsModel() != null) {
                ModelDefinition parentModel = metaModel.getModels().get(model.getExtendsModel());
                if (parentModel != null) {
                    // 继承父模型的属性引用
                    List<String> inheritedProperties = new ArrayList<>();
                    if (parentModel.getReferencedProperties() != null) {
                        inheritedProperties.addAll(parentModel.getReferencedProperties());
                    }
                    if (model.getReferencedProperties() != null) {
                        inheritedProperties.addAll(model.getReferencedProperties());
                    }
                    model.setReferencedProperties(inheritedProperties);

                    log.debug("模型 {} 继承自 {}，共继承 {} 个属性",
                             model.getId(), parentModel.getId(), inheritedProperties.size());
                } else {
                    log.warn("模型 {} 尝试继承不存在的父模型: {}", model.getId(), model.getExtendsModel());
                }
            }
        }
    }

    /**
     * 第五步：验证属性引用
     */
    private void validatePropertyReferences(MetaModel metaModel) {
        log.info("验证属性引用...");

        int totalReferences = 0;
        int invalidReferences = 0;

        for (ModelDefinition model : metaModel.getModels().values()) {
            if (model.getReferencedProperties() != null) {
                List<String> invalidRefs = propertyPool.validatePropertyReferences(model.getReferencedProperties());
                totalReferences += model.getReferencedProperties().size();
                invalidReferences += invalidRefs.size();

                if (!invalidRefs.isEmpty()) {
                    log.warn("模型 {} 包含无效的属性引用: {}", model.getId(), invalidRefs);
                }
            }
        }

        log.info("属性引用验证完成：总计 {} 个引用，{} 个无效", totalReferences, invalidReferences);

        if (invalidReferences > 0) {
            log.warn("发现 {} 个无效的属性引用，可能影响代码生成", invalidReferences);
        }
    }

    /**
     * 记录解析统计信息
     */
    private void logParsingStatistics(MetaModel metaModel) {
        GlobalPropertyPool.PropertyPoolStats stats = propertyPool.getStats();

        log.info("=== 元模型解析统计 ===");
        log.info("元模型版本: {}", metaModel.getVersion());
        log.info("模型总数: {}", metaModel.getModels().size());
        log.info("全局属性池: {}", stats);

        long concreteModels = metaModel.getModels().values().stream()
                .filter(model -> !model.isAbstract())
                .count();
        log.info("具体模型数量: {} (将生成数据库表)", concreteModels);
    }

    /**
     * 辅助方法：解析布尔属性
     */
    private Boolean parseBooleanAttribute(Element element, String tagName) {
        String value = getElementTextContent(element, tagName);
        if (value != null && !value.isEmpty()) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    /**
     * 辅助方法：获取元素文本内容
     */
    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }
}
