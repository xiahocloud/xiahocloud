package com.xiahou.yu.paasmetacore.models.contextmodel;

import java.util.List;

/**
 * description 上下文接口
 *
 * @author wanghaoxin
 * date     2022/7/11 23:14
 * @version 1.0
 */
public interface Context {

    /**
     * <p>创建一个上下文模型</p>
     *
     * @param key 根据key 值
     * @return 返回一个Context
     */
    Context createContext(String key);

    /**
     * <p>返回所有上下文模型</p>
     *
     * @return 所有上下文
     */
    List<Context> listContexts();
}
