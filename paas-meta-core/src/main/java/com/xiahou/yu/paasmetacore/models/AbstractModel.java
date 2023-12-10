package com.xiahou.yu.paasmetacore.models;

import com.xiahou.yu.paasmetacore.models.props.Property;
import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/5/30 23:10
 * @version 1.0
 */

@Data
public sealed class AbstractModel implements ComponentAdapter, MetamodelDefiner permits FieldModel, PageModel {

    private List<Property> properties;

}
