package com.cryptalk.auth;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "auth_nonces")
public class AuthNonce {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    @Column(name = "wallet_address", nullable = false, length = 80)
    private String walletAddress;
    @Column(nullable = false, length = 100)
    private String nonce;
    @Column(nullable = false, length = 1000)
    private String message;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "used_at")
    private Instant usedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuthNonce() {}
    public AuthNonce(String walletAddress, String nonce, String message) {
        this.id = UUID.randomUUID(); this.walletAddress = walletAddress; this.nonce = nonce; this.message = message;
        this.createdAt = Instant.now(); this.expiresAt = createdAt.plusSeconds(300);
    }
    public UUID getId() { return id; }
    public String getWalletAddress() { return walletAddress; }
    public String getMessage() { return message; }
    public boolean isUsable() { return usedAt == null && expiresAt.isAfter(Instant.now()); }
    public void use() { usedAt = Instant.now(); }
}
