package com.cryptalk.market;

import com.cryptalk.coin.Coin;
import com.cryptalk.common.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class MarketPriceService {
    private static final Duration CACHE_TTL = Duration.ofSeconds(20);
    private final RestClient client;
    private final ConcurrentMap<String, CachedQuote> cache = new ConcurrentHashMap<>();

    public MarketPriceService(@Value("${cryptalk.market-price.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory requests = new SimpleClientHttpRequestFactory();
        requests.setConnectTimeout(Duration.ofSeconds(3));
        requests.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(requests).build();
    }

    public PriceQuote currentPrice(Coin coin, String requestedCurrency) {
        String marketId = coin.getMarketPriceId();
        if (marketId == null || marketId.isBlank())
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "이 자산은 실시간 시세 조회를 지원하지 않습니다.");
        String currency = normalizeCurrency(requestedCurrency);
        String cacheKey = marketId + ":" + currency;
        CachedQuote cached = cache.get(cacheKey);
        Instant now = Instant.now();
        if (cached != null && cached.expiresAt().isAfter(now)) return cached.quote();

        try {
            JsonNode body = client.get().uri(uri -> uri.path("/simple/price")
                    .queryParam("ids", marketId)
                    .queryParam("vs_currencies", currency.toLowerCase(Locale.ROOT))
                    .queryParam("include_last_updated_at", true)
                    .build())
                .retrieve().body(JsonNode.class);
            JsonNode result = body == null ? null : body.get(marketId);
            JsonNode priceNode = result == null ? null : result.get(currency.toLowerCase(Locale.ROOT));
            if (priceNode == null || !priceNode.isNumber()) throw new RestClientException("price missing");
            JsonNode updatedNode = result.get("last_updated_at");
            Instant capturedAt = updatedNode != null && updatedNode.canConvertToLong()
                ? Instant.ofEpochSecond(updatedNode.asLong()) : now;
            PriceQuote quote = new PriceQuote(coin.getSymbol(), priceNode.decimalValue(), currency, capturedAt, "COINGECKO");
            cache.put(cacheKey, new CachedQuote(quote, now.plus(CACHE_TTL)));
            return quote;
        } catch (RestClientException exception) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "실시간 자산 가격을 조회하지 못했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    private String normalizeCurrency(String value) {
        String currency = value == null || value.isBlank() ? "USD" : value.trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("^[A-Z0-9]{2,10}$"))
            throw new ApiException(HttpStatus.BAD_REQUEST, "가격 통화 형식을 확인해 주세요.");
        return currency;
    }

    public record PriceQuote(String symbol, BigDecimal price, String currency, Instant capturedAt, String source) {}
    private record CachedQuote(PriceQuote quote, Instant expiresAt) {}
}
