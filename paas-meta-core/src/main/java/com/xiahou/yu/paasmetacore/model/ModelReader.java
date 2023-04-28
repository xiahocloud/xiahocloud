package com.xiahou.yu.paasmetacore.model;

import com.xiahou.yu.paasmetacore.utils.NullObject;
import com.xiahou.yu.paasmetacore.utils.XmlParseUtils;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.SysConstant;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.net.URL;
import java.util.List;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2022/9/4 14:46
 * @version 1.0
 */
public class ModelReader {

    /**
     * <p> 列出所有的模型 </p>
     *
     * @return 所有模型
     */
    public MetaModel listMetaModel() {
        MetaModel metaModel = new MetaModel();
        try {
            URL url = ModelReader.class.getResource(SysConstant.META_MODEL_ROOT_PATH);
            final Document document = XmlParseUtils.getDocument(url);
            final List<DomainModel> models = XmlParseUtils.getNodes(document, "/meta-model/models/domain-model", DomainModel.class);
            final String version = XmlParseUtils.getNodeText(document, "/meta-model/version");
            metaModel.setModels(models);
            metaModel.setVersion(version);
        } catch (DocumentException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage(), "元数据解析异常");
        }
        return metaModel;
    }

    public Model getModelByDomainModel(DomainModel domainModel){
        final String resourcePath = domainModel.getResourcePath();
        final URL url = ModelReader.class.getResource(resourcePath);

        return domainModel;
    }

    public Model getModelById(String id) {
        final MetaModel metaModel = listMetaModel();
        final DomainModel domainModel = metaModel.getModels().stream()
                .filter(model -> StringUtils.equals(id, model.getId())).findFirst()
                .orElse(NullObject.getNullObject(DomainModel.class));
        return getModelByDomainModel(domainModel);
    }
}
