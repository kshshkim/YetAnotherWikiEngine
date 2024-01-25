package dev.prvt.yawiki.auth.jwt.infra;

import dev.prvt.yawiki.config.jwt.JwtProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static dev.prvt.yawiki.fixture.JwtFixture.getJwtDecoder;
import static dev.prvt.yawiki.fixture.JwtFixture.getJwtEncoder;
import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenGeneratorImplTest {
    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;
    JwtProperties jwtProperties;

    AccessTokenGeneratorImpl accessTokenGenerator;

    UUID givenId;
    String givenName;

    @SneakyThrows
    @BeforeEach
    void init() {
        Random random = new Random();

        jwtEncoder = getJwtEncoder();
        jwtDecoder = getJwtDecoder();
        jwtProperties = JwtProperties.builder()
                .issuer(randString())
                .subject(randString())
                .lifespan(random.nextInt(180, 1800))
                .build();
        accessTokenGenerator = new AccessTokenGeneratorImpl(jwtProperties, jwtEncoder);
        givenId = UUID.randomUUID();
        givenName = randString();
    }

    @Test
    void generate() {
        // when
        String s = accessTokenGenerator.generate(givenId, givenName);

        // then
        Jwt decoded = jwtDecoder.decode(s);
        Map<String, Object> claims = decoded.getClaims();
        String contributorId = (String) claims.get("contributorId");
        String name = (String) claims.get("name");
        String issuer = (String) claims.get("iss");
        String subject = decoded.getSubject();

        // yawiki authentication
        assertThat(contributorId)
                .isEqualTo(givenId.toString());
        assertThat(name)
                .isEqualTo(givenName);

        // properties
        assertThat(decoded.getExpiresAt().minusSeconds(jwtProperties.getLifespan()))
                .isNotNull()
                .isEqualTo(decoded.getIssuedAt());
        assertThat(issuer)
                .isEqualTo(jwtProperties.getIssuer());
        assertThat(subject)
                .isEqualTo(jwtProperties.getSubject());
    }
}