package dev.prvt.yawiki.auth.jwt.infra;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static dev.prvt.yawiki.common.util.test.Fixture.random;
import static dev.prvt.yawiki.auth.jwt.JwtFixture.getJwtDecoder;
import static dev.prvt.yawiki.auth.jwt.JwtFixture.getJwtEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload.CustomClaim;
import java.time.Instant;
import java.util.List;
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

    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;

    @BeforeEach
    @SneakyThrows
    void init() {
        jwtEncoder = getJwtEncoder();
        jwtDecoder = getJwtDecoder();
        accessTokenGenerator = new AccessTokenGeneratorImpl(jwtEncoder);
    }

    @Test
    @SneakyThrows
    void generate() {
        // given
        UUID givenSubjectId = UUID.randomUUID();  // 임의 ID
        String givenIssuer = randString();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(1000);

        CustomClaim customClaim1 = new CustomClaim("claim-key-1", "claim-value-1");
        CustomClaim customClaim2 = new CustomClaim(randString(), randString());
        CustomClaim shouldBeOverwritten = new CustomClaim("iss", "not the issuer");

        TokenPayload givenPayload = new TokenPayload(
            givenIssuer,
            givenSubjectId.toString(),
            issuedAt,
            expiresAt,
            List.of(customClaim1, customClaim2, shouldBeOverwritten)
        );

        // when
        String generated = accessTokenGenerator.generate(givenPayload);  // 토큰 생성

        // then
        // 토큰 decode
        Jwt decoded = jwtDecoder.decode(generated);
        Map<String, Object> claims = decoded.getClaims();

        // 값 검증
        assertThat(decoded.getIssuedAt().getEpochSecond())
            .describedAs("발급일자가 적절히 설정되어야함")
            .isEqualTo(issuedAt.getEpochSecond());

        assertThat(decoded.getExpiresAt().getEpochSecond())
            .describedAs("만료일자가 적절히 설정되어야함")
            .isEqualTo(expiresAt.getEpochSecond());

        assertThat(claims.get(customClaim1.key()))
            .describedAs("claim 1 필드가 적절히 설정되어야함")
            .isEqualTo(customClaim1.value());

        assertThat(claims.get(customClaim2.key()))
            .describedAs("claim 2 필드가 적절히 설정되어야함")
            .isEqualTo(customClaim2.value());

        assertThat(claims.get("iss"))
            .describedAs("커스텀 페이로드에 iss를 키로 가지는 값이 있는 경우, 반영되어서는 안 됨.")
            .isNotEqualTo(shouldBeOverwritten.value())
            .describedAs("issuer 필드가 적절히 설정되어야함")
            .isEqualTo(givenPayload.issuer());

        assertThat(decoded.getSubject())
            .describedAs("subject 필드가 적절히 설정되어야함")
            .isEqualTo(givenPayload.subject());
    }
}