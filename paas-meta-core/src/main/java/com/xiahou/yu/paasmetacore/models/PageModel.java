package com.xiahou.yu.paasmetacore.models;

/**
 * 页面模型，继承自AbstractModel
 */
public final class PageModel extends AbstractModel {

    public PageModel(String id, String name, String description, String importPath, String extendsModel) {
        super(id, name, description, importPath, extendsModel);
    }

    /**
     * 页面特有的方法
     */
    public boolean isPageType() {
        return true;
    }

    @Override
    public String getDisplayInfo() {
        return "页面模型: " + super.getDisplayInfo();
    }
}
