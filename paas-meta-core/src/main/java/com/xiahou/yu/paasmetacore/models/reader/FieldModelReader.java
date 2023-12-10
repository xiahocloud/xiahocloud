package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.MetaModel;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.SystemConstant;
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
        FieldModel fieldModel = new FieldModel();
        try {
            URL url = MetamodelReader.class.getResource(path);
            Document document = XmlParseUtils.getDocument(url);
            List<FieldModel.Field> fields = XmlParseUtils.getNodes(document, "/models/model/fields/field", FieldModel.Field.class);

            List<FieldModel.Field> fields = XmlParseUtils.getNodes(document, "/models/model/modelProps/modelProp", FieldModel.Field.class);

            fieldModel.setFields(fields);
        } catch (DocumentException e) {
            throw new PaaSException(ResultStatusEnum.SYSTEM_ERROR, e.getMessage(), "字段元数据解析异常");
        }
        return fieldModel;
    }
}
