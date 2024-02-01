package dev.prvt.yawiki.auth.jwt.infra;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;


public class AccessTokenGeneratorImpl implements AccessTokenGenerator {

    private final JwtEncoder jwtEncoder;
    private final int lifespan;
    private final String issuer;

    public AccessTokenGeneratorImpl(
        JwtEncoder jwtEncoder,
        int lifespan,
        String issuer
    ) {
        this.jwtEncoder = jwtEncoder;
        this.lifespan = lifespan;
        this.issuer = issuer;
    }

    @Override
    public String generate(UUID id, String name) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(lifespan))
                .subject(id.toString())
                .claim("contributorId", id.toString())
                .claim("name", name)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
