package com.xiahou.yu.paasdomincore.design.command;

import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import com.xiahou.yu.paasdomincore.design.filter.Filter;
import com.xiahou.yu.paasinfracommon.context.RequestContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令上下文
 * 用于在命令执行过程中传递数据和状态
 *
 * @author xiahou
 */
@Data
@Builder
public class CommandContext {

    private RequestContext requestContext;

    /**
     * 实体标识
     */
    private String entityName;

    private String operation;

    /**
     * 数据载荷
     */
    private List<DynamicDataObject> records = new ArrayList<>();


    /**
     * 查询/更新/删除条件 - 使用统一的Filter
     * -- SETTER --
     * 设置过滤条件
     */
    private Filter filter;

    private Page page = new Page();

    private Sort sort = new Sort();

    /**
     * 扩展属性，用于插件传递自定义数据
     */
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 添加扩展属性
     */
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) this.attributes.get(key);
    }

    /**
     * 获取有效的过滤条件
     */
    public Filter getEffectiveFilter() {
        return filter != null ? filter : Filter.empty();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page {
        public static final Integer DEFAULT_PAGE_SIZE = 20;

        private Integer pageNum = 1;
        private Integer pageSize = DEFAULT_PAGE_SIZE;

        public void setPageNum(Integer pageNum) {
            if (pageNum == null || pageNum < 1) {
                this.pageNum = 1;
            } else {
                this.pageNum = pageNum;
            }
        }

        public void setPageSize(Integer pageSize) {
            if (pageSize == null || pageSize < 1) {
                this.pageSize = DEFAULT_PAGE_SIZE;
            } else {
                this.pageSize = pageSize;
            }
        }
    }

    @Data
    public static class Sort {
        private String field = "id";
        private SortEnum orderBy = SortEnum.ASC;

        private Map<String, SortEnum> sortMapping = new HashMap<>();

        public Map<String, SortEnum> getSortMapping() {
            if (sortMapping.isEmpty()) {
                if ("id".equals(this.field)) {
                    sortMapping.put(this.field, SortEnum.DESC);
                } else {
                    sortMapping.put(this.field, this.orderBy);
                }
            }
            return sortMapping;
        }
    }

    @Getter
    public enum SortEnum {
        ASC("asc"),
        DESC("desc");

        private final String value;

        SortEnum(String value) {
            this.value = value;
        }
    }
}

