package com.cryptalk.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public final class PostDtos {
    private PostDtos() {}
    public record CreatePostRequest(@NotBlank String coinSymbol, @NotBlank @Size(max=120) String title, @NotBlank @Size(max=5000) String content) {}
    public record AuthorResponse(Long id, String nickname, String avatarColor, String walletAddress) {}
    public record PostResponse(Long id, String coinSymbol, String title, String content, AuthorResponse author,
                               boolean verifiedHolder, BigDecimal assetValueKrw, String assetDisplay,
                               long likes, long comments, boolean liked, Instant createdAt) {}
}
