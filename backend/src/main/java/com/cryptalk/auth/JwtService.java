package com.cryptalk.auth;

import com.cryptalk.member.Member;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final long accessTokenMinutes;

    public JwtService(JwtEncoder encoder, @Value("${cryptalk.jwt.access-token-minutes}") long accessTokenMinutes) {
        this.encoder = encoder; this.accessTokenMinutes = accessTokenMinutes;
    }

    public String issue(Member member) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("cryptalk")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(accessTokenMinutes * 60))
            .subject(member.getId().toString())
            .claim("nickname", member.getNickname())
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
