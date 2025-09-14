package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.cqrs.command.DynamicCommandHandler;
import com.xiahou.yu.paaswebserver.cqrs.query.DynamicQueryHandler;
import com.xiahou.yu.paaswebserver.dto.DynamicCommandResponse;
import com.xiahou.yu.paaswebserver.dto.DynamicQueryResponse;
import com.xiahou.yu.paaswebserver.dto.input.DynamicCommandInput;
import com.xiahou.yu.paaswebserver.dto.input.DynamicQueryInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;

/**
 * 动态GraphQL控制器
 * 基于CQRS模式处理动态查询和命令
 *
 * @author xiahou
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class DynamicGraphQLController {

    private final DynamicQueryHandler queryHandler;
    private final DynamicCommandHandler commandHandler;

    /**
     * 动态查询接口
     */
    @QueryMapping
    public DynamicQueryResponse dynamicQuery(@Argument("input") DynamicQueryInput input) {
        StopWatch stopWatch = new StopWatch("DynamicQuery");
        stopWatch.start("query-execution");

        log.info("Received dynamic query request: {}", input);

        try {
            DynamicQueryResponse response = queryHandler.handle(input);
            log.info("Dynamic query response: {}", response.toString());
            return response;
        } finally {
            stopWatch.stop();
            log.info("Dynamic query executed: {}", stopWatch.prettyPrint());
        }
    }

    /**
     * 动态命令接口
     */
    @MutationMapping
    public DynamicCommandResponse dynamicCommand(@Argument("input") DynamicCommandInput input) {
        StopWatch stopWatch = new StopWatch("DynamicCommand");
        stopWatch.start("command-execution");

        log.info("Received dynamic command request: {}", input);

        try {
            DynamicCommandResponse response = commandHandler.handle(input);
            log.info("Dynamic command response: {}", response.toString());
            return response;
        } finally {
            stopWatch.stop();
            log.info("Dynamic command executed: {}", stopWatch.prettyPrint());
        }
    }
}
