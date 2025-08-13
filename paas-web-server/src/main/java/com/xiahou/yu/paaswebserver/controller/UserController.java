package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dto.input.CreateUserInput;
import com.xiahou.yu.paaswebserver.dto.input.UpdateUserInput;
import com.xiahou.yu.paaswebserver.entity.User;
import com.xiahou.yu.paaswebserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @QueryMapping
    public List<User> users() {
        return userService.findAll();
    }

    @QueryMapping
    public User user(@Argument Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        return userService.createUser(input);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UpdateUserInput input) {
        return userService.updateUser(id, input);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        return userService.deleteUser(id);
    }

    @SchemaMapping
    public String createdAt(User user) {
        return user.getCreatedAt().toString();
    }

    @SchemaMapping
    public String updatedAt(User user) {
        return user.getUpdatedAt().toString();
    }
}
