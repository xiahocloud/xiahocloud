package com.xiahou.yu.paaswebserver.example;

import com.xiahou.yu.paasdomincore.design.filter.Filter;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paaswebserver.dto.input.DynamicCommandInput;
import com.xiahou.yu.paaswebserver.dto.input.DynamicQueryInput;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter 使用示例
 * 展示如何在命令和查询中统一使用 Filter，以及如何使用嵌套字段查询
 *
 * @author xiahou
 */
public class FilterUsageExample {

    /**
     * 示例1：基本 Filter 使用
     */
    public static void basicFilterUsage() {
        System.out.println("=== 基本 Filter 使用示例 ===");

        // 创建基本过滤条件
        Filter filter = Filter.empty()
            .eq("name_filter", "name", "John")
            .gt("age_filter", "age", 18)
            .contains("email_filter", "email", "@example.com");

        System.out.println("基本过滤器: " + filter);
        System.out.println("转换为Map: " + filter.toMap());
    }

    /**
     * 示例2：嵌套字段查询 (支持 a.b.c 格式)
     */
    public static void nestedFieldUsage() {
        System.out.println("\n=== 嵌套字段查询示例 ===");

        // 创建嵌套字段过滤条件
        Filter nestedFilter = Filter.empty()
            .nestedEq("user.profile.name", "Alice")           // 用户档案中的姓名
            .nestedGt("profile_age", "user.profile.age", 25)                // 用户档案中的年龄
            .nestedContains("user.address.city", "Beijing")   // 用户地址中的城市
            .nestedIn("user_roles", "user.roles", Arrays.asList("admin", "user")); // 用户角色

        System.out.println("嵌套字段过滤器: " + nestedFilter);

        // 从扁平化Map创建嵌套Filter
        Map<String, Object> flatMap = new HashMap<>();
        flatMap.put("user.profile.name", "Bob");
        flatMap.put("user.department.name", "IT");
        flatMap.put("order.customer.email", "customer@example.com");

        Filter fromFlatMap = Filter.fromFlatMap(flatMap);
        System.out.println("从扁平Map创建: " + fromFlatMap);
    }

    /**
     * 示例3：Filter 合并策略
     */
    public static void filterMergeUsage() {
        System.out.println("\n=== Filter 合并策略示例 ===");

        // 基础过滤器
        Filter baseFilter = Filter.empty()
            .eq("status_filter", "status", "active")
            .nestedEq("dept_filter", "user.department.name", "Engineering");

        // 权限过滤器
        Filter permissionFilter = Filter.empty()
            .nestedEq("role_filter", "user.roles", "admin")
            .nestedContains("perm_filter", "user.permissions", "read");

        // AND 合并
        Filter andMerged = baseFilter.merge(permissionFilter, Filter.MergeStrategy.AND);
        System.out.println("AND合并: " + andMerged);

        // OR 合并
        Filter orMerged = baseFilter.merge(permissionFilter, Filter.MergeStrategy.OR);
        System.out.println("OR合并: " + orMerged);

        // OVERRIDE 合并
        Filter overrideFilter = Filter.empty().eq("status_override", "status", "inactive");
        Filter overrideMerged = baseFilter.merge(overrideFilter, Filter.MergeStrategy.OVERRIDE);
        System.out.println("OVERRIDE合并: " + overrideMerged);
    }

    /**
     * 示例4：在 CommandContext 中使用 Filter
     */
    public static void commandContextUsage() {
        System.out.println("\n=== CommandContext 中使用 Filter 示例 ===");

        // 创建过滤条件
        Filter commandFilter = Filter.empty()
            .eq("id_filter", "id", "12345")
            .nestedEq("status_filter", "user.profile.status", "verified");

        // 在 CommandContext 中使用
        CommandContext context = CommandContext.builder()
            .entityName("User")
            .build();

        // 设置过滤器
        context.setFilter(commandFilter);

        System.out.println("CommandContext过滤器: " + context.getEffectiveFilter());

        // 从Map创建Filter（新的推荐方式）
        Map<String, Object> oldConditions = new HashMap<>();
        oldConditions.put("name", "test");
        oldConditions.put("user.email", "test@example.com");

        Filter filterFromMap = Filter.fromMap(oldConditions);
        context.setFilter(filterFromMap);
        System.out.println("从Map创建Filter后: " + context.getEffectiveFilter());
    }

    /**
     * 示例5：在 DynamicCommandInput 中使用 Filter
     */
    public static void dynamicCommandUsage() {
        System.out.println("\n=== DynamicCommandInput 中使用 Filter 示例 ===");

        // 创建命令输入
        DynamicCommandInput commandInput = new DynamicCommandInput();
        commandInput.setEntityName("Order");
        commandInput.setOperation("UPDATE");

        // 设置嵌套字段过滤条件
        Filter updateFilter = Filter.empty()
            .nestedEq("cust_filter", "customer.id", "cust123")
            .nestedContains("status_filter", "order.status", "pending")
            .nestedGt("amount_filter", "order.amount", 100.0);

        commandInput.setFilter(updateFilter);

        System.out.println("命令过滤器: " + commandInput.getEffectiveFilter());
        System.out.println("过滤器转Map格式: " + commandInput.getEffectiveFilter().toMap());
    }

    /**
     * 示例6：在 DynamicQueryInput 中使用 Filter
     */
    public static void dynamicQueryUsage() {
        System.out.println("\n=== DynamicQueryInput 中使用 Filter 示例 ===");

        // 创建查询输入
        DynamicQueryInput queryInput = new DynamicQueryInput();
        queryInput.setEntityName("Product");
        queryInput.setPage(1);
        queryInput.setSize(20);
        queryInput.setFields(Arrays.asList("id", "name", "category.name", "supplier.company"));

        // 复杂的嵌套查询条件
        Filter queryFilter = Filter.empty()
            .nestedContains("cat_filter", "category.name", "Electronics")
            .nestedEq("country_filter", "supplier.country", "China")
            .nestedGt("price_filter", "price.amount", 50.0)
            .nestedIn("tags_filter", "tags", Arrays.asList("new", "popular"))
            .nestedIsNotNull("qty_filter", "inventory.quantity");

        queryInput.setFilter(queryFilter);

        System.out.println("查询过滤器: " + queryInput.getEffectiveFilter());
        System.out.println("查询字段: " + queryInput.getFields());
    }

    /**
     * 示例7：复杂的业务场景
     */
    public static void complexBusinessScenario() {
        System.out.println("\n=== 复杂业务场景示例 ===");

        // 场景：查找符合条件的订单
        // 条件：用户是VIP，订单金额>1000，商品类别是电子产品，且供应商在北京

        Filter vipUserFilter = Filter.empty()
            .nestedEq("vip_filter", "user.level", "VIP")
            .nestedContains("tag_filter", "user.tags", "premium");

        Filter orderFilter = Filter.empty()
            .nestedGt("amount_filter", "order.totalAmount", 1000.0)
            .nestedEq("status_filter", "order.status", "confirmed");

        Filter productFilter = Filter.empty()
            .nestedContains("category_filter", "products.category.name", "Electronics")
            .nestedEq("city_filter", "products.supplier.city", "Beijing");

        // 合并所有条件 (AND 关系)
        Filter combinedFilter = vipUserFilter
            .merge(orderFilter, Filter.MergeStrategy.AND)
            .merge(productFilter, Filter.MergeStrategy.AND);

        System.out.println("复杂业务过滤器: " + combinedFilter);

        // 转换为扁平化Map供其他系统使用
        Map<String, Object> flatMap = combinedFilter.toFlatMap();
        System.out.println("扁平化结果: " + flatMap);
    }

    /**
     * 主方法，运行所有示例
     */
    public static void main(String[] args) {
        System.out.println("Filter 统一筛选系统使用示例");
        System.out.println("========================================");

        basicFilterUsage();
        nestedFieldUsage();
        filterMergeUsage();
        commandContextUsage();
        dynamicCommandUsage();
        dynamicQueryUsage();
        complexBusinessScenario();

        System.out.println("\n========================================");
        System.out.println("示例运行完成！");
    }
}

