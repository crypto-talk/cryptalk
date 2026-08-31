package com.cryptalk.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepostRepository extends JpaRepository<PostRepost, PostMemberId> {
    long countByPostId(Long postId);
}
