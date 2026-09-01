package com.cryptalk.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cryptalk.coin.Coin;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketPriceServiceTest {
    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/simple/price", exchange -> {
            byte[] response = "{\"ethereum\":{\"krw\":4321000.50,\"last_updated_at\":1788220800}}"
                .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() { server.stop(0); }

    @Test
    void fetchesProviderPriceAndTimestamp() {
        Coin coin = mock(Coin.class);
        when(coin.getMarketPriceId()).thenReturn("ethereum");
        when(coin.getSymbol()).thenReturn("ETH");
        MarketPriceService prices = new MarketPriceService("http://localhost:" + server.getAddress().getPort());

        MarketPriceService.PriceQuote quote = prices.currentPrice(coin, "krw");

        assertEquals("ETH", quote.symbol());
        assertEquals(0, new BigDecimal("4321000.50").compareTo(quote.price()));
        assertEquals("KRW", quote.currency());
        assertEquals(Instant.ofEpochSecond(1788220800), quote.capturedAt());
        assertEquals("COINGECKO", quote.source());
    }
}
