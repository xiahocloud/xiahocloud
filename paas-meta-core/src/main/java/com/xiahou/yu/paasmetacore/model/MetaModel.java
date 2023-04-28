package com.xiahou.yu.paasmetacore.model;

import com.xiahou.yu.paasmetacore.annotation.AggregateRoot;
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
@AggregateRoot
public class MetaModel extends Model {
    private String version;
    private List<DomainModel> models;


    /*public DomainModel getDomainModel() {
        try {
            URL url = this.getClass().getResource(SysConstant.META_MODEL_ROOT_PATH);
            final Document document = XmlParseUtils.getDocument(url);
            final List<Model> nodes = XmlParseUtils.getNodes(document, "/domain-model/models/model", Model.class);
            final String version = XmlParseUtils.getNodeText(document, "/domain-model/version");
            this.setModels(nodes);
            this.setVersion(version);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public List<AbstractModel> listModelById(String modelId) {
        if (this.getModels() == null) {
            this.getDomainModel();
        }
        final String resourcePath = this.getModels().stream().filter(model -> StringUtils.endsWith(modelId, model.getId()))
                .map(Model::getResourcePath).findFirst().orElse(null);
        if (StringUtils.isEmpty(resourcePath)) {
            throw new MetaException();
        }
        return listByResourcePath(resourcePath);
    }

    public List<AbstractModel> listByResourcePath(String resourcePath) {
        List<AbstractModel> models = null;
        try {
            URL url = this.getClass().getResource(resourcePath);
            final Document document = XmlParseUtils.getDocument(url);
        } catch (DocumentException e) {
            throw new MetaException();
        }
        return models;
    }*/
}
