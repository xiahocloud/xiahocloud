package com.xiahou.yu.paaswebserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import com.xiahou.yu.paaswebserver.service.UserService;
import com.xiahou.yu.paaswebserver.service.PostService;
import com.xiahou.yu.paaswebserver.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;

@GraphQlTest
class GraphQlIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @Test
    void shouldGetUsers() {
        User user = User.builder()
                .id(1L)
                .name("测试用户")
                .email("test@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.findAll()).thenReturn(Arrays.asList(user));

        this.graphQlTester
                .document("query { users { id name email age } }")
                .execute()
                .path("users")
                .entityList(User.class)
                .hasSize(1);
    }

    @Test
    void shouldGetUserById() {
        User user = User.builder()
                .id(1L)
                .name("测试用户")
                .email("test@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        this.graphQlTester
                .document("query { user(id: 1) { id name email age } }")
                .execute()
                .path("user.name")
                .entity(String.class)
                .isEqualTo("测试用户");
    }
}
