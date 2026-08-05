package com.cryptalk.post;

import com.cryptalk.coin.Coin;
import com.cryptalk.member.Member;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "coin_id")
    private Coin coin;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "author_asset_value_krw", precision = 24, scale = 2)
    private BigDecimal authorAssetValueKrw;
    @Column(name = "author_verified", nullable = false)
    private boolean authorVerified;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Post() {}
    public Post(Member member, Coin coin, String title, String content, BigDecimal assetValue, boolean verified) {
        this.member = member; this.coin = coin; this.title = title; this.content = content;
        this.authorAssetValueKrw = assetValue; this.authorVerified = verified; this.createdAt = Instant.now(); this.updatedAt = createdAt;
    }
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Coin getCoin() { return coin; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public BigDecimal getAuthorAssetValueKrw() { return authorAssetValueKrw; }
    public boolean isAuthorVerified() { return authorVerified; }
    public Instant getCreatedAt() { return createdAt; }
}
