package com.xiahou.yu.paaswebserver.dynamic;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entity_definitions")
public class EntityDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", unique = true, nullable = false)
    private String entityName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "entityDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldDefinition> fields = new ArrayList<>();

    // Constructors
    public EntityDefinition() {}

    public EntityDefinition(String entityName, String displayName) {
        this.entityName = entityName;
        this.displayName = displayName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<FieldDefinition> getFields() { return fields; }
    public void setFields(List<FieldDefinition> fields) { this.fields = fields; }

    public void addField(FieldDefinition field) {
        fields.add(field);
        field.setEntityDefinition(this);
    }
}
