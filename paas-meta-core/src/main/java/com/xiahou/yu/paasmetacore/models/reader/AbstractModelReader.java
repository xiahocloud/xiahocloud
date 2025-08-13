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
        // 使用工厂方法创建AbstractModel实例，传入默认参数
        AbstractModel abstractModel = AbstractModel.create(
            "AbstractModel",
            "抽象模型",
            "从XML文件读取的抽象模型",
            path,
            null
        );

        try {
            URL url = AbstractModelReader.class.getResource(path);
            if (url == null) {
                throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "文件不存在", "无法找到路径: " + path);
            }

            Document document = XmlParseUtils.getDocument(url);
            List<Property> properties = XmlParseUtils.getNodes(document, "/models/model/properties/property", Property.class);

            // 将解析的属性添加到模型中
            properties.forEach(abstractModel::addProperty);

        } catch (DocumentException e) {
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, e.getMessage(), "抽象元数据解析异常", e);
        }
        return abstractModel;
    }
}
