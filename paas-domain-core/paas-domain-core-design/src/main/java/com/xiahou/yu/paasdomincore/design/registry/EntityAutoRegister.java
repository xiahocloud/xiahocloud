package com.xiahou.yu.paasdomincore.design.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 实体自动注册器
 * 在 Spring 容器启动时自动扫描并注册所有实现了 EntityRegister 接口的实体
 *
 * @author wanghaoxin
 * @date 2025/9/4
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityAutoRegister implements InitializingBean {

    private final ApplicationContext applicationContext;
    private final EntityTypeRegister entityTypeRegister;

    @Override
    public void afterPropertiesSet() throws Exception {
        autoRegisterEntities();
    }

    /**
     * 自动注册所有实现了 EntityRegister 接口的实体
     */
    private void autoRegisterEntities() {
        log.info("Starting auto registration of entities...");

        // 获取所有实现了 EntityRegister 接口的 Bean
        Map<String, EntityRegister> entityRegisterBeans =
                applicationContext.getBeansOfType(EntityRegister.class);

        if (entityRegisterBeans.isEmpty()) {
            log.info("No EntityRegister implementations found");
            return;
        }

        int registeredCount = 0;
        for (Map.Entry<String, EntityRegister> entry : entityRegisterBeans.entrySet()) {
            String beanName = entry.getKey();
            EntityRegister entityRegister = entry.getValue();

            try {
                entityTypeRegister.register(entityRegister);
                registeredCount++;
                log.debug("Auto registered entity bean: {} -> {}", beanName, entityRegister.getClass().getName());
            } catch (Exception e) {
                log.error("Failed to auto register entity bean: {}", beanName, e);
            }
        }

        log.info("Auto registration completed. Total registered: {}", registeredCount);
    }
}
