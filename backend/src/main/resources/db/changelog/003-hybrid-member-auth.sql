--liquibase formatted sql

--changeset cryptalk:003
ALTER TABLE members ADD COLUMN email VARCHAR(254) NULL;
ALTER TABLE members ADD COLUMN password_hash VARCHAR(100) NULL;
ALTER TABLE members ADD CONSTRAINT uk_members_email UNIQUE (email);

ALTER TABLE auth_nonces ADD COLUMN purpose VARCHAR(20) NOT NULL DEFAULT 'LOGIN';
ALTER TABLE auth_nonces ADD COLUMN member_id BIGINT NULL;
ALTER TABLE auth_nonces ADD CONSTRAINT fk_auth_nonces_member FOREIGN KEY (member_id) REFERENCES members (id);
