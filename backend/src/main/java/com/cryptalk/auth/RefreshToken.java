package com.cryptalk.auth;

import com.cryptalk.member.Member;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RefreshToken() {}
    public RefreshToken(Member member, String tokenHash, Instant expiresAt) {
        this.member = member; this.tokenHash = tokenHash; this.expiresAt = expiresAt; this.createdAt = Instant.now();
    }
    public Member getMember() { return member; }
    public boolean isUsable() { return revokedAt == null && expiresAt.isAfter(Instant.now()); }
    public void revoke() { revokedAt = Instant.now(); }
}
