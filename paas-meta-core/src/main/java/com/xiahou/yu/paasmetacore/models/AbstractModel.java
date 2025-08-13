package com.xiahou.yu.paasmetacore.models;

import com.xiahou.yu.paasmetacore.models.props.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象模型基类，使用Java 21语法
 * @author paas-meta-core
 */
public sealed class AbstractModel
    permits AbstractModel.ConcreteModel, PageModel, FieldModel, DataModel {

    private static final Logger logger = LoggerFactory.getLogger(AbstractModel.class);

    protected final String id;
    protected final String name;
    protected final String description;
    protected final String importPath;
    protected final String extendsModel;

    protected AbstractModel parent;
    protected final Map<String, Property> properties = new ConcurrentHashMap<>();
    protected final Set<String> refs = ConcurrentHashMap.newKeySet();

    protected AbstractModel(String id, String name, String description, String importPath, String extendsModel) {
        this.id = Objects.requireNonNull(id, "Model ID cannot be null");
        this.name = name;
        this.description = description;
        this.importPath = importPath;
        this.extendsModel = extendsModel;
    }

    /**
     * 获取显示信息，使用传统的字符串拼接而非Java 21的字符串模板
     */
    public String getDisplayInfo() {
        if (this instanceof ConcreteModel && hasParent()) {
            return name + " (继承自: " + parent.getName() + ")";
        } else if (this instanceof ConcreteModel) {
            return name + " (基础模型)";
        }
        return name;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImportPath() { return importPath; }
    public String getExtendsModel() { return extendsModel; }
    public AbstractModel getParent() { return parent; }

    public boolean hasParent() { return parent != null; }

    public void setParent(AbstractModel parent) {
        this.parent = parent;
    }

    /**
     * 获取所有属性（包括继承的）
     */
    public Map<String, Property> getAllProperties() {
        if (parent == null) {
            return Collections.unmodifiableMap(properties);
        }

        Map<String, Property> allProps = new HashMap<>(parent.getAllProperties());
        allProps.putAll(properties);
        return Collections.unmodifiableMap(allProps);
    }

    /**
     * 获取直接属性
     */
    public Map<String, Property> getDirectProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * 添加属性
     */
    public void addProperty(Property property) {
        properties.put(property.id(), property);
        logger.debug("为模型 {} 添加属性: {}", id, property.id());
    }

    /**
     * 添加引用
     */
    public void addRef(String ref) {
        refs.add(ref);
    }

    public Set<String> getRefs() {
        return Collections.unmodifiableSet(refs);
    }

    /**
     * 检查是否有指定属性（包括继承的）
     */
    public boolean hasProperty(String propertyId) {
        return getAllProperties().containsKey(propertyId);
    }

    /**
     * 获取属性（包括继承的）
     */
    public Optional<Property> getProperty(String propertyId) {
        return Optional.ofNullable(getAllProperties().get(propertyId));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractModel)) return false;
        AbstractModel other = (AbstractModel) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbstractModel{id='" + id + "', name='" + name +
               "', parent=" + (parent != null ? parent.getId() : "none") + "}";
    }

    /**
     * 具体模型实现
     */
    public static final class ConcreteModel extends AbstractModel {
        public ConcreteModel(String id, String name, String description, String importPath, String extendsModel) {
            super(id, name, description, importPath, extendsModel);
        }
    }

    /**
     * 创建模型的工厂方法
     */
    public static AbstractModel create(String id, String name, String description, String importPath, String extendsModel) {
        return new ConcreteModel(id, name, description, importPath, extendsModel);
    }
}
