package com.cryptalk.post;

import com.cryptalk.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "post_reposts")
public class PostRepost {
    @EmbeddedId private PostMemberId id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("postId") @JoinColumn(name = "post_id") private Post post;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("memberId") @JoinColumn(name = "member_id") private Member member;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected PostRepost() {}
    public PostRepost(Post post, Member member) {
        this.id = new PostMemberId(post.getId(), member.getId()); this.post = post; this.member = member; this.createdAt = Instant.now();
    }
}
