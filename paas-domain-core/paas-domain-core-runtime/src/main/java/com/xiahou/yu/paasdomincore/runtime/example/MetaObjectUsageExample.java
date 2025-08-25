package com.xiahou.yu.paasdomincore.runtime.example;

import com.xiahou.yu.paasdomincore.runtime.dto.DynamicDataObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * MetaObject 使用示例
 * 展示如何使用 DynamicDataObject 替代 Map<String, Object>，让程序更优雅
 *
 * @author xiahou
 */
@Slf4j
public class MetaObjectUsageExample {

    public static void main(String[] args) {
        demonstrateMetaObjectUsage();
    }

    /**
     * 演示 MetaObject 和 DynamicDataObject 的优雅用法
     */
    public static void demonstrateMetaObjectUsage() {
        log.info("=== MetaObject 使用示例开始 ===");

        // 1. 传统的 Map<String, Object> 方式 - 不够优雅
        Map<String, Object> traditionalMap = new HashMap<>();
        traditionalMap.put("id", "1001");
        traditionalMap.put("name", "张三");
        traditionalMap.put("age", 28);
        traditionalMap.put("email", "zhangsan@example.com");

        // 嵌套对象
        Map<String, Object> profile = new HashMap<>();
        profile.put("department", "技术部");
        profile.put("position", "高级工程师");
        profile.put("salary", 15000);
        traditionalMap.put("profile", profile);

        log.info("传统方式 - 获取姓名: {}", traditionalMap.get("name"));
        log.info("传统方式 - 获取部门: {}", ((Map<String, Object>) traditionalMap.get("profile")).get("department"));

        // 2. 使用 DynamicDataObject 的优雅方式
        DynamicDataObject user = DynamicDataObject.empty()
                .setValue("id", "1001")
                .setValue("name", "张三")
                .setValue("age", 28)
                .setValue("email", "zhangsan@example.com")
                .setValue("profile.department", "技术部")  // 支持嵌套属性
                .setValue("profile.position", "高级工程师")
                .setValue("profile.salary", 15000);

        log.info("优雅方式 - 获取姓名: {}", user.getString("name"));
        log.info("优雅方式 - 获取年龄: {}", user.getInteger("age"));
        log.info("优雅方式 - 获取部门: {}", user.getString("profile.department")); // 直接支持嵌套属性
        log.info("优雅方式 - 获取薪资: {}", user.getInteger("profile.salary"));

        // 3. 类型安全的访问
        String name = user.getString("name");
        Integer age = user.getInteger("age");
        Boolean isActive = user.getBoolean("isActive"); // 即使不存在也不会报错，返回 null

        log.info("类型安全访问 - 姓名: {}, 年龄: {}, 激活状态: {}", name, age, isActive);

        // 4. 属性检查
        log.info("是否有 name 属性: {}", user.hasProperty("name"));
        log.info("是否有 phone 属性: {}", user.hasProperty("phone"));
        log.info("name 是否为空: {}", user.isNull("name"));
        log.info("phone 是否为空: {}", user.isNull("phone"));

        // 5. 动态添加属性
        user.setValue("phone", "13800138000")
            .setValue("address.city", "北京")
            .setValue("address.district", "朝阳区");

        log.info("动态添加后 - 电话: {}", user.getString("phone"));
        log.info("动态添加后 - 城市: {}", user.getString("address.city"));

        // 6. 获取所有属性
        String[] properties = user.getPropertyNames();
        log.info("所有属性: {}", String.join(", ", properties));

        // 7. 对象合并
        DynamicDataObject additionalInfo = DynamicDataObject.empty()
                .setValue("level", "P7")
                .setValue("joinDate", "2022-01-15");

        user.merge(additionalInfo);
        log.info("合并后 - 级别: {}", user.getString("level"));
        log.info("合并后 - 入职日期: {}", user.getString("joinDate"));

        // 8. 创建副本
        DynamicDataObject userCopy = user.copy();
        userCopy.setValue("name", "李四");
        log.info("原对象姓名: {}", user.getString("name"));
        log.info("副本对象姓名: {}", userCopy.getString("name"));

        // 9. 转换为 Map（用于 API 兼容性）
        Map<String, Object> resultMap = user.toMap();
        log.info("转换为Map的大小: {}", resultMap.size());

        // 10. 从任意对象创建 DynamicDataObject
        SampleUser sampleUser = new SampleUser("王五", 30, "wangwu@example.com");
        DynamicDataObject fromObject = DynamicDataObject.fromObject(sampleUser);
        log.info("从对象创建 - 姓名: {}", fromObject.getString("name"));
        log.info("从对象创建 - 年龄: {}", fromObject.getInteger("age"));

        log.info("=== MetaObject 使用示例结束 ===");
    }

    /**
     * 比较传统方式和优雅方式的代码示例
     */
    public static void compareTraditionalVsElegant() {
        log.info("\n=== 传统方式 vs 优雅方式对比 ===");

        // 传统方式：创建用户数据
        Map<String, Object> traditionalUser = new HashMap<>();
        traditionalUser.put("id", "1001");
        traditionalUser.put("name", "张三");

        Map<String, Object> traditionalProfile = new HashMap<>();
        traditionalProfile.put("department", "技术部");
        traditionalProfile.put("salary", 15000);
        traditionalUser.put("profile", traditionalProfile);

        // 传统方式：访问嵌套数据（容易出错）
        String traditionalDept = null;
        if (traditionalUser.get("profile") instanceof Map) {
            Map<String, Object> profileMap = (Map<String, Object>) traditionalUser.get("profile");
            if (profileMap.get("department") instanceof String) {
                traditionalDept = (String) profileMap.get("department");
            }
        }

        // 优雅方式：创建和访问数据
        DynamicDataObject elegantUser = DynamicDataObject.empty()
                .setValue("id", "1001")
                .setValue("name", "张三")
                .setValue("profile.department", "技术部")
                .setValue("profile.salary", 15000);

        String elegantDept = elegantUser.getString("profile.department"); // 简洁、安全

        log.info("传统方式获取部门: {}", traditionalDept);
        log.info("优雅方式获取部门: {}", elegantDept);

        // 数据验证和处理
        if (elegantUser.hasProperty("profile.salary") && elegantUser.isNotNull("profile.salary")) {
            Integer salary = elegantUser.getInteger("profile.salary");
            log.info("员工薪资: {}", salary);

            // 根据薪资等级调整
            if (salary >= 15000) {
                elegantUser.setValue("level", "高级");
            } else {
                elegantUser.setValue("level", "初级");
            }
        }

        log.info("员工等级: {}", elegantUser.getString("level"));
    }

    /**
     * 示例用户类
     */
    public static class SampleUser {
        private String name;
        private Integer age;
        private String email;

        public SampleUser(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        // Getters
        public String getName() { return name; }
        public Integer getAge() { return age; }
        public String getEmail() { return email; }

        // Setters
        public void setName(String name) { this.name = name; }
        public void setAge(Integer age) { this.age = age; }
        public void setEmail(String email) { this.email = email; }
    }
}
