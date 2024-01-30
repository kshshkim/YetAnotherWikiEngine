package dev.prvt.yawiki.auth.jwt.infra;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static dev.prvt.yawiki.common.util.test.Fixture.random;
import static dev.prvt.yawiki.fixture.JwtFixture.getJwtDecoder;
import static dev.prvt.yawiki.fixture.JwtFixture.getJwtEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

class AccessTokenGeneratorImplTest {

    AccessTokenGenerator accessTokenGenerator;

    String jwtIssuer = randString();
    int jwtLifespan = random().nextInt(180, 1800);

    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;

    @BeforeEach
    @SneakyThrows
    void init() {
        jwtIssuer = randString();
        jwtLifespan = random().nextInt(180, 1800);

        jwtEncoder = getJwtEncoder();
        jwtDecoder = getJwtDecoder();
        accessTokenGenerator = new AccessTokenGeneratorImpl(jwtEncoder, jwtLifespan, jwtIssuer);
    }

    @Test
    @SneakyThrows
    void generate() {
        // given
        UUID givenId = UUID.randomUUID();  // 임의 ID
        String givenName = randString();  // 임의 name

        // when
        String generated = accessTokenGenerator.generate(givenId, givenName);  // 토큰 생성

        // then

        // 토큰 decode
        Jwt decoded = jwtDecoder.decode(generated);
        Map<String, Object> claims = decoded.getClaims();
        String contributorId = (String) claims.get("contributorId");
        String name = (String) claims.get("name");
        String issuer = (String) claims.get("iss");
        String subject = decoded.getSubject();

        // 값 검증
        assertThat(contributorId)
            .describedAs("contributorId 필드가 적절히 입력되어야함")
            .isEqualTo(givenId.toString());

        assertThat(name)
            .describedAs("name 필드가 적절히 입력되어야함")
            .isEqualTo(givenName);

        assertThat(decoded.getExpiresAt().minusSeconds(jwtLifespan))
            .describedAs("lifespan 적절히 설정되어야함")
            .isNotNull()
            .isEqualTo(decoded.getIssuedAt());

        assertThat(issuer)
            .describedAs("issuer 필드가 적절히 설정되어야함")
            .isEqualTo(jwtIssuer);

        assertThat(subject)
            .describedAs("subject 필드가 적절히 설정되어야함")
            .isEqualTo(givenId.toString());
    }
}