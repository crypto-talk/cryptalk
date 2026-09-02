package com.cryptalk.wallet;

import com.cryptalk.auth.AuthDtos.MemberResponse;
import com.cryptalk.auth.AuthDtos.NonceRequest;
import com.cryptalk.auth.AuthDtos.NonceResponse;
import com.cryptalk.auth.AuthDtos.WalletLoginRequest;
import com.cryptalk.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/wallet")
@Tag(name = "지갑 연결", description = "로그인 계정의 EVM 지갑 소유권 인증 및 연결 API")
public class WalletController {
    private final AuthService authService;

    public WalletController(AuthService authService) { this.authService = authService; }

    @Operation(summary = "지갑 연결 nonce 발급", description = "지갑 서명에 사용할 일회성 메시지와 nonce 식별자를 발급합니다.")
    @PostMapping("/nonce")
    NonceResponse nonce(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody NonceRequest request) {
        return authService.createLinkNonce(Long.valueOf(jwt.getSubject()), request.walletAddress());
    }

    @Operation(summary = "지갑 연결", description = "서명을 검증해 EVM 지갑 소유권을 확인하고 로그인 계정에 연결합니다.")
    @PostMapping
    MemberResponse connect(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody WalletLoginRequest request) {
        return authService.connectWallet(Long.valueOf(jwt.getSubject()), request);
    }
}
