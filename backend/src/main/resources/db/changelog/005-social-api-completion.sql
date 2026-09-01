--liquibase formatted sql

--changeset cryptalk:005
ALTER TABLE coins ADD COLUMN market_price_id VARCHAR(100) NULL;
UPDATE coins SET market_price_id = 'bitcoin' WHERE symbol = 'BTC';
UPDATE coins SET market_price_id = 'ethereum' WHERE symbol = 'ETH';
UPDATE coins SET market_price_id = 'solana' WHERE symbol = 'SOL';
UPDATE coins SET market_price_id = 'ripple' WHERE symbol = 'XRP';
UPDATE coins SET market_price_id = 'dogecoin' WHERE symbol = 'DOGE';

ALTER TABLE posts ADD COLUMN asset_price_source VARCHAR(30) NULL;
UPDATE posts SET asset_price_source = 'CLIENT' WHERE asset_price IS NOT NULL;

ALTER TABLE comments ADD COLUMN updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

CREATE TABLE media_assets (
    file_name VARCHAR(60) NOT NULL,
    member_id BIGINT NOT NULL,
    post_id BIGINT NULL,
    media_type VARCHAR(20) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_media_assets PRIMARY KEY (file_name),
    CONSTRAINT fk_media_assets_member FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE,
    CONSTRAINT fk_media_assets_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE SET NULL
);
CREATE INDEX idx_media_assets_member ON media_assets (member_id, created_at DESC);
CREATE INDEX idx_media_assets_post ON media_assets (post_id);
