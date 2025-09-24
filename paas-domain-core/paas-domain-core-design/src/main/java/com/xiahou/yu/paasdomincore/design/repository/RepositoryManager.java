package com.xiahou.yu.paasdomincore.design.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 仓储管理服务 - Spring Data JDBC 版本
 * 提供统一的数据访问接口，根据实体名称动态获取对应的 Repository
 *
 * @author xiahou
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RepositoryManager {

    private final RepositoryRegister repositoryRegister;

    /**
     * 根据实体名称获取 Repository
     *
     * @param entityName 实体名称
     * @return Repository 实例
     */
    public <T, ID extends Serializable> BaseRepository<T, ID> getRepository(String entityName) {
        BaseRepository<?, ?> repository = repositoryRegister.getRepository(entityName);
        if (repository == null) {
            throw new IllegalArgumentException("No repository found for entity: " + entityName);
        }
        return (BaseRepository<T, ID>) repository;
    }

    /**
     * 根据实体名称和ID查找实体
     *
     * @param entityName 实体名称
     * @param id 实体ID
     * @return 实体对象
     */
    public <T, ID extends Serializable> Optional<T> findById(String entityName, ID id) {
        BaseRepository<T, ID> repository = getRepository(entityName);
        return repository.findById(id);
    }

    /**
     * 根据实体名称保存实体
     *
     * @param entityName 实体名称
     * @param entity 实体对象
     * @return 保存后的实体
     */
    public <T> T save(String entityName, T entity) {
        BaseRepository<T, Serializable> repository = getRepository(entityName);
        return repository.save(entity);
    }

    public <T> List<T> saveAll(String entityName, List<T> entities) {
        return saveByPage(entityName, entities);
    }

    private <T> List<T> saveByPage(String entityName, List<T> entities) {
        BaseRepository<T, Serializable> repository = getRepository(entityName);
        int batchSize = 1000;
        List<T> result = new ArrayList<>();
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entities.size());
            List<T> batch = entities.subList(i, end);
            Iterable<T> saved = repository.saveAll(batch);
            return StreamSupport.stream(saved.spliterator(), false).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 根据实体名称删除���体
     *
     * @param entityName 实体名称
     * @param id 实体ID
     */
    public <ID extends Serializable> void deleteById(String entityName, ID id) {
        BaseRepository<?, ID> repository = getRepository(entityName);
        repository.deleteById(id);
    }

    /**
     * 根据实体名称查找所有实体（JDBC版本返回Iterable）
     *
     * @param entityName 实体名称
     * @return 实体列表
     */
    public <T> List<T> findAll(String entityName) {
        BaseRepository<T, Serializable> repository = getRepository(entityName);
        Iterable<T> iterable = repository.findAll();

        // ��Iterable转换为List
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    /**
     * 根据实体名称、租户和业务键查找实体
     *
     * @param entityName 实体名称
     * @param tenant 租户编码
     * @param key 业务键
     * @return 实体对象
     */
/*
    public <T> T findByTenantAndKey(String entityName, String tenant, String key) {
        BaseRepository<T, Serializable> repository = getRepository(entityName);
        return repository.findByTenantAndKey(tenant, key);
    }
*/

    /**
     * 根据租户查找所有实体
     *
     * @param entityName 实体名称
     * @param tenant 租户编码
     * @return 实体列表
     */
    public <T> List<T> findByTenant(String entityName, String tenant) {
        BaseRepository<T, Serializable> repository = getRepository(entityName);
        Iterable<T> iterable = repository.findByTenant(tenant);

        // 将Iterable转换为List
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    /**
     * 检查实体是否存在
     *
     * @param entityName 实体名称
     * @param tenant 租户编码
     * @param key 业务键
     * @return 是否存在
     */
    public boolean existsByTenantAndKey(String entityName, String tenant, String key) {
        BaseRepository<?, ?> repository = getRepository(entityName);
        return repository.existsByTenantAndKey(tenant, key);
    }

    /**
     * 检查实体是否存在（根据ID）
     *
     * @param entityName 实体名称
     * @param id 实体ID
     * @return 是否存在
     */
    public <ID extends Serializable> boolean existsById(String entityName, ID id) {
        BaseRepository<?, ID> repository = getRepository(entityName);
        return repository.existsById(id);
    }

    /**
     * 统计实体数量
     *
     * @param entityName 实体名称
     * @return 实体数量
     */
    public long count(String entityName) {
        BaseRepository<?, ?> repository = getRepository(entityName);
        return repository.count();
    }

    /**
     * 检查是否有对应的 Repository
     *
     * @param entityName 实体名称
     * @return 是否存在
     */
    public boolean hasRepository(String entityName) {
        return repositoryRegister.hasRepository(entityName);
    }

    /**
     * 获取所有已注册的实体名称
     *
     * @return 实体名称集合
     */
    public java.util.Set<String> getAllEntityNames() {
        return repositoryRegister.getAllEntityNames();
    }
}
