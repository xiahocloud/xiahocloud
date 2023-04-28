package com.xiahou.yu.paasmetacore.utils;

/**
 * description: 空对象
 *
 * @author wanghaoxin
 * date     2022/9/6 10:05
 * @version 1.0
 */
public abstract class AbstractNull {
    protected transient boolean isNull;

    public boolean isEmpty() {
        return isNull;
    }
}
