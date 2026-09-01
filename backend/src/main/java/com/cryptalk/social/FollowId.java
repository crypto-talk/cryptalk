package com.cryptalk.social;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record FollowId(Long followerId, Long followingId) implements Serializable {
    public FollowId() { this(null, null); }
}
