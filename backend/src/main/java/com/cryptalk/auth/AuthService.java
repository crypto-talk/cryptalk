package com.cryptalk.auth;

import com.cryptalk.auth.AuthDtos.*;
import com.cryptalk.common.ApiException;
import com.cryptalk.common.ErrorCode;
import com.cryptalk.member.Member;
import com.cryptalk.member.MemberRepository;
import com.cryptalk.wallet.Wallet;
import com.cryptalk.wallet.WalletRepository;
import com.cryptalk.wallet.WalletConnectionEvent;
import com.cryptalk.wallet.WalletConnectionEventRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthNonceRepository nonces;
    private final RefreshTokenRepository refreshTokens;
    private final MemberRepository members;
    private final WalletRepository wallets;
    private final WalletSignatureVerifier verifier;
    private final WalletConnectionEventRepository walletEvents;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final long refreshTokenDays;
    private final SecureRandom random = new SecureRandom();

    public AuthService(AuthNonceRepository nonces, RefreshTokenRepository refreshTokens, MemberRepository members,
                       WalletRepository wallets, WalletSignatureVerifier verifier, JwtService jwtService,
                       WalletConnectionEventRepository walletEvents, PasswordEncoder passwordEncoder,
                       @Value("${cryptalk.jwt.refresh-token-days}") long refreshTokenDays) {
        this.nonces = nonces; this.refreshTokens = refreshTokens; this.members = members; this.wallets = wallets;
        this.verifier = verifier; this.jwtService = jwtService; this.walletEvents = walletEvents;
        this.passwordEncoder = passwordEncoder; this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public LoginResult signup(SignupRequest request) {
        String loginId = request.loginId().toLowerCase();
        String nickname = request.nickname().trim();
        if (members.findByLoginIdIgnoreCase(loginId).isPresent())
            throw new ApiException(ErrorCode.LOGIN_ID_TAKEN);
        if (members.findByNickname(nickname).isPresent())
            throw new ApiException(ErrorCode.NICKNAME_TAKEN);
        Member member = members.save(new Member(loginId, passwordEncoder.encode(request.password()), nickname, colorFor(loginId)));
        return issueTokens(member, null);
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        Member member = members.findByLoginIdIgnoreCase(request.loginId().trim())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));
        if (member.getPasswordHash() == null || !passwordEncoder.matches(request.password(), member.getPasswordHash()))
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        String address = wallets.findFirstByMemberId(member.getId()).map(Wallet::getAddress).orElse(null);
        return issueTokens(member, address);
    }

    @Transactional
    public NonceResponse createLinkNonce(Long memberId, String rawAddress) {
        members.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        return createNonce(rawAddress, "LINK", memberId);
    }

    private NonceResponse createNonce(String rawAddress, String purpose, Long memberId) {
        String address = rawAddress.toLowerCase();
        String nonce = UUID.randomUUID().toString();
        String action = "LINK".equals(purpose) ? "지갑 연결" : "로그인";
        String message = "CrypTalk " + action + "\n\n지갑 주소: " + address + "\n일회용 코드: " + nonce + "\n유효 시간: 5분";
        AuthNonce saved = nonces.save(new AuthNonce(address, purpose, memberId, nonce, message));
        return new NonceResponse(saved.getId(), message, 300);
    }

    @Transactional
    public MemberResponse connectWallet(Long memberId, WalletLoginRequest request) {
        String address = request.walletAddress().toLowerCase();
        verifyNonce(request, address, "LINK", memberId);
        Wallet wallet = wallets.findByChainTypeAndAddress("EVM", address).orElse(null);
        if (wallet != null && !wallet.getMember().getId().equals(memberId))
            throw new ApiException(ErrorCode.WALLET_ALREADY_LINKED);
        Member member = members.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        if (wallet == null) {
            wallet = wallets.save(new Wallet(member, address));
            walletEvents.save(new WalletConnectionEvent(member, wallet, WalletConnectionEvent.EventType.CONNECTED));
        }
        return profile(member, address);
    }

    @Transactional
    public LoginResult refresh(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        RefreshToken token = refreshTokens.findByTokenHash(hash(rawToken))
            .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_EXPIRED));
        if (!token.isUsable()) throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        token.revoke();
        String address = wallets.findFirstByMemberId(token.getMember().getId()).map(Wallet::getAddress).orElse(null);
        return issueTokens(token.getMember(), address);
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null) return;
        refreshTokens.findByTokenHash(hash(rawToken)).ifPresent(RefreshToken::revoke);
    }

    private void verifyNonce(WalletLoginRequest request, String address, String purpose, Long memberId) {
        AuthNonce nonce = nonces.findById(request.nonceId())
            .orElseThrow(() -> new ApiException(ErrorCode.WALLET_CHALLENGE_NOT_FOUND));
        boolean sameMember = memberId == null ? nonce.getMemberId() == null : memberId.equals(nonce.getMemberId());
        if (!nonce.isUsable() || !purpose.equals(nonce.getPurpose()) || !sameMember ||
            !nonce.getWalletAddress().equals(address) || !verifier.verify(address, nonce.getMessage(), request.signature()))
            throw new ApiException(ErrorCode.WALLET_VERIFICATION_FAILED);
        nonce.use();
    }

    private LoginResult issueTokens(Member member, String address) {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String rawRefresh = HexFormat.of().formatHex(bytes);
        refreshTokens.save(new RefreshToken(member, hash(rawRefresh), Instant.now().plusSeconds(refreshTokenDays * 86400)));
        return new LoginResult(new AuthResponse(jwtService.issue(member), "Bearer", profile(member, address)), rawRefresh);
    }

    private MemberResponse profile(Member member, String address) {
        return new MemberResponse(member.getId(), member.getNickname(), member.getAvatarColor(), address, member.getAssetVisibility().name());
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
