package com.cryptalk.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    long countByPostId(Long postId);
}
