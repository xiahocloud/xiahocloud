package com.xiahou.yu.paaswebserver.dynamic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicEntityRepository extends JpaRepository<DynamicEntity, Long> {

    List<DynamicEntity> findByEntityType(String entityType);

    @Query("SELECT d FROM DynamicEntity d WHERE d.entityType = :entityType AND " +
           "(:fieldName IS NULL OR d.attributes[:fieldName] LIKE %:fieldValue%)")
    List<DynamicEntity> findByEntityTypeAndAttribute(
        @Param("entityType") String entityType,
        @Param("fieldName") String fieldName,
        @Param("fieldValue") String fieldValue
    );

    @Query("SELECT d FROM DynamicEntity d WHERE d.entityType = :entityType " +
           "ORDER BY d.id DESC")
    List<DynamicEntity> findByEntityTypeOrderByIdDesc(@Param("entityType") String entityType);

    void deleteByEntityType(String entityType);
}
