package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * TTL多线程上下文测试控制器
 * 演示TransmittableThreadLocal在异步执行中的上下文传递能力
 *
 * @author xiahou
 */
@RestController
@RequestMapping("/api/ttl")
@RequiredArgsConstructor
@Slf4j
public class TtlTestController {

    private final AsyncTestService asyncTestService;
    private final Executor asyncExecutor;

    /**
     * 测试异步方法中的上下文传递
     */
    @GetMapping("/async-test")
    public Map<String, Object> testAsyncContext() {
        String mainThreadTenantId = RequestContextHolder.getTenantId();
        String mainThreadUserId = RequestContextHolder.getUserId();
        String mainThreadRequestId = RequestContextHolder.getRequestId();

        log.info("Main thread context - tenantId: {}, userId: {}, requestId: {}",
                mainThreadTenantId, mainThreadUserId, mainThreadRequestId);

        Map<String, Object> result = new HashMap<>();
        result.put("mainThread", Map.of(
            "tenantId", mainThreadTenantId,
            "userId", mainThreadUserId,
            "requestId", mainThreadRequestId,
            "threadName", Thread.currentThread().getName()
        ));

        // 测试@Async注解的异步方法
        Map<String, Object> asyncResult = asyncTestService.testAsyncMethod();
        result.put("asyncMethod", asyncResult);

        // 测试CompletableFuture异步执行
        CompletableFuture<Map<String, Object>> futureResult = CompletableFuture.supplyAsync(() -> {
            String asyncTenantId = RequestContextHolder.getTenantId();
            String asyncUserId = RequestContextHolder.getUserId();
            String asyncRequestId = RequestContextHolder.getRequestId();

            log.info("CompletableFuture thread context - tenantId: {}, userId: {}, requestId: {}",
                    asyncTenantId, asyncUserId, asyncRequestId);

            return Map.of(
                "tenantId", asyncTenantId,
                "userId", asyncUserId,
                "requestId", asyncRequestId,
                "threadName", Thread.currentThread().getName()
            );
        }, asyncExecutor);

        try {
            result.put("completableFuture", futureResult.get());
        } catch (Exception e) {
            log.error("Error in CompletableFuture execution", e);
            result.put("completableFuture", Map.of("error", e.getMessage()));
        }

        return result;
    }

    /**
     * 测试嵌套异步调用
     */
    @GetMapping("/nested-async-test")
    public Map<String, Object> testNestedAsync() {
        String mainThreadContext = String.format("Main[%s-%s-%s]",
            RequestContextHolder.getTenantId(),
            RequestContextHolder.getUserId(),
            Thread.currentThread().getName());

        log.info("Starting nested async test: {}", mainThreadContext);

        return asyncTestService.testNestedAsyncCall(mainThreadContext);
    }
}

/**
 * 异步测试服务
 */
@Service
@Slf4j
class AsyncTestService {

    @Async("asyncExecutor")
    public Map<String, Object> testAsyncMethod() {
        String asyncTenantId = RequestContextHolder.getTenantId();
        String asyncUserId = RequestContextHolder.getUserId();
        String asyncRequestId = RequestContextHolder.getRequestId();

        log.info("@Async method context - tenantId: {}, userId: {}, requestId: {}",
                asyncTenantId, asyncUserId, asyncRequestId);

        // 模拟一些异步处理
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Map.of(
            "tenantId", asyncTenantId,
            "userId", asyncUserId,
            "requestId", asyncRequestId,
            "threadName", Thread.currentThread().getName(),
            "processingTime", "100ms"
        );
    }

    @Async("graphqlAsyncExecutor")
    public Map<String, Object> testNestedAsyncCall(String parentContext) {
        String nestedTenantId = RequestContextHolder.getTenantId();
        String nestedUserId = RequestContextHolder.getUserId();
        String nestedRequestId = RequestContextHolder.getRequestId();

        String nestedContext = String.format("Nested[%s-%s-%s]",
            nestedTenantId, nestedUserId, Thread.currentThread().getName());

        log.info("Nested async call - parent: {}, current: {}", parentContext, nestedContext);

        // 再次嵌套异步调用
        CompletableFuture<String> deepNested = CompletableFuture.supplyAsync(() -> {
            String deepTenantId = RequestContextHolder.getTenantId();
            String deepUserId = RequestContextHolder.getUserId();

            return String.format("Deep[%s-%s-%s]",
                deepTenantId, deepUserId, Thread.currentThread().getName());
        });

        try {
            String deepContext = deepNested.get();
            log.info("Deep nested result: {}", deepContext);

            return Map.of(
                "parent", parentContext,
                "nested", nestedContext,
                "deepNested", deepContext,
                "contextPreserved", nestedTenantId != null && nestedUserId != null
            );
        } catch (Exception e) {
            log.error("Error in nested async call", e);
            return Map.of("error", e.getMessage());
        }
    }
}
