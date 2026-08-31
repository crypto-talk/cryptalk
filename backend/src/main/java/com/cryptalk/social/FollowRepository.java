package com.cryptalk.social;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    long countByFollowerId(Long followerId);
    long countByFollowingId(Long followingId);
    List<Follow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);
    List<Follow> findByFollowingIdOrderByCreatedAtDesc(Long followingId);
}
