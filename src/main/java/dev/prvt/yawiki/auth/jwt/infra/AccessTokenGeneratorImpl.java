package dev.prvt.yawiki.auth.jwt.infra;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.config.springsecurity.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessTokenGeneratorImpl implements AccessTokenGenerator {
    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;

    @Override
    public String generate(UUID id, String name) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getLifespan()))
                .subject(jwtProperties.getSubject())
                .claim("contributorId", id.toString())
                .claim("name", name)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
