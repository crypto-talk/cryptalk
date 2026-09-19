package com.cryptalk.config;

import com.cryptalk.common.ErrorCode;
import com.cryptalk.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public SecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");
        ErrorCode code = authorization == null || authorization.isBlank()
            ? ErrorCode.AUTHENTICATION_REQUIRED
            : isExpired(exception) ? ErrorCode.TOKEN_EXPIRED : ErrorCode.INVALID_TOKEN;
        write(response, code);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        write(response, ErrorCode.ACCESS_DENIED);
    }

    private boolean isExpired(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof OAuth2AuthenticationException oauthException) {
                String description = oauthException.getError().getDescription();
                if (description != null && description.toLowerCase().contains("expired")) return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private void write(HttpServletResponse response, ErrorCode code) throws IOException {
        response.setStatus(code.status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), ErrorResponse.of(code));
    }
}
