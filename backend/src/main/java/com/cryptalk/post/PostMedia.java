package com.cryptalk.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "post_media")
public class PostMedia {
    public enum MediaType { IMAGE, VIDEO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "post_id") private Post post;
    @Enumerated(EnumType.STRING) @Column(name = "media_type", nullable = false, length = 20) private MediaType mediaType;
    @Column(nullable = false, length = 1000) private String url;
    @Column(name = "thumbnail_url", length = 1000) private String thumbnailUrl;
    @Column(name = "display_order", nullable = false) private int displayOrder;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected PostMedia() {}
    public PostMedia(Post post, MediaType mediaType, String url, String thumbnailUrl, int displayOrder) {
        this.post = post; this.mediaType = mediaType; this.url = url; this.thumbnailUrl = thumbnailUrl;
        this.displayOrder = displayOrder; this.createdAt = Instant.now();
    }
    public Long getId() { return id; }
    public MediaType getMediaType() { return mediaType; }
    public String getUrl() { return url; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public int getDisplayOrder() { return displayOrder; }
}
