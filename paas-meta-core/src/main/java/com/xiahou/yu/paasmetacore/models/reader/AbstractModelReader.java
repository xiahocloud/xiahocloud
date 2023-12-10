package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.models.AbstractModel;
import com.xiahou.yu.paasmetacore.models.props.Property;
import com.xiahou.yu.paasmetacore.utils.XmlParseUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.net.URL;
import java.util.List;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/12/3 22:11
 * @version 1.0
 */
public class AbstractModelReader {
    public static AbstractModel readModel(String path) {
        AbstractModel abstractModel = new AbstractModel();
        abstractModel.setProperties(List.of());
        try {
            URL url = MetamodelReader.class.getResource(path);
            Document document = XmlParseUtils.getDocument(url);
            List<Property> properties = XmlParseUtils.getNodes(document, "/models/model/properties/property", Property.class);
            abstractModel.setProperties(properties);
        } catch (DocumentException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage(), "抽象元数据解析异常");
        }
        return abstractModel;
    }
}
