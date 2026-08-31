package com.cryptalk.social;

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
@Table(name = "member_follows")
public class Follow {
    @EmbeddedId private FollowId id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("followerId") @JoinColumn(name = "follower_id") private Member follower;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("followingId") @JoinColumn(name = "following_id") private Member following;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected Follow() {}
    public Follow(Member follower, Member following) {
        this.id = new FollowId(follower.getId(), following.getId()); this.follower = follower; this.following = following; this.createdAt = Instant.now();
    }
    public Member getFollower() { return follower; }
    public Member getFollowing() { return following; }
}
