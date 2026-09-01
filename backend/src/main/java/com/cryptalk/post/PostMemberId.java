package com.cryptalk.post;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record PostMemberId(Long postId, Long memberId) implements Serializable {
    public PostMemberId() { this(null, null); }
}
