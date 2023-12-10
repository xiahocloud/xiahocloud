package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.MetaModel;
import com.xiahou.yu.paasmetacore.MetaModel.Model;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.SystemConstant;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.models.AbstractModel;
import com.xiahou.yu.paasmetacore.models.FieldModel;
import com.xiahou.yu.paasmetacore.models.MetamodelDefiner;
import com.xiahou.yu.paasmetacore.models.props.Property;
import com.xiahou.yu.paasmetacore.utils.NullObject;
import com.xiahou.yu.paasmetacore.utils.XmlParseUtils;
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
public class MetamodelReader {

    private static AbstractModelData abstractModelData;

    private record AbstractModelData(AbstractModel abstractModel) {
    }

    /**
     * <p> 列出所有的模型 </p>
     *
     * @return 所有模型
     */
    public static MetaModel listMetaModel() {
        MetaModel metaModel = new MetaModel();
        try {
            URL url = MetamodelReader.class.getResource(SystemConstant.META_MODEL_ROOT_PATH);
            Document document = XmlParseUtils.getDocument(url);
            List<Model> models = XmlParseUtils.getNodes(document, "/metaModel/models/model", Model.class);
            String version = XmlParseUtils.getNodeText(document, "/metaModel/version");
            List<MetaModel.Category> categories =
                    XmlParseUtils.getNodes(document, "/metaModel/categories/category", MetaModel.Category.class);
            metaModel.setModels(models);
            metaModel.setVersion(version);
            metaModel.setCategories(categories);

            for (Model model : models) {
                MetamodelDefiner metamodelDefiner = null;
                if (StringUtils.equals(model.getId(), "AbstractModel")) {
                    AbstractModel abstractModel = AbstractModelReader.readModel(model.getResourcePath());
                    abstractModelData = new AbstractModelData(abstractModel);
                } else if (StringUtils.equals(model.getId(), SystemConstant.FIELD_MODEL_NAME)) {
                    metamodelDefiner = FieldModelReader.readModel(model.getResourcePath());
                    if (metamodelDefiner instanceof FieldModel fieldModel) {
                        List<Property> properties = abstractModelData.abstractModel().getProperties();
                        if(fieldModel.getProperties() == null) {
                            fieldModel.setProperties(properties);
                        } else {
                            fieldModel.getProperties().addAll(properties);
                        }
                    }
                }
                model.setMetamodelDefiner(metamodelDefiner);
            }

        } catch (DocumentException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage(), "元数据解析异常");
        }
        return metaModel;
    }

    public Model getModelByDomainModel(Model model) {
        final String resourcePath = model.getResourcePath();
        final URL url = MetamodelReader.class.getResource(resourcePath);

        return model;
    }

    public Model getModelById(String id) {
        final MetaModel metaModel = listMetaModel();
        final Model domainModel = metaModel.getModels().stream()
                .filter(model -> StringUtils.equals(id, model.getId())).findFirst()
                .orElse(NullObject.getNullObject(Model.class));
        return getModelByDomainModel(domainModel);
    }
}
