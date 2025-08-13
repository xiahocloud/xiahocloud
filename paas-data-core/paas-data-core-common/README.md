# paas-data-core-common

通用工具类模块，提供雪花算法ID生成器等通用功能。

## 功能特性

### 雪花算法ID生成器

- **唯一性**: 保证生成的ID在分布式环境下全局唯一
- **高性能**: 单机每秒可生成400万个ID
- **时间有序**: 生成的ID按时间递增
- **可配置**: 支持自定义机器ID和数据中心ID

## 使用方法

### 1. 配置参数

在 `application.yml` 中配置雪花算法参数：

```yaml
snowflake:
  worker-id: 1        # 工作机器ID (0-31)
  datacenter-id: 1    # 数据中心ID (0-31)
```

### 2. 注入使用

```java
@Service
public class UserService {
    
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    public User createUser(String name) {
        long userId = snowflakeIdGenerator.nextId();
        return new User(userId, name);
    }
}
```

### 3. 手动创建

```java
// 使用默认配置 (workerId=1, datacenterId=1)
SnowflakeIdGenerator generator = new SnowflakeIdGenerator();

// 使用自定义配置
SnowflakeIdGenerator generator = new SnowflakeIdGenerator(5L, 3L);

// 生成ID
long id = generator.nextId();
```

### 4. ID解析

```java
long id = generator.nextId();
SnowflakeIdInfo info = generator.parseId(id);

System.out.println("时间戳: " + info.getTimestamp());
System.out.println("数据中心ID: " + info.getDatacenterId());
System.out.println("工作机器ID: " + info.getWorkerId());
System.out.println("序列号: " + info.getSequence());
```

## ID结构

雪花算法生成的64位ID结构如下：

```
1位符号位 | 41位时间戳 | 5位数据中心ID | 5位机器ID | 12位序列号
0        | 时间戳      | 数据中心      | 机器     | 序列
```

- **符号位**: 固定为0，保证ID为正数
- **时间戳**: 41位，可使用约69年 (从2020-01-01开始)
- **数据中心ID**: 5位，支持32个数据中心
- **机器ID**: 5位，每个数据中心支持32台机器
- **序列号**: 12位，每毫秒可生成4096个ID

## 性能特点

- **高并发**: 线程安全，支持多线程并发生成
- **高性能**: 单机TPS可达400万+
- **低延迟**: 内存操作，微秒级延迟
- **无依赖**: 不依赖数据库或其他外部系统

## 注意事项

1. **时钟回拨**: 系统时钟不能回拨，否则会抛出异常
2. **机器ID唯一**: 同一数据中心内机器ID必须唯一
3. **数据中心ID唯一**: 不同数据中心的ID必须唯一
4. **配置范围**: workerId和datacenterId的取值范围都是0-31

## 测试

运行单元测试：

```bash
./gradlew :paas-data-core-common:test
```

测试涵盖：
- ID唯一性验证
- 并发安全性测试
- 配置参数验证
- ID解析功能测试
- 异常情况处理测试
