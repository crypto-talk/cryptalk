package com.cryptalk.asset;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EthereumBalanceClient {
    private static final BigDecimal WEI = BigDecimal.TEN.pow(18);
    private final String rpcUrl;
    private final RestClient restClient;

    public EthereumBalanceClient(@Value("${cryptalk.asset.ethereum-rpc-url}") String rpcUrl, RestClient.Builder builder) {
        this.rpcUrl = rpcUrl; this.restClient = builder.build();
    }

    public BalanceResult balanceOf(String address) {
        if (rpcUrl == null || rpcUrl.isBlank()) return new BalanceResult(BigDecimal.ZERO, "UNAVAILABLE");
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post().uri(rpcUrl)
                .body(Map.of("jsonrpc", "2.0", "id", 1, "method", "eth_getBalance", "params", List.of(address, "latest")))
                .retrieve().body(Map.class);
            Object result = response == null ? null : response.get("result");
            if (!(result instanceof String hex)) return new BalanceResult(BigDecimal.ZERO, "RPC_ERROR");
            return new BalanceResult(new BigDecimal(new BigInteger(hex.substring(2), 16)).divide(WEI), "VERIFIED");
        } catch (Exception ignored) {
            return new BalanceResult(BigDecimal.ZERO, "RPC_ERROR");
        }
    }

    public record BalanceResult(BigDecimal quantity, String status) {}
}
