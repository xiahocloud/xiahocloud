package com.xiahou.yu.paasmetacore.models;

import com.xiahou.yu.paasmetacore.models.props.Property;
import lombok.Data;

import java.util.List;

/**
 * description: 字段模型
 *
 * @author wanghaoxin
 * date     2023/12/3 18:58
 * @version 1.0
 */
@Data
public non-sealed class FieldModel extends AbstractModel {

    private List<Field> fields;

    @Data
    public static class Field {
        private String id;
        private String name;
        private String desc;

        private List<Property> properties;
    }

}
