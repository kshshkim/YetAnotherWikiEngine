package dev.prvt.yawiki.auth.jwt.infra;

import static org.springframework.security.oauth2.jwt.JwtEncoderParameters.from;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;


public class AccessTokenGeneratorImpl implements AccessTokenGenerator {

    private final JwtEncoder jwtEncoder;

    public AccessTokenGeneratorImpl(
        JwtEncoder jwtEncoder
    ) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generate(TokenPayload payload) {
        return jwtEncoder.encode(from(jwtClaimSet(payload)))
                   .getTokenValue();
    }

    private static JwtClaimsSet jwtClaimSet(TokenPayload payload) {
        return JwtClaimsSet.builder()
                   .issuer(payload.issuer())
                   .subject(payload.subject())
                   .issuedAt(payload.issuedAt())
                   .expiresAt(payload.expiresAt())
                   .claims(payload.putAllCustomClaimsToMap())
                   .build();
    }

}
