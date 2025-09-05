吃package com.xiahou.yu.paaswebserver.example;

import com.xiahou.yu.paasdomincore.design.repository.BaseRepository;
import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Repository 动态获取示例
 * 展示如何通过 entityName 动态获取对应的 BaseRepository
 *
 * @author xiahou
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepositoryUsageExample {

    private final RepositoryManager repositoryManager;

    /**
     * 示例1：通过 entityName 获取 Repository 并执行基本操作
     */
    public void basicRepositoryUsage() {
        String entityName = "DataModel";

        try {
            // 检查是否存在对应的 Repository
            if (repositoryManager.hasRepository(entityName)) {
                log.info("Found repository for entity: {}", entityName);

                // 获取 Repository 实例
                BaseRepository<Object, Long> repository = repositoryManager.getRepository(entityName);

                // 执行查询操作
                List<Object> allEntities = repositoryManager.findAll(entityName);
                log.info("Found {} entities of type {}", allEntities.size(), entityName);

                // 根据ID查询
                Optional<Object> entity = repositoryManager.findById(entityName, 1L);
                if (entity.isPresent()) {
                    log.info("Found entity with ID 1: {}", entity.get());
                }

                // 根据租户和业务键查询
                Object entityByKey = repositoryManager.findByTenantAndKey(entityName, "tenant001", "key001");
                if (entityByKey != null) {
                    log.info("Found entity by tenant and key: {}", entityByKey);
                }

            } else {
                log.warn("No repository found for entity: {}", entityName);
            }
        } catch (Exception e) {
            log.error("Error accessing repository for entity: {}", entityName, e);
        }
    }

    /**
     * 示例2：批量操作多个实体类型
     */
    public void batchRepositoryOperations() {
        // 获取所有已注册的实体名称
        java.util.Set<String> allEntityNames = repositoryManager.getAllEntityNames();
        log.info("All registered entities: {}", allEntityNames);

        // 批量查询所有实体类型的数据
        for (String entityName : allEntityNames) {
            try {
                List<Object> entities = repositoryManager.findAll(entityName);
                log.info("Entity: {}, Count: {}", entityName, entities.size());
            } catch (Exception e) {
                log.error("Error querying entity: {}", entityName, e);
            }
        }
    }

    /**
     * 示例3：动态保存实体
     */
    public void dynamicSaveExample() {
        String entityName = "DataModel";

        try {
            if (repositoryManager.hasRepository(entityName)) {
                // 注意：这里需要根据实际的实体类型来创建对象
                // Object newEntity = createEntityInstance(entityName);
                // Object savedEntity = repositoryManager.save(entityName, newEntity);
                // log.info("Saved entity: {}", savedEntity);

                log.info("Repository available for entity: {}", entityName);
            }
        } catch (Exception e) {
            log.error("Error saving entity: {}", entityName, e);
        }
    }

    /**
     * 示例4：检查实体是否存在
     */
    public void checkEntityExists() {
        String entityName = "DataModel";
        String tenant = "tenant001";
        String key = "key001";

        try {
            boolean exists = repositoryManager.existsByTenantAndKey(entityName, tenant, key);
            log.info("Entity {}[{}:{}] exists: {}", entityName, tenant, key, exists);
        } catch (Exception e) {
            log.error("Error checking entity existence: {}", entityName, e);
        }
    }

    /**
     * 示例5：动态删除实体
     */
    public void dynamicDeleteExample() {
        String entityName = "DataModel";
        Long entityId = 1L;

        try {
            if (repositoryManager.hasRepository(entityName)) {
                repositoryManager.deleteById(entityName, entityId);
                log.info("Deleted entity: {} with ID: {}", entityName, entityId);
            }
        } catch (Exception e) {
            log.error("Error deleting entity: {}", entityName, e);
        }
    }
}
