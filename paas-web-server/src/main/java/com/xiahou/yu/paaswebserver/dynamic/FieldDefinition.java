package com.xiahou.yu.paaswebserver.dynamic;

import jakarta.persistence.*;

@Entity
@Table(name = "field_definitions")
public class FieldDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "field_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

    @Column(name = "is_required")
    private Boolean required = false;

    @Column(name = "is_list")
    private Boolean list = false;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "validation_rules")
    private String validationRules;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_definition_id")
    private EntityDefinition entityDefinition;

    // 关联实体类型（用于关系字段）
    @Column(name = "reference_entity")
    private String referenceEntity;

    public enum FieldType {
        STRING, INTEGER, LONG, DOUBLE, BOOLEAN, DATE, DATETIME,
        TEXT, JSON, REFERENCE, FILE, EMAIL, URL
    }

    // Constructors
    public FieldDefinition() {}

    public FieldDefinition(String fieldName, FieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public FieldType getFieldType() { return fieldType; }
    public void setFieldType(FieldType fieldType) { this.fieldType = fieldType; }

    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }

    public Boolean getList() { return list; }
    public void setList(Boolean list) { this.list = list; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public EntityDefinition getEntityDefinition() { return entityDefinition; }
    public void setEntityDefinition(EntityDefinition entityDefinition) { this.entityDefinition = entityDefinition; }

    public String getReferenceEntity() { return referenceEntity; }
    public void setReferenceEntity(String referenceEntity) { this.referenceEntity = referenceEntity; }
}
