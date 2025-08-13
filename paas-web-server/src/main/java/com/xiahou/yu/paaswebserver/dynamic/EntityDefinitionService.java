package com.xiahou.yu.paaswebserver.dynamic;

import com.xiahou.yu.paaswebserver.repository.EntityDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EntityDefinitionService {

    @Autowired
    private EntityDefinitionRepository entityDefinitionRepository;

    public List<EntityDefinition> getAllEntityDefinitions() {
        return entityDefinitionRepository.findAll();
    }

    public Optional<EntityDefinition> getEntityDefinition(String entityName) {
        return entityDefinitionRepository.findByEntityName(entityName);
    }

    public EntityDefinition createEntityDefinition(String entityName, String displayName, String description) {
        if (entityDefinitionRepository.existsByEntityName(entityName)) {
            throw new RuntimeException("Entity definition with name '" + entityName + "' already exists");
        }

        EntityDefinition entityDef = new EntityDefinition(entityName, displayName);
        entityDef.setDescription(description);

        // 添加默认的id字段
        FieldDefinition idField = new FieldDefinition("id", FieldDefinition.FieldType.LONG);
        idField.setRequired(true);
        idField.setDisplayName("ID");
        idField.setDescription("Unique identifier");
        entityDef.addField(idField);

        return entityDefinitionRepository.save(entityDef);
    }

    public EntityDefinition updateEntityDefinition(Long id, String displayName, String description) {
        EntityDefinition entityDef = entityDefinitionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entity definition not found with id: " + id));

        if (displayName != null) {
            entityDef.setDisplayName(displayName);
        }
        if (description != null) {
            entityDef.setDescription(description);
        }

        return entityDefinitionRepository.save(entityDef);
    }

    public boolean deleteEntityDefinition(Long id) {
        if (entityDefinitionRepository.existsById(id)) {
            entityDefinitionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public FieldDefinition addFieldToEntity(Long entityId, String fieldName, FieldDefinition.FieldType fieldType,
                                          Boolean required, String defaultValue, String displayName, String description) {
        EntityDefinition entityDef = entityDefinitionRepository.findById(entityId)
            .orElseThrow(() -> new RuntimeException("Entity definition not found with id: " + entityId));

        // 检查字段名是否已存在
        boolean fieldExists = entityDef.getFields().stream()
            .anyMatch(field -> field.getFieldName().equals(fieldName));

        if (fieldExists) {
            throw new RuntimeException("Field '" + fieldName + "' already exists in entity '" + entityDef.getEntityName() + "'");
        }

        FieldDefinition fieldDef = new FieldDefinition(fieldName, fieldType);
        fieldDef.setRequired(required != null ? required : false);
        fieldDef.setDefaultValue(defaultValue);
        fieldDef.setDisplayName(displayName);
        fieldDef.setDescription(description);

        entityDef.addField(fieldDef);
        entityDefinitionRepository.save(entityDef);

        return fieldDef;
    }
}
