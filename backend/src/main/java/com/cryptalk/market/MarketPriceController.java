package com.cryptalk.market;

import com.cryptalk.coin.Coin;
import com.cryptalk.coin.CoinRepository;
import com.cryptalk.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market/prices")
public class MarketPriceController {
    private final CoinRepository coins;
    private final MarketPriceService prices;

    public MarketPriceController(CoinRepository coins, MarketPriceService prices) {
        this.coins = coins; this.prices = prices;
    }

    @GetMapping("/{symbol}")
    MarketPriceService.PriceQuote current(@PathVariable String symbol,
                                           @RequestParam(defaultValue = "USD") String currency) {
        Coin coin = coins.findBySymbolIgnoreCaseAndActiveTrue(symbol)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "코인 커뮤니티를 찾을 수 없습니다."));
        return prices.currentPrice(coin, currency);
    }
}
