package com.xiahou.yu.paasmetacore.protobuf.conf;

import com.xiahou.yu.paasmetacore.protobuf.MetaModelProto;

import java.util.List;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/3/17 14:34
 * @version 1.0
 */
public final class MetaModelConf {
    public static MetaModelProto.MetaModel fetchMetaModel() {
        MetaModelProto.MetaModel metaModel = MetaModelProto.MetaModel.newBuilder()
                .setVersion("0.0.1.001")
                .addModels(AbstractModelConf.fetchContextModel())
                .addModels(AbstractModelConf.fetchFormModel())
                .addModels(AbstractModelConf.fetchEntityModel())
                .build();
        return metaModel;
    }
}
