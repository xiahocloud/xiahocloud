package com.xiahou.yu.paasmetacore.protobuf.conf;

import com.xiahou.yu.paasmetacore.protobuf.MetaModelProto;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/3/20 23:48
 * @version 1.0
 */
public class AbstractModelConf {
    public static MetaModelProto.AbstractModel fetchContextModel() {
        MetaModelProto.AbstractModel contextModel = MetaModelProto.AbstractModel.newBuilder()
                .setId("100")
                .setKey(MetaModelProto.DomainModelCategoryEnum.CONTEXT.name())
                .setName("上下文模型")
                .setCategory(MetaModelProto.DomainModelCategoryEnum.CONTEXT)
                .build();
        return contextModel;
    }

    public static MetaModelProto.AbstractModel fetchFormModel() {
        MetaModelProto.AbstractModel formModel = MetaModelProto.AbstractModel.newBuilder()
                .setId("101")
                .setKey(MetaModelProto.DomainModelCategoryEnum.FORM.name())
                .setName("表单模型")
                .setCategory(MetaModelProto.DomainModelCategoryEnum.FORM)
                .build();
        return formModel;
    }

    public static MetaModelProto.AbstractModel fetchEntityModel() {
        MetaModelProto.AbstractModel entityModel = MetaModelProto.AbstractModel.newBuilder()
                .setId("102")
                .setKey(MetaModelProto.DomainModelCategoryEnum.ENTITY.name())
                .setName("实体模型")
                .setCategory(MetaModelProto.DomainModelCategoryEnum.ENTITY)
                .build();
        return entityModel;
    }

    public static MetaModelProto.AbstractModel fetchUIModel() {
        MetaModelProto.AbstractModel uiModel = MetaModelProto.AbstractModel.newBuilder()
                .setId("103")
                .setKey(MetaModelProto.DomainModelCategoryEnum.UI.name())
                .setName("UI模型")
                .setCategory(MetaModelProto.DomainModelCategoryEnum.UI)
                .build();
    }
}
