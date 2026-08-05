package com.cryptalk.post;

import com.cryptalk.member.Member;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "post_likes")
public class PostLike {
    @EmbeddedId private PostLikeId id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("postId") @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("memberId") @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    protected PostLike() {}
    public PostLike(Post post, Member member) { this.id = new PostLikeId(post.getId(), member.getId()); this.post = post; this.member = member; this.createdAt = Instant.now(); }
}
