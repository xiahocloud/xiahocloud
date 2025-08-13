package com.xiahou.yu.paaswebserver.service;

import com.xiahou.yu.paaswebserver.dto.input.CreatePostInput;
import com.xiahou.yu.paaswebserver.dto.input.UpdatePostInput;
import com.xiahou.yu.paaswebserver.entity.Post;
import com.xiahou.yu.paaswebserver.entity.User;
import com.xiahou.yu.paaswebserver.repository.PostRepository;
import com.xiahou.yu.paaswebserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> findByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public Post createPost(CreatePostInput input) {
        User author = userRepository.findById(input.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + input.getAuthorId()));

        Post post = Post.builder()
                .title(input.getTitle())
                .content(input.getContent())
                .author(author)
                .build();

        return postRepository.save(post);
    }

    public Post updatePost(Long id, UpdatePostInput input) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (input.getTitle() != null) {
            post.setTitle(input.getTitle());
        }
        if (input.getContent() != null) {
            post.setContent(input.getContent());
        }

        return postRepository.save(post);
    }

    public boolean deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
        return true;
    }
}
