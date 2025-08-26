package com.xiahou.yu.paasdomincore.design.filter;

import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import lombok.Data;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 通用过滤器类
 * 支持命令和查询统一使用的过滤条件
 *
 * @author xiahou
 */
@Data
@Builder
@Slf4j
public class Filter {

    /**
     * 条件连接类型
     */
    public enum ConjunctionType {
        AND("and"),
        OR("or"),
        ADVANCED("advanced");

        private final String value;

        ConjunctionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 右值类型枚举
     */
    public enum RightValueType {
        VALUE("value"),           //
        // 直接值
        FIELD("field"),           // 字段引用
        EXPRESSION("expression"), // 表达式
        CURRENT_USER("currentUser"), // 当前用户
        CURRENT_TIME("currentTime"); // 当前时间

        private final String value;

        RightValueType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 操作符枚举
     */
    public enum Operator {
        // 基本比较操作符
        EQ("eq"),           // 等于
        NE("ne"),           // 不等于
        GT("gt"),           // 大于
        GTE("gte"),         // 大于等于
        LT("lt"),           // 小于
        LTE("lte"),         // 小于等于

        // 集合操作符
        IN("in"),           // 包含于
        NOT_IN("nin"),      // 不包含于
        BETWEEN("bt"),      // 介于之间

        // 字符串操作符
        CONTAINS("ct"),     // 包含
        NOT_CONTAINS("nct"), // 不包含
        STARTS_WITH("sw"),  // 开始于
        ENDS_WITH("ew"),    // 结束于

        // 空值操作符
        IS_NULL("isNull"),  // 为空
        IS_NOT_NULL("exists"), // 不为空

        // 时间操作符
        BEFORE_DAYS("bfd"), // 早于N天前
        AFTER_DAYS("afd"),  // 晚于N天后
        WITHIN_DAYS("wd");  // N天内

        private final String value;

        Operator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Operator fromValue(String value) {
            for (Operator op : values()) {
                if (op.getValue().equals(value)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Unknown operator: " + value);
        }
    }

    /**
     * 过滤条件项
     */
    @Data
    @Builder
    public static class FilterItem {
        private String key;           // 条件唯一标识
        private String leftField;    // 左侧字段名（支持嵌套路径，如：user.profile.name）
        private RightValueType rightType; // 右值类型
        private Object rightValue;   // 右值
        private Operator operator;   // 操作符
        private String description;  // 条件描述

        /**
         * 创建简单条件
         */
        public static FilterItem simple(String key, String field, Operator operator, Object value) {
            return FilterItem.builder()
                    .key(key)
                    .leftField(field)
                    .operator(operator)
                    .rightType(RightValueType.VALUE)
                    .rightValue(value)
                    .build();
        }

        /**
         * 创建嵌套字段条件（支持 a.b.c 格式）
         */
        public static FilterItem nested(String key, String nestedField, Operator operator, Object value) {
            return FilterItem.builder()
                    .key(key)
                    .leftField(nestedField)
                    .operator(operator)
                    .rightType(RightValueType.VALUE)
                    .rightValue(value)
                    .description("Nested field condition: " + nestedField)
                    .build();
        }

        /**
         * 创建字段引用条件（支持嵌套字段引用）
         */
        public static FilterItem fieldRef(String key, String leftField, String rightField, Operator operator) {
            return FilterItem.builder()
                    .key(key)
                    .leftField(leftField)
                    .operator(operator)
                    .rightType(RightValueType.FIELD)
                    .rightValue(rightField)
                    .description("Field reference: " + leftField + " vs " + rightField)
                    .build();
        }

        /**
         * 解析嵌套字段路径
         * 例如: "user.profile.name" -> ["user", "profile", "name"]
         */
        public String[] parseFieldPath() {
            if (leftField == null) {
                return new String[0];
            }
            return leftField.split("\\.");
        }

        /**
         * 检查是否为嵌套字段
         */
        public boolean isNestedField() {
            return leftField != null && leftField.contains(".");
        }

        /**
         * 获取根字段名（嵌套字段的第一级）
         */
        public String getRootField() {
            if (leftField == null) {
                return null;
            }
            String[] parts = leftField.split("\\.");
            return parts[0];
        }

        /**
         * 获取嵌套路径（除根字段外的部分）
         */
        public String getNestedPath() {
            if (leftField == null || !leftField.contains(".")) {
                return null;
            }
            int firstDot = leftField.indexOf(".");
            return leftField.substring(firstDot + 1);
        }

        /**
         * 解析右值，支持嵌套字段解析
         */
        public Object resolveRightValue(DynamicDataObject context) {
            switch (rightType) {
                case VALUE:
                    return rightValue;
                case FIELD:
                    return context != null ? resolveNestedValue(context, rightValue.toString()) : null;
                case EXPRESSION:
                    return resolveExpression(rightValue.toString(), context);
                case CURRENT_USER:
                    return getCurrentUser();
                case CURRENT_TIME:
                    return System.currentTimeMillis();
                default:
                    return rightValue;
            }
        }

        /**
         * 解析嵌套字段值
         * 支持 a.b.c 格式的字段路径解析
         */
        private Object resolveNestedValue(DynamicDataObject context, String fieldPath) {
            if (context == null || fieldPath == null) {
                return null;
            }

            String[] parts = fieldPath.split("\\.");
            Object current = context;

            for (String part : parts) {
                if (current instanceof DynamicDataObject) {
                    current = ((DynamicDataObject) current).getValue(part);
                } else if (current instanceof Map) {
                    current = ((Map<?, ?>) current).get(part);
                } else {
                    // 如果当前对象不支持嵌套访问，返回null
                    return null;
                }

                if (current == null) {
                    return null;
                }
            }

            return current;
        }

        /**
         * 解析表达式
         */
        public Object resolveExpression(String expression, DynamicDataObject context) {
            // 简化的表达式解析
            switch (expression.toLowerCase()) {
                case "today":
                    return getTodayRange();
                case "yesterday":
                    return getYesterdayRange();
                case "thisweek":
                    return getThisWeekRange();
                case "thismonth":
                    return getThisMonthRange();
                default:
                    return expression;
            }
        }

        private String getCurrentUser() {
            // 从上下文获取当前用户
            // return RequestContextHolder.getUserId();
            return "current_user"; // 简化实现
        }

        private long[] getTodayRange() {
            // 返回今天的时间范围
            return new long[]{
                System.currentTimeMillis() - (System.currentTimeMillis() % 86400000),
                System.currentTimeMillis() + (86400000 - (System.currentTimeMillis() % 86400000))
            };
        }

        private long[] getYesterdayRange() {
            long today = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000);
            return new long[]{today - 86400000, today};
        }

        private long[] getThisWeekRange() {
            // 简化实现，返回本周范围
            return getTodayRange();
        }

        private long[] getThisMonthRange() {
            // 简化实现，返回本月范围
            return getTodayRange();
        }
    }

    // Filter 类的字段
    private String entity;                    // 实体名称
    private ConjunctionType conjunction;      // 连接类型
    private String expression;               // 高级表达式
    @Builder.Default
    private List<FilterItem> conditions = new ArrayList<>(); // 条件列表
    @Builder.Default
    private Map<String, FilterItem> conditionMap = new ConcurrentHashMap<>(); // 条件映射

    /**
     * 添加条件
     */
    public Filter addCondition(FilterItem item) {
        conditions.add(item);
        conditionMap.put(item.getKey(), item);
        return this;
    }

    /**
     * 添加简单条件
     */
    public Filter addCondition(String key, String field, Operator operator, Object value) {
        return addCondition(FilterItem.simple(key, field, operator, value));
    }

    /**
     * 添加等于条件
     */
    public Filter eq(String key, String field, Object value) {
        return addCondition(key, field, Operator.EQ, value);
    }

    /**
     * 添加不等于条件
     */
    public Filter ne(String key, String field, Object value) {
        return addCondition(key, field, Operator.NE, value);
    }

    /**
     * 添加大于条件
     */
    public Filter gt(String key, String field, Object value) {
        return addCondition(key, field, Operator.GT, value);
    }

    /**
     * 添加小于条件
     */
    public Filter lt(String key, String field, Object value) {
        return addCondition(key, field, Operator.LT, value);
    }

    /**
     * 添加包含条件
     */
    public Filter contains(String key, String field, Object value) {
        return addCondition(key, field, Operator.CONTAINS, value);
    }

    /**
     * 添加IN条件
     */
    public Filter in(String key, String field, Collection<?> values) {
        return addCondition(key, field, Operator.IN, values);
    }

    /**
     * 添加介于条件
     */
    public Filter between(String key, String field, Object start, Object end) {
        return addCondition(key, field, Operator.BETWEEN, Arrays.asList(start, end));
    }

    /**
     * 添加空值条件
     */
    public Filter isNull(String key, String field) {
        return addCondition(key, field, Operator.IS_NULL, null);
    }

    /**
     * 添加非空条件
     */
    public Filter isNotNull(String key, String field) {
        return addCondition(key, field, Operator.IS_NOT_NULL, null);
    }

    /**
     * 添加嵌套字段等于条件（支持 a.b.c 格式）
     */
    public Filter nestedEq(String key, String nestedField, Object value) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.EQ, value));
    }

    /**
     * 添加嵌套字段不等于条件
     */
    public Filter nestedNe(String key, String nestedField, Object value) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.NE, value));
    }

    /**
     * 添加嵌套字段大于条件
     */
    public Filter nestedGt(String key, String nestedField, Object value) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.GT, value));
    }

    /**
     * 添加嵌套字段小于条件
     */
    public Filter nestedLt(String key, String nestedField, Object value) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.LT, value));
    }

    /**
     * 添加嵌套字段包含条件
     */
    public Filter nestedContains(String key, String nestedField, Object value) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.CONTAINS, value));
    }

    /**
     * 添加嵌套字段IN条件
     */
    public Filter nestedIn(String key, String nestedField, Collection<?> values) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.IN, values));
    }

    /**
     * 添加嵌套字段空值条件
     */
    public Filter nestedIsNull(String key, String nestedField) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.IS_NULL, null));
    }

    /**
     * 添加嵌套字段非空条件
     */
    public Filter nestedIsNotNull(String key, String nestedField) {
        return addCondition(FilterItem.nested(key, nestedField, Operator.IS_NOT_NULL, null));
    }

    /**
     * 便捷方法：自动生成key的嵌套字段等于条件
     */
    public Filter nestedEq(String nestedField, Object value) {
        return nestedEq(UUID.randomUUID().toString(), nestedField, value);
    }

    /**
     * 便捷方法：自动生成key的嵌套字段不等于条件
     */
    public Filter nestedNe(String nestedField, Object value) {
        return nestedNe(UUID.randomUUID().toString(), nestedField, value);
    }

    /**
     * 便捷方法：自动生成key的嵌套字段包含条件
     */
    public Filter nestedContains(String nestedField, Object value) {
        return nestedContains(UUID.randomUUID().toString(), nestedField, value);
    }

    /**
     * 合并另一个过滤器（AND连接）
     */
    public Filter and(Filter other) {
        if (other == null || other.isEmpty()) {
            return this;
        }

        // 添加其他过滤器的条件
        for (FilterItem item : other.getConditions()) {
            addCondition(item);
        }

        // 设置连接类型
        this.conjunction = ConjunctionType.AND;

        return this;
    }

    /**
     * 合并另一个过滤器（OR连接）
     */
    public Filter or(Filter other) {
        if (other == null || other.isEmpty()) {
            return this;
        }

        // 如果当前过滤器为空，直接复制
        if (this.isEmpty()) {
            this.conditions = new ArrayList<>(other.getConditions());
            this.conditionMap = new ConcurrentHashMap<>(other.getConditionMap());
            this.conjunction = other.getConjunction();
            return this;
        }

        // 创建复合表达式
        this.conjunction = ConjunctionType.ADVANCED;
        this.expression = String.format("(%s) OR (%s)",
                                       buildSimpleExpression(),
                                       other.buildSimpleExpression());

        // 添加其他过滤器的条件
        for (FilterItem item : other.getConditions()) {
            addCondition(item);
        }

        return this;
    }

    /**
     * 合并策略枚举
     */
    public enum MergeStrategy {
        AND,        // AND连接
        OR,         // OR连接
        REPLACE,    // 替换（用新Filter替换当前Filter）
        OVERRIDE    // 覆盖（新Filter的条件覆盖当前Filter中相同key的条件）
    }

    /**
     * 合并两个过滤器
     *
     * @param other 要合并的过滤器
     * @param strategy 合并策略
     * @return 合并后的新过滤器
     */
    public Filter merge(Filter other, MergeStrategy strategy) {
        if (other == null) {
            return this.copy();
        }

        if (this.isEmpty()) {
            return other.copy();
        }

        if (other.isEmpty()) {
            return this.copy();
        }

        switch (strategy) {
            case AND:
                return mergeWithAnd(other);
            case OR:
                return mergeWithOr(other);
            case REPLACE:
                return other.copy();
            case OVERRIDE:
                return mergeWithOverride(other);
            default:
                return mergeWithAnd(other);
        }
    }

    /**
     * 使用AND策略合并
     */
    private Filter mergeWithAnd(Filter other) {
        Filter result = this.copy();

        // 添加other的所有条件
        for (FilterItem item : other.getConditions()) {
            // 避免重复key，如果key相同则生成新key
            String newKey = result.conditionMap.containsKey(item.getKey())
                ? item.getKey() + "_merged_" + UUID.randomUUID().toString().substring(0, 8)
                : item.getKey();

            FilterItem newItem = FilterItem.builder()
                .key(newKey)
                .leftField(item.getLeftField())
                .operator(item.getOperator())
                .rightType(item.getRightType())
                .rightValue(item.getRightValue())
                .description(item.getDescription())
                .build();

            result.addCondition(newItem);
        }

        result.conjunction = ConjunctionType.AND;
        return result;
    }

    /**
     * 使用OR策略合并
     */
    private Filter mergeWithOr(Filter other) {
        Filter result = this.copy();

        // 如果当前过滤器只有一个条件组，可以直接OR
        if (this.conjunction == ConjunctionType.AND && other.getConjunction() == ConjunctionType.AND) {
            // 添加other的所有条件
            for (FilterItem item : other.getConditions()) {
                String newKey = result.conditionMap.containsKey(item.getKey())
                    ? item.getKey() + "_merged_" + UUID.randomUUID().toString().substring(0, 8)
                    : item.getKey();

                FilterItem newItem = FilterItem.builder()
                    .key(newKey)
                    .leftField(item.getLeftField())
                    .operator(item.getOperator())
                    .rightType(item.getRightType())
                    .rightValue(item.getRightValue())
                    .description(item.getDescription())
                    .build();

                result.addCondition(newItem);
            }

            result.conjunction = ConjunctionType.OR;
        } else {
            // 复杂情况，使用高级表达式
            result.conjunction = ConjunctionType.ADVANCED;
            result.expression = String.format("(%s) OR (%s)",
                this.buildSimpleExpression(),
                other.buildSimpleExpression());

            // 添加所有条件
            for (FilterItem item : other.getConditions()) {
                if (!result.conditionMap.containsKey(item.getKey())) {
                    result.addCondition(item);
                }
            }
        }

        return result;
    }

    /**
     * 使用覆盖策略合并
     */
    private Filter mergeWithOverride(Filter other) {
        Filter result = this.copy();

        // 覆盖相同key的条件，添加新的条件
        for (FilterItem item : other.getConditions()) {
            // 如果key已存在，先移除旧的
            if (result.conditionMap.containsKey(item.getKey())) {
                result.conditions.removeIf(existing -> existing.getKey().equals(item.getKey()));
            }

            FilterItem newItem = FilterItem.builder()
                .key(item.getKey())
                .leftField(item.getLeftField())
                .operator(item.getOperator())
                .rightType(item.getRightType())
                .rightValue(item.getRightValue())
                .description(item.getDescription())
                .build();

            result.addCondition(newItem);
        }

        // 保持原有的连接类型，除非other有特殊要求
        if (other.getConjunction() != null) {
            result.conjunction = other.getConjunction();
        }

        return result;
    }

    /**
     * 默认使用AND策略合并
     */
    public Filter merge(Filter other) {
        return merge(other, MergeStrategy.AND);
    }

    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    /**
     * 转换为DynamicDataObject（用于传递给命令处理器）
     */
    public DynamicDataObject toConditions() {
        DynamicDataObject result = DynamicDataObject.empty();

        for (FilterItem item : conditions) {
            result.setValue(item.getLeftField(), item.resolveRightValue(null));
        }

        // 添加元信息
        result.setValue("_conjunction", conjunction != null ? conjunction.getValue() : "and");
        result.setValue("_expression", expression);
        result.setValue("_entity", entity);

        return result;
    }

    /**
     * 从DynamicDataObject创建Filter
     */
    public static Filter fromConditions(DynamicDataObject conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return Filter.builder().build();
        }

        Filter filter = Filter.builder()
                .entity(conditions.getString("_entity"))
                .expression(conditions.getString("_expression"))
                .build();

        // 解析连接类型
        String conjunctionStr = conditions.getString("_conjunction");
        if (conjunctionStr != null) {
            filter.conjunction = ConjunctionType.valueOf(conjunctionStr.toUpperCase());
        }

        // 解析条件（简化实现）
        for (String property : conditions.getPropertyNames()) {
            if (!property.startsWith("_")) {
                Object value = conditions.getValue(property);
                filter.addCondition(UUID.randomUUID().toString(), property, Operator.EQ, value);
            }
        }

        return filter;
    }

    /**
     * 从Map创建Filter
     */
    public static Filter fromMap(Map<String, Object> conditionsMap) {
        if (conditionsMap == null || conditionsMap.isEmpty()) {
            return Filter.builder().build();
        }

        return fromConditions(DynamicDataObject.fromMap(conditionsMap));
    }

    /**
     * 转换为Map（向后兼容）
     */
    public Map<String, Object> toMap() {
        return toConditions().toMap();
    }

    /**
     * 从扁平化的Map创建嵌套Filter
     * 支持 "user.profile.name" -> "John" 这样的格式
     */
    public static Filter fromFlatMap(Map<String, Object> flatMap) {
        if (flatMap == null || flatMap.isEmpty()) {
            return Filter.empty();
        }

        Filter filter = Filter.empty();

        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (!field.startsWith("_")) { // 跳过元信息字段
                String key = UUID.randomUUID().toString();
                if (field.contains(".")) {
                    // 嵌套字段
                    filter.nestedEq(key, field, value);
                } else {
                    // 普通字段
                    filter.eq(key, field, value);
                }
            }
        }

        return filter;
    }

    /**
     * 转换为扁平化Map（支持嵌套字段）
     */
    public Map<String, Object> toFlatMap() {
        Map<String, Object> result = new HashMap<>();

        for (FilterItem item : conditions) {
            result.put(item.getLeftField(), item.resolveRightValue(null));
        }

        // 添加元信息
        result.put("_conjunction", conjunction != null ? conjunction.getValue() : "and");
        result.put("_expression", expression);
        result.put("_entity", entity);

        return result;
    }

    /**
     * 构建简单表达式
     */
    private String buildSimpleExpression() {
        if (conditions.isEmpty()) {
            return "";
        }

        if (conjunction == ConjunctionType.OR) {
            return conditions.stream()
                    .map(FilterItem::getKey)
                    .collect(Collectors.joining(" OR "));
        } else {
            return conditions.stream()
                    .map(FilterItem::getKey)
                    .collect(Collectors.joining(" AND "));
        }
    }

    /**
     * 创建空过滤器
     */
    public static Filter empty() {
        return Filter.builder()
                .conjunction(ConjunctionType.AND)
                .build();
    }

    /**
     * 创建单条件过滤器
     */
    public static Filter single(String field, Operator operator, Object value) {
        return Filter.empty()
                .addCondition(UUID.randomUUID().toString(), field, operator, value);
    }

    /**
     * 验证过滤器
     */
    public void validate() {
        for (FilterItem item : conditions) {
            if (item.getLeftField() == null || item.getLeftField().trim().isEmpty()) {
                throw new IllegalArgumentException("Filter item field cannot be empty");
            }
            if (item.getOperator() == null) {
                throw new IllegalArgumentException("Filter item operator cannot be null");
            }
        }
    }

    /**
     * 克隆过滤器
     */
    public Filter copy() {
        Filter copy = Filter.builder()
                .entity(this.entity)
                .conjunction(this.conjunction)
                .expression(this.expression)
                .build();

        for (FilterItem item : this.conditions) {
            copy.addCondition(FilterItem.builder()
                    .key(item.getKey())
                    .leftField(item.getLeftField())
                    .operator(item.getOperator())
                    .rightType(item.getRightType())
                    .rightValue(item.getRightValue())
                    .description(item.getDescription())
                    .build());
        }

        return copy;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Filter{empty}";
        }

        StringBuilder sb = new StringBuilder("Filter{");
        sb.append("entity='").append(entity).append('\'');
        sb.append(", conjunction=").append(conjunction);
        sb.append(", conditions=[");

        String conditionsStr = conditions.stream()
            .map(item -> item.getLeftField() + " " + item.getOperator().getValue() + " " + item.getRightValue())
            .collect(Collectors.joining(", "));
        sb.append(conditionsStr);

        sb.append("]}");
        return sb.toString();
    }
}
