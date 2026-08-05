--liquibase formatted sql

--changeset cryptalk:001
CREATE TABLE members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(40) NOT NULL,
    avatar_color VARCHAR(20) NOT NULL,
    asset_visibility VARCHAR(20) NOT NULL DEFAULT 'EXACT',
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_members PRIMARY KEY (id),
    CONSTRAINT uk_members_nickname UNIQUE (nickname)
);

CREATE TABLE wallets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    chain_type VARCHAR(20) NOT NULL,
    address VARCHAR(80) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_wallets PRIMARY KEY (id),
    CONSTRAINT uk_wallets_chain_address UNIQUE (chain_type, address),
    CONSTRAINT fk_wallets_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE auth_nonces (
    id CHAR(36) NOT NULL,
    wallet_address VARCHAR(80) NOT NULL,
    nonce VARCHAR(100) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    used_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_auth_nonces PRIMARY KEY (id)
);

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE coins (
    id BIGINT NOT NULL AUTO_INCREMENT,
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(80) NOT NULL,
    chain_type VARCHAR(20) NOT NULL,
    contract_address VARCHAR(80) NULL,
    accent_color VARCHAR(20) NOT NULL,
    display_order INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_coins PRIMARY KEY (id),
    CONSTRAINT uk_coins_symbol UNIQUE (symbol)
);

CREATE TABLE asset_snapshots (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    coin_id BIGINT NOT NULL,
    quantity DECIMAL(36, 18) NOT NULL,
    value_krw DECIMAL(24, 2) NOT NULL,
    verified BOOLEAN NOT NULL,
    verification_status VARCHAR(30) NOT NULL,
    captured_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_asset_snapshots PRIMARY KEY (id),
    CONSTRAINT uk_asset_member_coin UNIQUE (member_id, coin_id),
    CONSTRAINT fk_assets_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_assets_coin FOREIGN KEY (coin_id) REFERENCES coins (id)
);

CREATE TABLE posts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    coin_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    content TEXT NOT NULL,
    author_asset_value_krw DECIMAL(24, 2) NULL,
    author_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id),
    CONSTRAINT fk_posts_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_posts_coin FOREIGN KEY (coin_id) REFERENCES coins (id)
);
CREATE INDEX idx_posts_coin_created ON posts (coin_id, created_at DESC);

CREATE TABLE post_likes (
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_post_likes PRIMARY KEY (post_id, member_id),
    CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE comments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_member FOREIGN KEY (member_id) REFERENCES members (id)
);
CREATE INDEX idx_comments_post_created ON comments (post_id, created_at);

--changeset cryptalk:002
INSERT INTO coins (symbol, name, chain_type, contract_address, accent_color, display_order, active) VALUES
('BTC', 'Bitcoin', 'BITCOIN', NULL, '#f7931a', 1, TRUE),
('ETH', 'Ethereum', 'EVM_NATIVE', NULL, '#627eea', 2, TRUE),
('SOL', 'Solana', 'SOLANA', NULL, '#14f195', 3, TRUE),
('XRP', 'XRP', 'XRPL', NULL, '#23292f', 4, TRUE),
('DOGE', 'Dogecoin', 'DOGECOIN', NULL, '#c2a633', 5, TRUE);
