package com.xiahou.yu.paasmetacore;

import com.xiahou.yu.paasmetacore.models.MetamodelDefiner;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * description: 元模型
 *
 * @author wanghaoxin
 * date     2022/8/31 00:09
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class MetaModel {
    private String version;

    private List<Model> models;

    private List<Category> categories;


    @Data
    public static class Model {
        private String id;
        private String name;
        private String desc;
        private boolean creatable = true;
        private String resourcePath;
        private MetamodelDefiner metamodelDefiner;
    }

    /**
     * description: 模型分类
     *
     * @author wanghaoxin
     * date     2023/5/30 23:12
     * @version 1.0
     */
    @Data
    public static class Category {
        private String id;
        private String name;
        private String desc;
    }
}
