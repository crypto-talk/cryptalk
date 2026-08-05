package com.cryptalk.auth;

import com.cryptalk.auth.AuthDtos.*;
import com.cryptalk.common.ApiException;
import com.cryptalk.member.Member;
import com.cryptalk.member.MemberRepository;
import com.cryptalk.wallet.Wallet;
import com.cryptalk.wallet.WalletRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthNonceRepository nonces;
    private final RefreshTokenRepository refreshTokens;
    private final MemberRepository members;
    private final WalletRepository wallets;
    private final WalletSignatureVerifier verifier;
    private final JwtService jwtService;
    private final long refreshTokenDays;
    private final SecureRandom random = new SecureRandom();

    public AuthService(AuthNonceRepository nonces, RefreshTokenRepository refreshTokens, MemberRepository members,
                       WalletRepository wallets, WalletSignatureVerifier verifier, JwtService jwtService,
                       @Value("${cryptalk.jwt.refresh-token-days}") long refreshTokenDays) {
        this.nonces = nonces; this.refreshTokens = refreshTokens; this.members = members; this.wallets = wallets;
        this.verifier = verifier; this.jwtService = jwtService; this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public NonceResponse createNonce(String rawAddress) {
        String address = rawAddress.toLowerCase();
        String nonce = UUID.randomUUID().toString();
        String message = "CrypTalk 로그인\n\n지갑 주소: " + address + "\n일회용 코드: " + nonce + "\n유효 시간: 5분";
        AuthNonce saved = nonces.save(new AuthNonce(address, nonce, message));
        return new NonceResponse(saved.getId(), message, 300);
    }

    @Transactional
    public LoginResult login(WalletLoginRequest request) {
        String address = request.walletAddress().toLowerCase();
        AuthNonce nonce = nonces.findById(request.nonceId())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "로그인 요청을 찾을 수 없습니다."));
        if (!nonce.isUsable() || !nonce.getWalletAddress().equals(address) || !verifier.verify(address, nonce.getMessage(), request.signature())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "지갑 서명이 올바르지 않거나 만료되었습니다.");
        }
        nonce.use();
        Wallet wallet = wallets.findByChainTypeAndAddress("EVM", address).orElseGet(() -> createMemberWallet(address));
        return issueTokens(wallet.getMember(), wallet.getAddress());
    }

    @Transactional
    public LoginResult refresh(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다.");
        RefreshToken token = refreshTokens.findByTokenHash(hash(rawToken))
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다."));
        if (!token.isUsable()) throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다.");
        token.revoke();
        String address = wallets.findFirstByMemberId(token.getMember().getId()).map(Wallet::getAddress).orElse(null);
        return issueTokens(token.getMember(), address);
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null) return;
        refreshTokens.findByTokenHash(hash(rawToken)).ifPresent(RefreshToken::revoke);
    }

    private Wallet createMemberWallet(String address) {
        String suffix = address.substring(address.length() - 6);
        Member member = members.save(new Member("holder_" + suffix, colorFor(address)));
        return wallets.save(new Wallet(member, address));
    }

    private LoginResult issueTokens(Member member, String address) {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String rawRefresh = HexFormat.of().formatHex(bytes);
        refreshTokens.save(new RefreshToken(member, hash(rawRefresh), Instant.now().plusSeconds(refreshTokenDays * 86400)));
        MemberResponse profile = new MemberResponse(member.getId(), member.getNickname(), member.getAvatarColor(), address, member.getAssetVisibility().name());
        return new LoginResult(new AuthResponse(jwtService.issue(member), "Bearer", profile), rawRefresh);
    }

    private String colorFor(String value) {
        String[] colors = {"#7c3aed", "#2563eb", "#059669", "#db2777", "#ea580c"};
        return colors[Math.floorMod(value.hashCode(), colors.length)];
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) { throw new IllegalStateException(exception); }
    }

    public record LoginResult(AuthResponse response, String refreshToken) {}
}
