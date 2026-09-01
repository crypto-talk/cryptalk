package com.cryptalk.comment;

import com.cryptalk.member.Member;
import com.cryptalk.post.Post;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "post_id") private Post post;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "member_id") private Member member;
    @Column(nullable = false, length = 1000) private String content;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Comment() {}
    public Comment(Post post, Member member, String content) {
        this.post = post; this.member = member; this.content = content.trim(); this.createdAt = Instant.now(); this.updatedAt = createdAt;
    }
    public void update(String content) { this.content = content.trim(); this.updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public Post getPost() { return post; }
    public Member getMember() { return member; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
