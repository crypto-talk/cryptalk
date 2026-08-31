--liquibase formatted sql

--changeset cryptalk:004
ALTER TABLE posts ADD COLUMN tradingview_symbol VARCHAR(80) NULL;
ALTER TABLE posts ADD COLUMN tradingview_interval VARCHAR(10) NULL;
ALTER TABLE posts ADD COLUMN tradingview_analysis TEXT NULL;
ALTER TABLE posts ADD COLUMN asset_price DECIMAL(30, 8) NULL;
ALTER TABLE posts ADD COLUMN asset_price_currency VARCHAR(10) NULL;
ALTER TABLE posts ADD COLUMN asset_price_at TIMESTAMP(6) NULL;
ALTER TABLE posts ADD COLUMN youtube_url VARCHAR(500) NULL;
ALTER TABLE posts ADD COLUMN youtube_video_id VARCHAR(20) NULL;
ALTER TABLE posts ADD COLUMN youtube_thumbnail_url VARCHAR(500) NULL;

CREATE TABLE post_media (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    thumbnail_url VARCHAR(1000) NULL,
    display_order INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_post_media PRIMARY KEY (id),
    CONSTRAINT fk_post_media_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);
CREATE INDEX idx_post_media_post_order ON post_media (post_id, display_order);

CREATE TABLE post_bookmarks (
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_post_bookmarks PRIMARY KEY (post_id, member_id),
    CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_bookmarks_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);
CREATE INDEX idx_bookmarks_member_created ON post_bookmarks (member_id, created_at DESC);

CREATE TABLE post_reposts (
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_post_reposts PRIMARY KEY (post_id, member_id),
    CONSTRAINT fk_reposts_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_reposts_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);
CREATE INDEX idx_reposts_member_created ON post_reposts (member_id, created_at DESC);

CREATE TABLE member_follows (
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_member_follows PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES members (id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES members (id) ON DELETE CASCADE,
    CONSTRAINT chk_follows_not_self CHECK (follower_id <> following_id)
);
CREATE INDEX idx_follows_following ON member_follows (following_id, created_at DESC);
