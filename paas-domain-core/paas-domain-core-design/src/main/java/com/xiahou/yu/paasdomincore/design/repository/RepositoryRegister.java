package com.xiahou.yu.paasdomincore.design.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仓储注册器
 * 动态注册和获取 Repository 实例
 *
 * @author xiahou
 */
@Component
@Slf4j
public class RepositoryRegister implements InitializingBean {

    private final ApplicationContext applicationContext;
    private final Map<String, BaseRepository<?, ?>> repositoryMap = new ConcurrentHashMap<>();

    public RepositoryRegister(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        autoRegisterRepositories();
    }

    /**
     * 自动注册所有 BaseRepository 实现
     */
    private void autoRegisterRepositories() {
        log.info("Starting auto registration of repositories...");

        // 获取所有 BaseRepository 的实现
        Map<String, BaseRepository> repositories = applicationContext.getBeansOfType(BaseRepository.class);

        for (Map.Entry<String, BaseRepository> entry : repositories.entrySet()) {
            String beanName = entry.getKey();
            BaseRepository<?, ?> repository = entry.getValue();

            // 从 Bean 名称推断实体名称
            String entityName = extractEntityNameFromBeanName(beanName);

            if (entityName != null) {
                repositoryMap.put(entityName, repository);
                log.info("Registered repository: {} -> {}", entityName, beanName);
            }
        }

        log.info("Auto registration completed. Total registered: {}", repositoryMap.size());
    }

    /**
     * 手动注册 Repository
     *
     * @param entityName 实体名称
     * @param repository Repository 实例
     */
    public void register(String entityName, BaseRepository<?, ?> repository) {
        repositoryMap.put(entityName, repository);
        log.info("Manually registered repository: {} -> {}", entityName, repository.getClass().getSimpleName());
    }

    /**
     * 根据实体名称获取 Repository
     *
     * @param entityName 实体名称
     * @return Repository 实例
     */
    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> BaseRepository<T, ID> getRepository(String entityName) {
        BaseRepository<?, ?> repository = repositoryMap.get(entityName);
        if (repository == null) {
            log.warn("No repository found for entity: {}", entityName);
            return null;
        }
        return (BaseRepository<T, ID>) repository;
    }

    /**
     * 检查是否存在指定实体的 Repository
     *
     * @param entityName 实体名称
     * @return 是否存在
     */
    public boolean hasRepository(String entityName) {
        return repositoryMap.containsKey(entityName);
    }

    /**
     * 移除指定实体的 Repository
     *
     * @param entityName 实体名称
     */
    public void removeRepository(String entityName) {
        repositoryMap.remove(entityName);
    }

    /**
     * 获取所有已注册的实体名称
     *
     * @return 实体名称集合
     */
    public java.util.Set<String> getAllEntityNames() {
        return repositoryMap.keySet();
    }

    /**
     * 从 Bean 名称中提取实体名称
     * 例如：dataModelRepository -> EntityModel
     *      userRepository -> User
     *
     * @param beanName Bean 名称
     * @return 实体名称
     */
    private String extractEntityNameFromBeanName(String beanName) {
        if (beanName == null || !beanName.toLowerCase().endsWith("repository")) {
            return null;
        }

        // 移除 "Repository" 后缀
        String entityName = beanName.substring(0, beanName.length() - "Repository".length());

        // 首字母大写
        if (!entityName.isEmpty()) {
            return Character.toUpperCase(entityName.charAt(0)) + entityName.substring(1);
        }

        return null;
    }

    /**
     * 清空注册信息（主要用于测试）
     */
    public void clear() {
        repositoryMap.clear();
        log.info("Repository register cleared");
    }
}
