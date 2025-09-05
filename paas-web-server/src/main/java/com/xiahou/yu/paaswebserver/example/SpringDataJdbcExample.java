package com.xiahou.yu.paaswebserver.example;

import com.xiahou.yu.paasdomincore.design.repository.RepositoryManager;
import com.xiahou.yu.paaswebserver.entity.DataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Spring Data JDBC Repository 使用示例
 * 展示如何通过 entityName 动态获取对应的 Repository 并执行 CRUD 操作
 *
 * @author xiahou
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringDataJdbcExample {

    private final RepositoryManager repositoryManager;

    /**
     * 示例1：通过 entityName 创建数据模型
     */
    public void createDataModelExample() {
        String entityName = "DataModel";

        // 构造创建数据
        Map<String, Object> data = new HashMap<>();
        data.put("tenant", "tenant001");
        data.put("key", "model001");
        data.put("name", "用户模型");
        data.put("type", "USER");
        data.put("description", "用户基础信息模型");
        data.put("status", "ACTIVE");
        data.put("createdAt", LocalDateTime.now().toString());
        data.put("updatedAt", LocalDateTime.now().toString());

        try {
            if (repositoryManager.hasRepository(entityName)) {
                // 直接使用 Map 数据创建实体（会在策略中转换）
                log.info("Creating {} with data: {}", entityName, data);

                // 也可以直接创建实体对象
                DataModel dataModel = new DataModel();
                dataModel.setTenant("tenant001");
                dataModel.setKey("model001");
                dataModel.setName("用户模型");
                dataModel.setType("USER");
                dataModel.setDescription("用户基础信息模型");
                dataModel.setStatus("ACTIVE");
                dataModel.setCreatedAt(LocalDateTime.now());
                dataModel.setUpdatedAt(LocalDateTime.now());

                // 保存实体
                DataModel saved = repositoryManager.save(entityName, dataModel);
                log.info("Successfully created DataModel: {}", saved);
            }
        } catch (Exception e) {
            log.error("Error creating DataModel: {}", e.getMessage());
        }
    }

    /**
     * 示例2：查询操作
     */
    public void queryDataModelExample() {
        String entityName = "DataModel";

        try {
            // 查询所有数据
            List<DataModel> allModels = repositoryManager.findAll(entityName);
            log.info("Found {} DataModels", allModels.size());

            // 根据ID查询
            Optional<DataModel> modelById = repositoryManager.findById(entityName, 1L);
            if (modelById.isPresent()) {
                log.info("Found DataModel by ID: {}", modelById.get());
            }

            // 根据租户和业务键查询
            DataModel modelByKey = repositoryManager.findByTenantAndKey(entityName, "tenant001", "model001");
            if (modelByKey != null) {
                log.info("Found DataModel by tenant and key: {}", modelByKey);
            }

            // 根据租户查询所有
            List<DataModel> modelsByTenant = repositoryManager.findByTenant(entityName, "tenant001");
            log.info("Found {} DataModels for tenant001", modelsByTenant.size());

        } catch (Exception e) {
            log.error("Error querying DataModel: {}", e.getMessage());
        }
    }

    /**
     * 示例3：更新操作
     */
    public void updateDataModelExample() {
        String entityName = "DataModel";
        Long modelId = 1L;

        try {
            // 先查询现有实体
            Optional<DataModel> existing = repositoryManager.findById(entityName, modelId);
            if (existing.isPresent()) {
                DataModel dataModel = existing.get();

                // 更新字段
                dataModel.setName("更新后的用户模型");
                dataModel.setDescription("更新后的用户基础信息模型");
                dataModel.setUpdatedAt(LocalDateTime.now());

                // 保存更新
                DataModel updated = repositoryManager.save(entityName, dataModel);
                log.info("Successfully updated DataModel: {}", updated);
            } else {
                log.warn("DataModel with ID {} not found", modelId);
            }
        } catch (Exception e) {
            log.error("Error updating DataModel: {}", e.getMessage());
        }
    }

    /**
     * 示例4：删除操作
     */
    public void deleteDataModelExample() {
        String entityName = "DataModel";
        Long modelId = 1L;

        try {
            // 检查实体是否存在
            if (repositoryManager.existsById(entityName, modelId)) {
                // 删除实体
                repositoryManager.deleteById(entityName, modelId);
                log.info("Successfully deleted DataModel with ID: {}", modelId);
            } else {
                log.warn("DataModel with ID {} not found", modelId);
            }
        } catch (Exception e) {
            log.error("Error deleting DataModel: {}", e.getMessage());
        }
    }

    /**
     * 示例5：统计和检查操作
     */
    public void statisticsExample() {
        String entityName = "DataModel";

        try {
            // 统计总数
            long totalCount = repositoryManager.count(entityName);
            log.info("Total DataModel count: {}", totalCount);

            // 检查特定条件的数据是否存在
            boolean exists = repositoryManager.existsByTenantAndKey(entityName, "tenant001", "model001");
            log.info("DataModel[tenant001:model001] exists: {}", exists);

            // 获取所有已注册的实体名称
            java.util.Set<String> allEntities = repositoryManager.getAllEntityNames();
            log.info("All registered entities: {}", allEntities);

        } catch (Exception e) {
            log.error("Error in statistics operations: {}", e.getMessage());
        }
    }

    /**
     * 示例6：批量操作多个实体类型
     */
    public void batchOperationsExample() {
        // 模拟多个实体类型
        String[] entityNames = {"DataModel", "User", "Order"};

        for (String entityName : entityNames) {
            try {
                if (repositoryManager.hasRepository(entityName)) {
                    long count = repositoryManager.count(entityName);
                    log.info("Entity: {}, Count: {}", entityName, count);
                } else {
                    log.warn("No repository found for entity: {}", entityName);
                }
            } catch (Exception e) {
                log.error("Error processing entity {}: {}", entityName, e.getMessage());
            }
        }
    }
}
