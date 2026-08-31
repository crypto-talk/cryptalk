package com.cryptalk.post;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, PostMemberId> {
    List<PostBookmark> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
