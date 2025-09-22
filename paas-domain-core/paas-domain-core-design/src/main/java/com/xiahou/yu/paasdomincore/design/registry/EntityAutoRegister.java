package com.xiahou.yu.paasdomincore.design.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体自动注册器
 * 在 Spring 容器启动时自动扫描并注册所有实现了 EntityRegister 接口的实体
 *
 * @author wanghaoxin
 * @since 2025/9/4
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityAutoRegister implements InitializingBean {

    private final ApplicationContext applicationContext;
    private final EntityTypeRegister entityTypeRegister;

    // 需要扫描的包路径
    private static final String[] SCAN_PACKAGES = {
        "com.xiahou.yu.paasdomincore.design.metamodel",
        "com.xiahou.yu.paasmetacore.models"
    };

    @Override
    public void afterPropertiesSet() {
        StopWatch stopWatch = new StopWatch("EntityAutoRegister");
        stopWatch.start("autoRegisterEntities");

        try {
            autoRegisterEntities();
        } finally {
            stopWatch.stop();
            log.info("Entity auto registration completed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * 自动注册所有实现了 EntityRegister 接口的实体
     */
    private void autoRegisterEntities() {
        log.info("Starting auto registration of entities...");

        // 2. 扫描类路径查找所有实现了 EntityRegister 接口的类
        List<EntityRegister> allEntityRegisters = scanEntityRegisterClasses();
        log.debug("Found {} EntityRegister classes from classpath scanning", allEntityRegisters.size());

        if (allEntityRegisters.isEmpty()) {
            log.info("No EntityRegister implementations found");
            return;
        }

        // 3. 注册所有找到的实体
        int registeredCount = 0;
        for (EntityRegister entityRegister : allEntityRegisters) {
            try {
                entityTypeRegister.register(entityRegister);
                registeredCount++;
                log.debug("Auto registered entity: {} -> {}",
                    entityRegister.getEntityName(),
                    entityRegister.getClass().getName());
            } catch (Exception e) {
                log.error("Failed to auto register entity: {}",
                    entityRegister.getClass().getName(), e);
            }
        }

        log.info("Auto registration completed. Total registered: {}", registeredCount);
    }

    /**
     * 扫描类路径查找所有实现了 EntityRegister 接口的类
     */
    private List<EntityRegister> scanEntityRegisterClasses() {
        List<EntityRegister> entityRegisters = new ArrayList<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

        for (String packageName : SCAN_PACKAGES) {
            try {
                String packagePath = packageName.replace('.', '/');
                String resourcePattern = "classpath*:" + packagePath + "/**/*.class";
                Resource[] resources = resourcePatternResolver.getResources(resourcePattern);

                log.debug("Scanning package: {} (found {} classes)", packageName, resources.length);

                for (Resource resource : resources) {
                    try {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();

                        // 检查是否实现了 EntityRegister 接口
                        if (implementsEntityRegister(metadataReader)) {
                            Class<?> clazz = Class.forName(className);

                            // 跳过接口和抽象类
                            if (!clazz.isInterface() && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                                // 检查是否已经是 Spring Bean，避免重复注册
                                if (!isSpringBean(clazz)) {
                                    EntityRegister instance = (EntityRegister) clazz.getDeclaredConstructor().newInstance();
                                    entityRegisters.add(instance);
                                    log.debug("Found EntityRegister implementation: {}", className);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Failed to process resource: {}", resource.getDescription(), e);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to scan package: {}", packageName, e);
            }
        }

        return entityRegisters;
    }

    /**
     * 检查类是否实现了 EntityRegister 接口
     */
    private boolean implementsEntityRegister(MetadataReader metadataReader) {
        String[] interfaceNames = metadataReader.getClassMetadata().getInterfaceNames();
        for (String interfaceName : interfaceNames) {
            if (EntityRegister.class.getName().equals(interfaceName)) {
                return true;
            }
        }

        // 检查父类是否实现了接口
        String superClassName = metadataReader.getClassMetadata().getSuperClassName();
        if (superClassName != null && !Object.class.getName().equals(superClassName)) {
            try {
                Class<?> superClass = Class.forName(superClassName);
                return EntityRegister.class.isAssignableFrom(superClass);
            } catch (ClassNotFoundException e) {
                log.debug("Failed to load super class: {}", superClassName);
            }
        }

        return false;
    }

    /**
     * 检查类是否已经是 Spring Bean
     */
    private boolean isSpringBean(Class<?> clazz) {
        try {
            return !applicationContext.getBeansOfType(clazz).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
