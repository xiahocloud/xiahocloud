package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.models.AbstractModel;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.models.*;
import com.xiahou.yu.paasmetacore.models.props.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 元模型XML解析器，使用Java 21语法
 */
public class MetamodelReader {

    private static final Logger logger = LoggerFactory.getLogger(MetamodelReader.class);

    /**
     * 元模型数据记录
     */
    public record MetamodelData(String version, List<AbstractModel> models) {}

    /**
     * 加载元模型
     */
    public MetamodelData loadMetamodel() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("Metamodel.xml")) {

            if (inputStream == null) {
                throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "Cannot find Metamodel.xml in classpath");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);

            var builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            return parseMetamodel(document);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Failed to load metamodel", e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "Failed to load metamodel", e);
        }
    }

    /**
     * 解析元模型文档
     */
    private MetamodelData parseMetamodel(Document document) {
        Element root = document.getDocumentElement();

        // 获取版本信息
        String version = getElementText(root, "Version");
        if (version == null || version.isBlank()) {
            version = "1.0.0";
        }

        List<AbstractModel> models = new ArrayList<>();
        Element modelsElement = getElement(root, "Models");

        if (modelsElement != null) {
            NodeList modelNodes = modelsElement.getElementsByTagName("Model");

            for (int i = 0; i < modelNodes.getLength(); i++) {
                Element modelElement = (Element) modelNodes.item(i);
                AbstractModel model = parseModel(modelElement);
                if (model != null) {
                    models.add(model);
                }
            }
        }

        return new MetamodelData(version, models);
    }

    /**
     * 解析单个模型
     */
    private AbstractModel parseModel(Element modelElement) {
        String id = getElementText(modelElement, "Id");
        String name = getElementText(modelElement, "Name");
        String description = getElementText(modelElement, "Desc");
        String importPath = modelElement.getAttribute("import");
        String extendsModel = modelElement.getAttribute("extends");

        if (id == null || id.isBlank()) {
            logger.warn("发现没有ID的模型，跳过");
            return null;
        }

        // 根据模型ID创建相应的模型实例
        AbstractModel model = createModelByType(id, name, description, importPath, extendsModel);

        // 如果有导入路径，加载导入的模型定义
        if (!importPath.isBlank()) {
            loadImportedModel(model, importPath);
        }

        return model;
    }

    /**
     * 根据类型创建模型实例
     */
    private AbstractModel createModelByType(String id, String name, String description,
                                          String importPath, String extendsModel) {
        return switch (id.toLowerCase()) {
            case "pagemodel" -> new PageModel(id, name, description, importPath, extendsModel);
            case "datamodel" -> new DataModel(id, name, description, importPath, extendsModel);
            case "fieldmodel" -> new FieldModel(id, name, description, importPath, extendsModel);
            default -> AbstractModel.create(id, name, description, importPath, extendsModel);
        };
    }

    /**
     * 加载导入的模型定义
     */
    private void loadImportedModel(AbstractModel model, String importPath) {
        try {
            // 处理路径，确保以 /models/ 开头的路径正确解析
            String resourcePath = importPath.startsWith("/")
                ? importPath.substring(1)
                : "models/" + importPath;

            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(resourcePath)) {

                if (inputStream == null) {
                    logger.warn("无法找到导入的模型文件: {}", resourcePath);
                    return;
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                var builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                parseModelDefinition(model, document.getDocumentElement());

            }
        } catch (Exception e) {
            logger.error("加载导入模型失败: {}", importPath, e);
        }
    }

    /**
     * 解析模型定义（属性、组件等）
     */
    private void parseModelDefinition(AbstractModel model, Element root) {
        // 解析属性
        parseProperties(model, root);

        // 解析引用
        parseRefs(model, root);

        // 如果是字段模型，解析组件
        if (model instanceof FieldModel fieldModel) {
            parseComponents(fieldModel, root);
        }
    }

    /**
     * 解析属性
     */
    private void parseProperties(AbstractModel model, Element root) {
        Element propertiesElement = getElement(root, "Properties");
        if (propertiesElement == null) {
            return;
        }

        NodeList propertyNodes = propertiesElement.getElementsByTagName("Property");
        for (int i = 0; i < propertyNodes.getLength(); i++) {
            Element propertyElement = (Element) propertyNodes.item(i);
            Property property = parseProperty(propertyElement);
            if (property != null) {
                model.addProperty(property);
            }
        }
    }

    /**
     * 解析单个属性
     */
    private Property parseProperty(Element propertyElement) {
        String id = getElementText(propertyElement, "Id");
        String name = getElementText(propertyElement, "n");
        String dataType = getElementText(propertyElement, "DataType");
        String description = getElementText(propertyElement, "Desc");
        String defaultValue = getElementText(propertyElement, "Default");

        if (id == null || name == null) {
            return null;
        }

        return new Property(id, name, dataType != null ? dataType : "String",
                          description, defaultValue, false);
    }

    /**
     * 解析引用
     */
    private void parseRefs(AbstractModel model, Element root) {
        Element refsElement = getElement(root, "Refs");
        if (refsElement == null) {
            return;
        }

        NodeList refNodes = refsElement.getElementsByTagName("Ref");
        for (int i = 0; i < refNodes.getLength(); i++) {
            Element refElement = (Element) refNodes.item(i);
            String refText = refElement.getTextContent();
            if (refText != null && !refText.isBlank()) {
                model.addRef(refText.trim());
            }
        }
    }

    /**
     * 解析组件（仅适用于FieldModel）
     */
    private void parseComponents(FieldModel fieldModel, Element root) {
        Element componentsElement = getElement(root, "Components");
        if (componentsElement == null) {
            return;
        }

        NodeList componentNodes = componentsElement.getElementsByTagName("Component");
        for (int i = 0; i < componentNodes.getLength(); i++) {
            Element componentElement = (Element) componentNodes.item(i);
            FieldModel.Component component = parseComponent(componentElement);
            if (component != null) {
                fieldModel.addComponent(component);
            }
        }
    }

    /**
     * 解析单个组件
     */
    private FieldModel.Component parseComponent(Element componentElement) {
        String id = getElementText(componentElement, "Id");
        String name = getElementText(componentElement, "n");
        String description = getElementText(componentElement, "Desc");
        String extendsComponent = componentElement.getAttribute("extends");
        String importPath = componentElement.getAttribute("import");

        if (id == null) {
            return null;
        }

        // 解析引用
        List<String> refs = new ArrayList<>();
        Element refsElement = getElement(componentElement, "Refs");
        if (refsElement != null) {
            NodeList refNodes = refsElement.getElementsByTagName("Ref");
            for (int i = 0; i < refNodes.getLength(); i++) {
                String refText = refNodes.item(i).getTextContent();
                if (refText != null && !refText.isBlank()) {
                    refs.add(refText.trim());
                }
            }
        }

        return new FieldModel.Component(id, name, description, extendsComponent, importPath, refs);
    }

    /**
     * 获取子元素
     */
    private Element getElement(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        return nodeList.getLength() > 0 ? (Element) nodeList.item(0) : null;
    }

    /**
     * 获取元素文本内容
     */
    private String getElementText(Element parent, String tagName) {
        Element element = getElement(parent, tagName);
        return element != null ? element.getTextContent().trim() : null;
    }
}
