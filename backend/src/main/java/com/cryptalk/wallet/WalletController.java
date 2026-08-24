package com.cryptalk.wallet;

import com.cryptalk.auth.AuthDtos.MemberResponse;
import com.cryptalk.auth.AuthDtos.NonceRequest;
import com.cryptalk.auth.AuthDtos.NonceResponse;
import com.cryptalk.auth.AuthDtos.WalletLoginRequest;
import com.cryptalk.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/wallet")
public class WalletController {
    private final AuthService authService;

    public WalletController(AuthService authService) { this.authService = authService; }

    @PostMapping("/nonce")
    NonceResponse nonce(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody NonceRequest request) {
        return authService.createLinkNonce(Long.valueOf(jwt.getSubject()), request.walletAddress());
    }

    @PostMapping
    MemberResponse connect(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody WalletLoginRequest request) {
        return authService.connectWallet(Long.valueOf(jwt.getSubject()), request);
    }
}
