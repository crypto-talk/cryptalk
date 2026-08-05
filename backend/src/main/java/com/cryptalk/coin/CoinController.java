package com.cryptalk.coin;

import com.cryptalk.common.ApiException;
import com.cryptalk.post.PostRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CoinController {
    private final CoinRepository coins; private final PostRepository posts;
    public CoinController(CoinRepository coins, PostRepository posts) { this.coins = coins; this.posts = posts; }
    @GetMapping("/coins")
    List<CoinResponse> coins() { return coins.findByActiveTrueOrderByDisplayOrder().stream().map(this::response).toList(); }
    @GetMapping("/communities/{symbol}")
    CommunityResponse community(@PathVariable String symbol) {
        Coin coin = coins.findBySymbolIgnoreCaseAndActiveTrue(symbol).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다."));
        long postCount = posts.findByCoinSymbolIgnoreCaseOrderByCreatedAtDesc(symbol, PageRequest.of(0, 100)).size();
        return new CommunityResponse(response(coin), postCount, coin.getName() + " 홀더와 투자자가 정보를 나누는 공간입니다.");
    }
    private CoinResponse response(Coin coin) { return new CoinResponse(coin.getId(), coin.getSymbol(), coin.getName(), coin.getChainType(), coin.getAccentColor()); }
    public record CoinResponse(Long id, String symbol, String name, String chainType, String accentColor) {}
    public record CommunityResponse(CoinResponse coin, long postCount, String description) {}
}
