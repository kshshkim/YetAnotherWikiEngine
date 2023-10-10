package dev.prvt.yawiki.fixture;

import dev.prvt.yawiki.config.jwt.JwtProperties;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static dev.prvt.yawiki.fixture.Fixture.randString;

public class SecurityFixture {

    static public WebAuthenticationDetails anWebAuthenticationDetails() {
        return new WebAuthenticationDetails(Fixture.aInetV4Address().getHostAddress(), UUID.randomUUID().toString());
    }

    static public AnonymousAuthenticationToken anAnonymousAuthenticationToken() {
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("key", "principal", List.of(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_ANONYMOUS";
            }
        }));
        anonymousAuthenticationToken.setDetails(anWebAuthenticationDetails());
        return anonymousAuthenticationToken;
    }

    @SneakyThrows
    static public Jwt aYawikiJwt(UUID contributorId, String name) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject("yawiki")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60*30))
                .claim("contributorId", contributorId.toString())
                .claim("name", name)
                .build();
        JwtEncoder jwtEncoder = JwtFixture.getJwtEncoder();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    static public Jwt aYawikiJwt() {
        return aYawikiJwt(UUID.randomUUID(), randString());
    }

    static public JwtAuthenticationToken aJwtAuthenticationToken(UUID contributorId, String name) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(aYawikiJwt(contributorId, name));
        authenticationToken.setDetails(anWebAuthenticationDetails());
        return authenticationToken;
    }

    static public JwtAuthenticationToken aJwtAuthenticationToken() {
        return aJwtAuthenticationToken(UUID.randomUUID(), randString());
    }

    static public JwtProperties.JwtPropertiesBuilder aJwtProperties() {
        return JwtProperties.builder()
                .lifespan(1800)
                .refreshTokenLifespan(180000)
                .issuer("self")
                .subject("yawiki");
    }
}
