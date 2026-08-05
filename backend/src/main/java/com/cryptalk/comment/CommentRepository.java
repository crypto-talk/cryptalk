package com.cryptalk.comment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(Long postId);
    List<Comment> findByPostIdOrderByCreatedAt(Long postId);
}
