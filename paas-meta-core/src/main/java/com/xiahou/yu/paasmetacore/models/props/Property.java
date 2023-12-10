package com.xiahou.yu.paasmetacore.models.props;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: 组件属性
 *
 * @author wanghaoxin
 * date     2023/5/30 23:11
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class Property {
    private String id;
    private String name;
    private String desc;
}
