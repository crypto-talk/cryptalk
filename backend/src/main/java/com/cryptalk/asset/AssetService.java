package com.cryptalk.asset;

import com.cryptalk.coin.Coin;
import com.cryptalk.coin.CoinRepository;
import com.cryptalk.common.ApiException;
import com.cryptalk.market.MarketPriceService;
import com.cryptalk.member.Member;
import com.cryptalk.member.MemberRepository;
import com.cryptalk.wallet.WalletRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService {
    private final AssetSnapshotRepository snapshots;
    private final CoinRepository coins;
    private final MemberRepository members;
    private final WalletRepository wallets;
    private final EthereumBalanceClient ethereum;
    private final MarketPriceService marketPrices;

    public AssetService(AssetSnapshotRepository snapshots, CoinRepository coins, MemberRepository members,
                        WalletRepository wallets, EthereumBalanceClient ethereum, MarketPriceService marketPrices) {
        this.snapshots = snapshots; this.coins = coins; this.members = members; this.wallets = wallets;
        this.ethereum = ethereum; this.marketPrices = marketPrices;
    }

    @Transactional
    public List<AssetResponse> refreshAndList(Long memberId) {
        Member member = members.findById(memberId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        var wallet = wallets.findFirstByMemberId(memberId);
        if (wallet.isEmpty()) return List.of();
        String address = wallet.get().getAddress();
        Coin eth = coins.findBySymbolIgnoreCaseAndActiveTrue("ETH").orElseThrow();
        EthereumBalanceClient.BalanceResult balance = ethereum.balanceOf(address);
        AssetSnapshot snapshot = snapshots.findByMemberIdAndCoinId(memberId, eth.getId()).orElseGet(() -> new AssetSnapshot(member, eth));
        BigDecimal value = balance.quantity().signum() == 0 ? BigDecimal.ZERO
            : balance.quantity().multiply(marketPrices.currentPrice(eth, "KRW").price());
        snapshot.capture(balance.quantity(), value, "VERIFIED".equals(balance.status()) && balance.quantity().signum() > 0, balance.status());
        snapshots.save(snapshot);
        return snapshots.findByMemberIdOrderByCoinDisplayOrder(memberId).stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public AssetSnapshot snapshotForPost(Long memberId, Long coinId) {
        return snapshots.findByMemberIdAndCoinId(memberId, coinId).orElse(null);
    }

    private AssetResponse response(AssetSnapshot snapshot) {
        return new AssetResponse(snapshot.getCoin().getSymbol(), snapshot.getQuantity(), snapshot.getValueKrw(),
            snapshot.isVerified(), snapshot.getVerificationStatus(), snapshot.getCapturedAt());
    }

    public record AssetResponse(String symbol, BigDecimal quantity, BigDecimal valueKrw, boolean verified, String status, java.time.Instant capturedAt) {}
}
