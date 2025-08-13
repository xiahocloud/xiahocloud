package com.xiahou.yu.paaswebserver.repository;

import com.xiahou.yu.paaswebserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByTitleContainingIgnoreCase(String title);
}
