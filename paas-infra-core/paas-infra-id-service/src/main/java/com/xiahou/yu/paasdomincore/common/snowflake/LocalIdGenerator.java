package com.xiahou.yu.paasdomincore.common.snowflake;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/22 15:12
 * @version 1.0
 */

public class LocalIdGenerator {
    // long 类型的16进制表示通常需要16个字符 (8字节 * 2字符/字节)
    private static final String LONG_HEX_FORMAT = "%016x";
    // int 类型的16进制表示通常需要8个字符 (4字节 * 2字符/字节)
    private static final String INT_HEX_FORMAT = "%08x";
    /**
     * 根据 tenantId 和当前时间戳生成一个固定长度为40个字符的16进制字符串ID。
     *
     * ID结构：
     * - `tenantId` (long): 格式化为16个字符的16进制字符串。
     * - `当前UTC毫秒时间戳` (long): 格式化为16个字符的16进制字符串。
     * - `一个小的随机数` (int): 格式化为8个字符的16进制字符串。
     *
     * @param tenantId 租户ID，通常是一个 long 类型的值。
     * @return 40个字符的16进制表示的唯一ID字符串。
     */
    public static String generateUniqueHexId(long tenantId) {
        // 1. 获取当前UTC毫秒时间戳
        long timestampMillis = Instant.now().toEpochMilli();
        // 2. 生成一个小的随机数，增加唯一性，防止同一毫秒内生成重复ID
        // 0-65535 (2^16-1) 范围的 int，实际占用的字节数会因平台而异，但作为 int 处理是4字节
        // 为了固定长度，我们这里仍确保它能被格式化为8个16进制字符。
        // 由于是0到65535，其最大值 FFFF (4个字符)，所以8个字符会填充很多0。
        // 如果想让随机数更有效率地使用8个字符空间，可以将其范围扩大到 Integer.MAX_VALUE。
        int randomSuffix = ThreadLocalRandom.current().nextInt(); // 使用完整的 int 范围
        // 3. 将这些部分格式化为固定长度的16进制字符串
        // "%016x" 表示将 long 转换为16进制，并用前导零填充到16位字符。
        // "%08x" 表示将 int 转换为16进制，并用前导零填充到8位字符。
        String tenantIdHex = String.format(LONG_HEX_FORMAT, tenantId);
        String timestampHex = String.format(LONG_HEX_FORMAT, timestampMillis);
        String randomSuffixHex = String.format(INT_HEX_FORMAT, randomSuffix);
        // 4. 拼接这些字符串
        return tenantIdHex + timestampHex + randomSuffixHex;
    }
    /**
     * 示例用法。
     */
    public static void main(String[] args) throws InterruptedException {
        long tenant1 = 1001L;
        long tenant2 = 200223456789L; // 演示一个更大的 tenantId
        System.out.println("Generating IDs for tenant " + tenant1 + ":");
        for (int i = 0; i < 3; i++) {
            String id = generateUniqueHexId(tenant1);
            System.out.println("ID " + (i + 1) + ": " + id + " (length: " + id.length() + ")");
            Thread.sleep(1); // 确保时间戳略有不同
        }
        System.out.println("\nGenerating IDs for tenant " + tenant2 + ":");
        for (int i = 0; i < 3; i++) {
            String id = generateUniqueHexId(tenant2);
            System.out.println("ID " + (i + 1) + ": " + id + " (length: " + id.length() + ")");
            Thread.sleep(1);
        }
        System.out.println("\n--- Testing Fixed Length ---");
        String testId = generateUniqueHexId(1L);
        System.out.println("ID for tenant 1L: " + testId + " (length: " + testId.length() + ")");
        System.out.println("Explanation of ID Structure (using tenantId=1, some_ts, some_rand_int):");
        // 假设 id = 0000000000000001 + 0000018EB321580A + 0A1B2C3D
        // tenantIdHex (16 chars):   0000000000000001 (representing 1L)
        // timestampHex (16 chars):  0000018EB321580A (representing current timestamp)
        // randomSuffixHex (8 chars): 0A1B2C3D (representing a random int)
        System.out.println("  1. First 16 chars: tenantId (padded)");
        System.out.println("  2. Middle 16 chars: timestamp (padded)");
        System.out.println("  3. Last 8 chars: random number (padded)");
    }
}
