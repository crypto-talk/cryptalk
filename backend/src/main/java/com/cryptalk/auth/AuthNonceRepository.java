package com.cryptalk.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthNonceRepository extends JpaRepository<AuthNonce, UUID> {}
