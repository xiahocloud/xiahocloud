package com.xiahou.yu.paasmetacore.utils;

import com.google.common.collect.Lists;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiahou.yu.paasmetacore.constant.SystemConstant.META_MODEL_ELE_TYPE;

/**
 * description: xml 解析工具
 *
 * @author wanghaoxin
 * date     2022/7/14 23:25
 * @version 1.0
 */
@Slf4j
public class XmlParseUtils {

    public static Document getDocument(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }

    public static <T> List<T> getNodes(URL url, String path, Class<T> clazz) throws DocumentException {
        Document document = getDocument(url);
        return getNodes(document, path, clazz);
    }

    public static <T> List<T> getNodes(Document document, String path, Class<T> clazz) throws DocumentException {
        List<T> models = Lists.newArrayList();
        List<Node> list = document.selectNodes(path);
        list.forEach(node -> {
            T t;
            try {
                t = getNode(node, clazz);
            } catch (IllegalAccessException | InstantiationException e) {
                log.error(e.getMessage(), e);
                throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "XML节点解析失败", e);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "XML节点解析失败", e);
            }
            models.add(t);
        });
        return models;
    }

    public static <T> T getNode(Document document, String path, Class<T> clazz) throws DocumentException {
        T t;
        try {
            Node node = document.selectSingleNode(path);
            t = getNode(node, clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            log.error(e.getMessage(), e);
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "XML节点解析失败", e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new PaaSException(ResultStatusEnum.META_CORE_ERROR, "XML节点解析失败", e);
        }
        return t;
    }

    public static <T> T getNode(Node node, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
        T t = declaredConstructor.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        Field[] parentFields = clazz.getSuperclass().getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields).collect(Collectors.toList());
        fieldList.addAll(Arrays.asList(parentFields));
        for (Field field : fieldList) {
            field.setAccessible(true);
            String name = field.getName();
            Element element = (Element) node.selectSingleNode(name);
            if (element == null) {
                continue;
            }
            String val = element.getText();
            Object realTypeVal = val;
            switch (element.attributeValue(META_MODEL_ELE_TYPE)) {
                case "Boolean":
                    realTypeVal = Boolean.parseBoolean(val);
                    break;
                case null:
                    break;
                default:
                    break;
            }
            field.set(t, realTypeVal);
        }
        return t;
    }

    public static String getNodeText(Document document, String path) {
        Node node = document.selectSingleNode(path);
        return node.getText();
    }

}
