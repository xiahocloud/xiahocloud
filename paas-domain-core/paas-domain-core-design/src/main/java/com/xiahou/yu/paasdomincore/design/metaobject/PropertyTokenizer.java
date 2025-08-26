package com.xiahou.yu.paasdomincore.design.metaobject;

/**
 * 属性标记化器
 * 用于解析属性路径，如 "user.name" 或 "list[0].value"
 *
 * @author xiahou
 */
public class PropertyTokenizer implements Iterable<PropertyTokenizer> {

    private String name;
    private final String indexedName;
    private String index;
    private final String children;

    public PropertyTokenizer(String fullname) {
        int delim = fullname.indexOf('.');
        if (delim > -1) {
            name = fullname.substring(0, delim);
            children = fullname.substring(delim + 1);
        } else {
            name = fullname;
            children = null;
        }
        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1) {
            index = name.substring(delim + 1, name.length() - 1);
            name = name.substring(0, delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getChildren() {
        return children;
    }

    public boolean hasNext() {
        return children != null;
    }

    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
    }

    @Override
    public java.util.Iterator<PropertyTokenizer> iterator() {
        return new java.util.Iterator<PropertyTokenizer>() {
            private PropertyTokenizer current = PropertyTokenizer.this;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public PropertyTokenizer next() {
                if (current == null) {
                    throw new java.util.NoSuchElementException();
                }
                PropertyTokenizer result = current;
                current = current.hasNext() ? current.next() : null;
                return result;
            }
        };
    }
}
