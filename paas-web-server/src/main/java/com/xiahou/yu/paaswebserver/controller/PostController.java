package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dto.input.CreatePostInput;
import com.xiahou.yu.paaswebserver.dto.input.UpdatePostInput;
import com.xiahou.yu.paaswebserver.entity.Post;
import com.xiahou.yu.paaswebserver.entity.User;
import com.xiahou.yu.paaswebserver.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @QueryMapping
    public List<Post> posts() {
        return postService.findAll();
    }

    @QueryMapping
    public Post post(@Argument Long id) {
        return postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @QueryMapping
    public List<Post> postsByUserId(@Argument Long userId) {
        return postService.findByAuthorId(userId);
    }

    @MutationMapping
    public Post createPost(@Argument CreatePostInput input) {
        return postService.createPost(input);
    }

    @MutationMapping
    public Post updatePost(@Argument Long id, @Argument UpdatePostInput input) {
        return postService.updatePost(id, input);
    }

    @MutationMapping
    public Boolean deletePost(@Argument Long id) {
        return postService.deletePost(id);
    }

    @SchemaMapping
    public String createdAt(Post post) {
        return post.getCreatedAt().toString();
    }

    @SchemaMapping
    public String updatedAt(Post post) {
        return post.getUpdatedAt().toString();
    }

    @SchemaMapping
    public List<Post> posts(User user) {
        return postService.findByAuthorId(user.getId());
    }
}
