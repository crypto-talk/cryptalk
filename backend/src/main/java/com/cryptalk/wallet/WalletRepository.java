package com.cryptalk.wallet;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByChainTypeAndAddress(String chainType, String address);
    Optional<Wallet> findFirstByMemberId(Long memberId);
}
