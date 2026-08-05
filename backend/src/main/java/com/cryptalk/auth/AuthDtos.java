package com.cryptalk.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() {}
    public record NonceRequest(@NotBlank @Pattern(regexp = "^0x[0-9a-fA-F]{40}$") String walletAddress) {}
    public record NonceResponse(UUID nonceId, String message, long expiresInSeconds) {}
    public record WalletLoginRequest(@NotBlank String walletAddress, UUID nonceId, @NotBlank String signature) {}
    public record AuthResponse(String accessToken, String tokenType, MemberResponse member) {}
    public record MemberResponse(Long id, String nickname, String avatarColor, String walletAddress, String assetVisibility) {}
}
