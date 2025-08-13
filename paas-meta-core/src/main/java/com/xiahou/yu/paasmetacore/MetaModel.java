package com.xiahou.yu.paasmetacore;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.models.AbstractModel;
import com.xiahou.yu.paasmetacore.models.reader.MetamodelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 元模型主类，使用Java 21语法
 * 负责加载和管理所有模型定义
 * @author paas-meta-core
 */
public sealed class MetaModel permits MetaModel.Instance {

    private static final Logger logger = LoggerFactory.getLogger(MetaModel.class);
    private static volatile Instance instance;

    private MetaModel() {}

    public static Instance getInstance() {
        if (instance == null) {
            synchronized (MetaModel.class) {
                if (instance == null) {
                    instance = new Instance();
                }
            }
        }
        return instance;
    }

    public static final class Instance extends MetaModel {
        private final Map<String, AbstractModel> models = new ConcurrentHashMap<>();
        private final MetamodelReader reader = new MetamodelReader();
        private String version;

        private Instance() {
            loadMetamodel();
        }

        /**
         * 加载元模型
         */
        private void loadMetamodel() {
            try {
                logger.info("开始加载元模型...");
                MetamodelReader.MetamodelData metamodel = reader.loadMetamodel();
                this.version = metamodel.version();

                // 处理模型继承和引用关系
                metamodel.models().forEach(model -> {
                    models.put(model.getId(), model);
                    logger.debug("加载模型: {} - {}", model.getId(), model.getName());
                });

                // 解析继承关系
                resolveInheritance();

                logger.info("元模型加载完成，版本: {}, 模型数量: {}", version, models.size());
            } catch (Exception e) {
                logger.error("加载元模型失败", e);
                throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "Failed to load metamodel", e);
            }
        }

        /**
         * 解析模型继承关系
         */
        private void resolveInheritance() {
            models.values().parallelStream()
                .filter(model -> model.getExtendsModel() != null)
                .forEach(model -> {
                    AbstractModel parentModel = models.get(model.getExtendsModel());
                    if (parentModel != null) {
                        model.setParent(parentModel);
                        logger.debug("建立继承关系: {} extends {}",
                                   model.getId(), parentModel.getId());
                    }
                });
        }

        public String getVersion() {
            return version;
        }

        public AbstractModel getModel(String id) {
            return models.get(id);
        }

        public List<AbstractModel> getAllModels() {
            return List.copyOf(models.values());
        }

        public Map<String, AbstractModel> getModelsMap() {
            return Collections.unmodifiableMap(models);
        }
    }
}
