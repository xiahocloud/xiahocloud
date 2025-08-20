package com.xiahou.yu.paaswebserver.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * TTL线程池配置类
 * 配置支持TransmittableThreadLocal的线程池
 *
 * @author xiahou
 */
@Configuration
@EnableAsync
@Slf4j
public class TtlThreadPoolConfig {

    /**
     * 配置异步任务执行器，支持TTL上下文传递
     */
    @Bean("asyncExecutor")
    @Primary
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 配置核心线程数
        executor.setCorePoolSize(8);
        // 配置最大线程数
        executor.setMaxPoolSize(16);
        // 配置队列大小
        executor.setQueueCapacity(100);
        // 配置线程名前缀
        executor.setThreadNamePrefix("async-ttl-");
        // 配置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);

        // 初始化线程池
        executor.initialize();

        // 使用TTL装饰器包装线程池，确保上下文传递
        return TtlExecutors.getTtlExecutor(executor.getThreadPoolExecutor());
    }

    /**
     * 配置GraphQL异步执行器
     */
    @Bean("graphqlAsyncExecutor")
    public Executor graphqlAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("graphql-ttl-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();

        // TTL装饰，支持上下文传递
        return TtlExecutors.getTtlExecutor(executor.getThreadPoolExecutor());
    }
}
