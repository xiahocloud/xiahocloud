package com.xiahou.yu.paasmetacore.metadata;

import lombok.Data;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/12/3 20:07
 * @version 1.0
 */

@Data
public abstract sealed class AbstractMetaData permits MetaEntity, MetaField, MetaPage {
}
