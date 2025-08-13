package com.xiahou.yu.paasmetacore.parser;

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
 * 元模型XML解析器
 * 负责解析Metamodel.xml和具体的模型XML文件
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Slf4j
public class MetaModelParser {

    private static final String METAMODEL_XML = "Metamodel.xml";

    /**
     * 解析元模型
     */
    public MetaModel parseMetaModel() {
        try {
            log.info("开始解析元模型配置文件: {}", METAMODEL_XML);

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
            log.info("元模型版本: {}", version);

            // 解析模型定义
            Map<String, ModelDefinition> models = parseModels(root);
            metaModel.setModels(models);

            // 解析每个模型的详细定义
            parseModelDetails(models);

            log.info("元模型解析完成，共解析 {} 个模型", models.size());
            return metaModel;

        } catch (Exception e) {
            log.error("解析元模型失败", e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "解析元模型失败", e);
        }
    }

    /**
     * 解析模型基本信息
     */
    private Map<String, ModelDefinition> parseModels(Element root) {
        Map<String, ModelDefinition> models = new HashMap<>();

        NodeList modelsNodeList = root.getElementsByTagName("Models");
        if (modelsNodeList.getLength() > 0) {
            Element modelsElement = (Element) modelsNodeList.item(0);
            NodeList modelNodeList = modelsElement.getElementsByTagName("Model");

            for (int i = 0; i < modelNodeList.getLength(); i++) {
                Element modelElement = (Element) modelNodeList.item(i);
                ModelDefinition model = parseModelBasicInfo(modelElement);
                models.put(model.getId(), model);
                log.debug("解析模型: {} - {}", model.getId(), model.getName());
            }
        }

        return models;
    }

    /**
     * 解析模型基本信息
     */
    private ModelDefinition parseModelBasicInfo(Element modelElement) {
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

        // 判断是否为抽象模型（如果没有继承关系且名称包含Abstract，则认为是抽象模型）
        model.setAbstract(model.getId().contains("Abstract"));

        return model;
    }

    /**
     * 解析模型详细定义
     */
    private void parseModelDetails(Map<String, ModelDefinition> models) {
        for (ModelDefinition model : models.values()) {
            if (model.getImportPath() != null && !model.getImportPath().isEmpty()) {
                parseModelDetailFromFile(model);
            }
        }
    }

    /**
     * 从指定文件解析模型详细定义
     */
    private void parseModelDetailFromFile(ModelDefinition model) {
        try {
            String filePath = model.getImportPath();
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1); // 移除开头的斔杠
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

            // 解析属性定义
            Map<String, PropertyDefinition> properties = parseProperties(root);
            model.setDeclaredProperties(properties);

            // 解析组件定义
            Map<String, ComponentDefinition> components = parseComponents(root);
            model.setDeclaredComponents(components);

            // 解析引用的属性
            List<String> referencedProperties = parseRefs(root);
            model.setReferencedProperties(referencedProperties);

            log.debug("模型 {} 解析完成: {} 个属性, {} 个组件, {} 个引用属性",
                     model.getId(), properties.size(), components.size(), referencedProperties.size());

        } catch (Exception e) {
            log.error("解析模型文件失败: " + model.getImportPath(), e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "解析模型文件失败: " + model.getImportPath(), e);
        }
    }

    /**
     * 解析属性定义
     */
    private Map<String, PropertyDefinition> parseProperties(Element root) {
        Map<String, PropertyDefinition> properties = new HashMap<>();

        NodeList propertiesNodeList = root.getElementsByTagName("Properties");
        if (propertiesNodeList.getLength() > 0) {
            Element propertiesElement = (Element) propertiesNodeList.item(0);
            NodeList propertyNodeList = propertiesElement.getElementsByTagName("Property");

            for (int i = 0; i < propertyNodeList.getLength(); i++) {
                Element propertyElement = (Element) propertyNodeList.item(i);
                PropertyDefinition property = parseProperty(propertyElement);
                properties.put(property.getId(), property);
            }
        }

        return properties;
    }

    /**
     * 解析单个属性定义
     */
    private PropertyDefinition parseProperty(Element propertyElement) {
        PropertyDefinition property = new PropertyDefinition();

        property.setId(getElementTextContent(propertyElement, "Id"));
        property.setName(getElementTextContent(propertyElement, "Name"));
        property.setDescription(getElementTextContent(propertyElement, "Desc"));
        property.setDataType(getElementTextContent(propertyElement, "DataType"));
        property.setDefaultValue(getElementTextContent(propertyElement, "Default"));

        // 新增：解析作用域属性
        property.setScope(getElementTextContent(propertyElement, "Scope"));

        // 解析数字类型的属性
        String lengthStr = getElementTextContent(propertyElement, "Length");
        if (lengthStr != null && !lengthStr.isEmpty()) {
            property.setLength(Integer.valueOf(lengthStr));
        }

        String nullableStr = getElementTextContent(propertyElement, "Nullable");
        if (nullableStr != null && !nullableStr.isEmpty()) {
            property.setNullable(Boolean.valueOf(nullableStr));
        }

        String primaryKeyStr = getElementTextContent(propertyElement, "PrimaryKey");
        if (primaryKeyStr != null && !primaryKeyStr.isEmpty()) {
            property.setPrimaryKey(Boolean.valueOf(primaryKeyStr));
        }

        String indexedStr = getElementTextContent(propertyElement, "Indexed");
        if (indexedStr != null && !indexedStr.isEmpty()) {
            property.setIndexed(Boolean.valueOf(indexedStr));
        }

        return property;
    }

    /**
     * 解析组件定义
     */
    private Map<String, ComponentDefinition> parseComponents(Element root) {
        Map<String, ComponentDefinition> components = new HashMap<>();

        NodeList componentsNodeList = root.getElementsByTagName("Components");
        if (componentsNodeList.getLength() > 0) {
            Element componentsElement = (Element) componentsNodeList.item(0);
            NodeList componentNodeList = componentsElement.getElementsByTagName("Component");

            for (int i = 0; i < componentNodeList.getLength(); i++) {
                Element componentElement = (Element) componentNodeList.item(i);
                ComponentDefinition component = parseComponent(componentElement);
                components.put(component.getId(), component);
            }
        }

        return components;
    }

    /**
     * 解析单个组件定义
     */
    private ComponentDefinition parseComponent(Element componentElement) {
        ComponentDefinition component = new ComponentDefinition();

        component.setId(getElementTextContent(componentElement, "Id"));
        component.setName(getElementTextContent(componentElement, "Name"));
        component.setDescription(getElementTextContent(componentElement, "Desc"));
        component.setType(getElementTextContent(componentElement, "Type"));

        return component;
    }

    /**
     * 解析引用的属性列表
     */
    private List<String> parseRefs(Element root) {
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
     * 获取元素的文本内容
     */
    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }
}
