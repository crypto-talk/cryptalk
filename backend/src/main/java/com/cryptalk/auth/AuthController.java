package com.cryptalk.auth;

import com.cryptalk.auth.AuthDtos.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final String COOKIE = "cryptalk_refresh";
    private final AuthService authService;
    private final boolean secureCookie;

    public AuthController(AuthService authService, @Value("${cryptalk.auth.cookie-secure}") boolean secureCookie) {
        this.authService = authService; this.secureCookie = secureCookie;
    }

    @PostMapping("/signup")
    AuthResponse signup(@Valid @RequestBody SignupRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.signup(request);
        addCookie(response, result.refreshToken(), 14 * 86400);
        return result.response();
    }

    @PostMapping("/login")
    AuthResponse emailLogin(@Valid @RequestBody EmailLoginRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.emailLogin(request);
        addCookie(response, result.refreshToken(), 14 * 86400);
        return result.response();
    }

    @PostMapping("/refresh")
    AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.refresh(readCookie(request));
        addCookie(response, result.refreshToken(), 14 * 86400);
        return result.response();
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(readCookie(request)); addCookie(response, "", 0); return ResponseEntity.noContent().build();
    }

    private String readCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) if (COOKIE.equals(cookie.getName())) return cookie.getValue();
        return null;
    }

    private void addCookie(HttpServletResponse response, String value, int maxAge) {
        String sameSite = secureCookie ? "None" : "Lax";
        String cookie = COOKIE + "=" + value + "; Path=/api/v1/auth; HttpOnly; SameSite=" + sameSite + "; Max-Age=" + maxAge + (secureCookie ? "; Secure" : "");
        response.addHeader("Set-Cookie", cookie);
    }
}
