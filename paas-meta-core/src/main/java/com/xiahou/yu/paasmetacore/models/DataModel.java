package com.xiahou.yu.paasmetacore.models;

/**
 * 数据模型，继承自AbstractModel
 */
public final class DataModel extends AbstractModel {

    public DataModel(String id, String name, String description, String importPath, String extendsModel) {
        super(id, name, description, importPath, extendsModel);
    }

    /**
     * 数据模型特有的方法
     */
    public boolean isPersistent() {
        return true;
    }

    @Override
    public String getDisplayInfo() {
        return "数据模型: " + super.getDisplayInfo();
    }
}
