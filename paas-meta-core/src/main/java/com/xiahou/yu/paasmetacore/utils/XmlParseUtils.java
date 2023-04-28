package com.xiahou.yu.paasmetacore.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        final List<T> models = Lists.newArrayList();
        List<Node> list = document.selectNodes(path);
        list.forEach(node -> {
            final T t;
            try {
                t = getNode(node, clazz);
            } catch (IllegalAccessException | InstantiationException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            models.add(t);
        });
        return models;
    }

    public static <T> T getNode(Node node, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        final T t = clazz.newInstance();
        final Field[] fields = clazz.getDeclaredFields();
        final Field[] parentFields = clazz.getSuperclass().getDeclaredFields();
        final List<Field> fieldList = Arrays.stream(fields).collect(Collectors.toList());
        fieldList.addAll(Arrays.stream(parentFields).collect(Collectors.toList()));
        for (Field field : fieldList) {
            field.setAccessible(true);
            final String name = field.getName();
            final String val = node.selectSingleNode(name).getText();
            field.set(t, val);
        }
        return t;
    }

    public static String getNodeText(Document document, String path) {
        final Node node = document.selectSingleNode(path);
        return node.getText();
    }

}
