package com.xiahou.yu.paasmetacore.models.props;

import com.xiahou.yu.paasmetacore.constant.DataTypeEnum;
import lombok.Data;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/5/30 23:36
 * @version 1.0
 */
@Data
public abstract class AbstractProperty {
    private String id;
    private String name;
    private String desc;
    private DataTypeEnum dataType;

}
