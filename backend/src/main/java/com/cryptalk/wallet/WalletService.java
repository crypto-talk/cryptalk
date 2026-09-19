package com.cryptalk.wallet;

import com.cryptalk.common.ApiException;
import com.cryptalk.common.ErrorCode;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final WalletRepository wallets;
    private final WalletConnectionEventRepository events;

    public WalletService(WalletRepository wallets, WalletConnectionEventRepository events) {
        this.wallets = wallets; this.events = events;
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> list(Long memberId) {
        return wallets.findByMemberIdOrderByCreatedAtAsc(memberId).stream().map(this::response).toList();
    }

    @Transactional
    public void disconnect(Long memberId, Long walletId) {
        Wallet wallet = wallets.findById(walletId)
            .filter(value -> value.getMember().getId().equals(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.WALLET_NOT_FOUND));
        events.save(new WalletConnectionEvent(wallet.getMember(), wallet, WalletConnectionEvent.EventType.DISCONNECTED));
        wallets.delete(wallet);
    }

    private WalletResponse response(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getChainType(), wallet.getAddress(), wallet.getCreatedAt());
    }

    public record WalletResponse(Long id, String chainType, String address, Instant connectedAt) {}
}
