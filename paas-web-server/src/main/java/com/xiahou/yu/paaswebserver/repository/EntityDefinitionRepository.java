package com.xiahou.yu.paaswebserver.repository;

import com.xiahou.yu.paaswebserver.dynamic.EntityDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntityDefinitionRepository extends JpaRepository<EntityDefinition, Long> {
    Optional<EntityDefinition> findByEntityName(String entityName);
    boolean existsByEntityName(String entityName);
}
