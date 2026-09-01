package com.cryptalk.post;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    List<PostMedia> findByPostIdOrderByDisplayOrder(Long postId);
    @Query("select media.url from PostMedia media where media.post.id = :postId")
    List<String> findUrlsByPostId(@Param("postId") Long postId);
    @Query("select media.thumbnailUrl from PostMedia media where media.post.id = :postId and media.thumbnailUrl is not null")
    List<String> findThumbnailUrlsByPostId(@Param("postId") Long postId);
    void deleteByPostId(Long postId);
}
