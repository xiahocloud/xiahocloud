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
     * 获取实体类型，默认为标准实体
     * @return 实体类型
     */
    default EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STD_ENTITY;
    }
}
