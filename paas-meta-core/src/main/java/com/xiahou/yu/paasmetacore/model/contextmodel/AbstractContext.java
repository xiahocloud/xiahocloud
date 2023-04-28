package com.xiahou.yu.paasmetacore.model.contextmodel;

import com.xiahou.yu.paasmetacore.factory.ContextFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 上下文抽象类
 *
 * @author wanghaoxin
 * date     2022/7/11 23:16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class AbstractContext implements Context {

    private String id;
    private String key;
    private String name;

    @Override
    public AbstractContext createContext(String key) {
        AbstractContext context = null;
        try {
            Context buildContext = ContextFactory.build(key);
            if (buildContext instanceof AbstractContext) {
                context = (AbstractContext) buildContext;
            }
        } catch (Exception e) {
            log.error("生成上下文失败！  {}", e.getMessage());
        }
        return context;
    }

    /**
     * <p>返回所有上下文模型</p>
     *
     * @return 所有上下文
     */
    @Override
    public List<Context> listContexts() {
        List<Context> contexts = new ArrayList<>();

        return contexts;
    }
}
