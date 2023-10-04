package dev.prvt.yawiki.auth.member.infra;

import dev.prvt.yawiki.auth.member.domain.AuthenticationTokenGenerator;
import dev.prvt.yawiki.auth.member.domain.BaseMember;
import dev.prvt.yawiki.config.springsecurity.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthenticationTokenGeneratorImpl implements AuthenticationTokenGenerator {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    @Override
    public String create(BaseMember member) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getLifespan()))
                .subject(jwtProperties.getSubject())
                .claim("contributorId", member.getId().toString())
                .claim("name", member.getDisplayedName())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
