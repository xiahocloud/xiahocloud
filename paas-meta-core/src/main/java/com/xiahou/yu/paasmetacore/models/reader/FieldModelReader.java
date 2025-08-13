package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.models.FieldModel;
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
public class FieldModelReader {
    public static FieldModel readModel(String path) {
        // 使用正确的构造函数创建FieldModel实例
        FieldModel fieldModel = new FieldModel(
            "FieldModel",
            "字段模型",
            "从XML文件读取的字段模型",
            path,
            null
        );

        try {
            URL url = FieldModelReader.class.getResource(path);
            if (url == null) {
                throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, "文件不存在", "无法找到路径: " + path);
            }

            Document document = XmlParseUtils.getDocument(url);

            // 解析字段信息
            List<FieldModel.Component> components = XmlParseUtils.getNodes(document, "/Model/Components/Component", FieldModel.Component.class);

            // 将解析的组件添加到字段模型中
            components.forEach(fieldModel::addComponent);

        } catch (DocumentException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage(), "字段元数据解析异常");
        }
        return fieldModel;
    }
}
