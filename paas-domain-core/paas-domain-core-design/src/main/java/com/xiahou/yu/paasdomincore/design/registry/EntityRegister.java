package com.xiahou.yu.paasdomincore.design.registry;

import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;

/**
 * 实体注册接口
 * 实现此接口的类将自动注册到 EntityTypeRegister 中
 *
 * @author wanghaoxin
 * @date 2025/9/4
 */
public interface EntityRegister {

    /**
     * 获取实体名称
     * @return 实体名称
     */
    default String getEntityName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取实体类型，默认为标准实体
     * @return 实体类型
     */
    default EntityTypeEnum getEntityType() {
        return EntityTypeEnum.SYSTEM_ENTITY;
    }

    /**
     * 获取实体描述
     * @return 实体描述
     */
    default String getDesc() {
        return getEntityName() + " - 系统实体";
    }

    /**
     * 获取实体类
     * @return 实体类
     */
    default Class<?> getEntityClass() {
        return this.getClass();
    }
}
