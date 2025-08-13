package com.xiahou.yu.paaswebserver.config;

import com.xiahou.yu.paaswebserver.entity.Post;
import com.xiahou.yu.paaswebserver.entity.User;
import com.xiahou.yu.paaswebserver.repository.PostRepository;
import com.xiahou.yu.paaswebserver.repository.UserRepository;
import com.xiahou.yu.paaswebserver.dynamic.EntityDefinitionService;
import com.xiahou.yu.paaswebserver.dynamic.EntityDefinition;
import com.xiahou.yu.paaswebserver.dynamic.FieldDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EntityDefinitionService entityDefinitionService;

    @Override
    public void run(String... args) throws Exception {
        // 初始化动态实体定义
        initializeDynamicEntities();

        // 创建测试用户
        User user1 = User.builder()
                .name("张三")
                .email("zhangsan@example.com")
                .age(25)
                .build();

        User user2 = User.builder()
                .name("李四")
                .email("lisi@example.com")
                .age(30)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // 创建测试文章
        Post post1 = Post.builder()
                .title("Spring GraphQL 入门指南")
                .content("这是一篇关于Spring GraphQL的入门文章...")
                .author(user1)
                .build();

        Post post2 = Post.builder()
                .title("Java 19 新特性")
                .content("Java 19 带来了许多新的特性和改进...")
                .author(user1)
                .build();

        Post post3 = Post.builder()
                .title("微服务架构设计模式")
                .content("微服务架构是现代应用开发的重要模式...")
                .author(user2)
                .build();

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        System.out.println("数据初始化完成");
    }

    private void initializeDynamicEntities() {
        // 检查是否已经初始化过
        if (!entityDefinitionService.getAllEntityDefinitions().isEmpty()) {
            return;
        }

        // 创建Customer实体定义
        EntityDefinition customerEntity = entityDefinitionService.createEntityDefinition(
            "customer", "客户", "客户信息管理");

        entityDefinitionService.addFieldToEntity(customerEntity.getId(),
            "name", FieldDefinition.FieldType.STRING, true, null, "客户姓名", "客户的真实姓名");
        entityDefinitionService.addFieldToEntity(customerEntity.getId(),
            "email", FieldDefinition.FieldType.EMAIL, true, null, "邮箱", "客户的联系邮箱");
        entityDefinitionService.addFieldToEntity(customerEntity.getId(),
            "phone", FieldDefinition.FieldType.STRING, false, null, "电话", "客户的联系电话");
        entityDefinitionService.addFieldToEntity(customerEntity.getId(),
            "company", FieldDefinition.FieldType.STRING, false, null, "公司", "客户所在公司");
        entityDefinitionService.addFieldToEntity(customerEntity.getId(),
            "status", FieldDefinition.FieldType.STRING, true, "active", "状态", "客户状态");

        // 创建Product实体定义
        EntityDefinition productEntity = entityDefinitionService.createEntityDefinition(
            "product", "产品", "产品信息管理");

        entityDefinitionService.addFieldToEntity(productEntity.getId(),
            "name", FieldDefinition.FieldType.STRING, true, null, "产品名称", "产品的名称");
        entityDefinitionService.addFieldToEntity(productEntity.getId(),
            "description", FieldDefinition.FieldType.TEXT, false, null, "产品描述", "产品的详细描述");
        entityDefinitionService.addFieldToEntity(productEntity.getId(),
            "price", FieldDefinition.FieldType.DOUBLE, true, null, "价格", "产品价格");
        entityDefinitionService.addFieldToEntity(productEntity.getId(),
            "category", FieldDefinition.FieldType.STRING, true, null, "分类", "产品分类");
        entityDefinitionService.addFieldToEntity(productEntity.getId(),
            "inStock", FieldDefinition.FieldType.BOOLEAN, true, "true", "库存状态", "是否有库存");

        // 创建Order实体定义
        EntityDefinition orderEntity = entityDefinitionService.createEntityDefinition(
            "order", "订单", "订单信息管理");

        entityDefinitionService.addFieldToEntity(orderEntity.getId(),
            "orderNumber", FieldDefinition.FieldType.STRING, true, null, "订单号", "唯一订单编号");
        entityDefinitionService.addFieldToEntity(orderEntity.getId(),
            "customerId", FieldDefinition.FieldType.REFERENCE, true, null, "客户", "关联的客户");
        entityDefinitionService.addFieldToEntity(orderEntity.getId(),
            "totalAmount", FieldDefinition.FieldType.DOUBLE, true, null, "总金额", "订单总金额");
        entityDefinitionService.addFieldToEntity(orderEntity.getId(),
            "status", FieldDefinition.FieldType.STRING, true, "pending", "订单状态", "订单当前状态");
        entityDefinitionService.addFieldToEntity(orderEntity.getId(),
            "orderDate", FieldDefinition.FieldType.DATETIME, true, null, "下单时间", "订单创建时间");

        System.out.println("动态实体定义初始化完成");
    }
}
