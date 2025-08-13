package com.xiahou.yu.paasmetacore.models;

import com.xiahou.yu.paasmetacore.models.props.Property;
import java.util.List;
import java.util.Map;

/**
 * 字段模型，继承自AbstractModel
 */
public final class FieldModel extends AbstractModel {

    private final Map<String, Component> components = new java.util.concurrent.ConcurrentHashMap<>();

    public FieldModel(String id, String name, String description, String importPath, String extendsModel) {
        super(id, name, description, importPath, extendsModel);
    }

    /**
     * 添加组件
     */
    public void addComponent(Component component) {
        components.put(component.id(), component);
    }

    /**
     * 获取所有组件
     */
    public List<Component> getComponents() {
        return List.copyOf(components.values());
    }

    /**
     * 获取指定组件
     */
    public Component getComponent(String id) {
        return components.get(id);
    }

    @Override
    public String getDisplayInfo() {
        return "字段模型: " + super.getDisplayInfo() + " (组件数: " + components.size() + ")";
    }

    /**
     * 组件记录类，使用Java 21 record
     */
    public record Component(
        String id,
        String name,
        String description,
        String extendsComponent,
        String importPath,
        List<String> refs
    ) {
        public Component {
            // 紧凑构造器验证
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Component ID cannot be null or blank");
            }
        }

        public boolean hasParent() {
            return extendsComponent != null && !extendsComponent.isBlank();
        }
    }
}
