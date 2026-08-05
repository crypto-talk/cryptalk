package com.cryptalk.post;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record PostLikeId(Long postId, Long memberId) implements Serializable {
    public PostLikeId() { this(null, null); }
}
