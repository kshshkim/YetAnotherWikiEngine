package dev.prvt.yawiki.auth.authprocessor.application;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.nimbusds.jose.jwk.JWKSet;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenException;
import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import dev.prvt.yawiki.member.application.MemberPasswordVerificationData;
import dev.prvt.yawiki.member.application.MemberService;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.ResourceUtils;

@SpringBootTest
public class AuthProcessorServiceIntegrationTest {

    @Value("${yawiki.jwt.auth.access-token.lifespan}")
    int lifespan;

    @Value("${yawiki.jwt.auth.access-token.issuer}")
    String issuer;

    @Autowired
    AuthProcessorService authProcessorService;

    @MockBean
    MemberService memberService;

    @MockBean(name = "tokenPayloadLoaderCurrentTimeProvider")
    CurrentTimeProvider currentTimeProvider;

    JwtDecoder jwtDecoder = getDecoder();
    @SneakyThrows
    JwtDecoder getDecoder() {
        JWKSet jwkSet = JWKSet.load(ResourceUtils.getFile("classpath:test_public_key.json"));
        RSAPublicKey rsaPublicKey = jwkSet.getKeyByKeyId("test-key").toRSAKey().toRSAPublicKey();
        return NimbusJwtDecoder
                   .withPublicKey(rsaPublicKey)
                   .build();
    }

    private UUID givenId;
    private String givenUsername;
    private String givenPassword;
    private Instant issuedAt;

    @BeforeEach
    void init() {
        givenId = UUID.randomUUID();
        givenUsername = randString();
        givenPassword = randString(12);
        issuedAt = Instant.now();
    }

    void verifyAccessToken(String accessToken, Instant issuedAt) {
        Jwt decoded = jwtDecoder.decode(accessToken);

        assertThat(decoded.getIssuedAt().getEpochSecond())
            .describedAs("발급 일자가 제대로 기입되어야함")
            .isEqualTo(issuedAt.getEpochSecond());

        assertThat(decoded.getExpiresAt().getEpochSecond())
            .describedAs("만료 일자가 제대로 기입되어야함")
            .isEqualTo(issuedAt.plusSeconds(lifespan).getEpochSecond());

        assertThat(decoded.getSubject())
            .describedAs("subject 제대로 기입되어야함")
            .isEqualTo(givenId.toString());

        assertThat(decoded.getClaims().get("iss"))
            .describedAs("issuer 제대로 설정되어야함")
            .isEqualTo(issuer);
    }

    @Test
    @DisplayName("패스워드 auth 테스트")
    void usernamePasswordAuth() {
        // given
        // 발급 요청 시간 고정
        given(currentTimeProvider.getCurrentInstant())
            .willReturn(issuedAt);

        // 회원 서비스 모킹
        given(memberService.verifyPassword(eq(new MemberPasswordVerificationData(givenUsername, givenPassword))))
            .willReturn(givenId);

        // when
        AuthToken authToken = authProcessorService.usernamePasswordAuth(givenUsername, givenPassword);
        String accessToken = authToken.accessToken();

        // then
        verifyAccessToken(accessToken, issuedAt);
    }
    
    @Test
    @DisplayName("refresh token 인증 테스트")
    void refreshTokenAuth() {
        // given
        // 발급 요청 시간 고정
        given(currentTimeProvider.getCurrentInstant())
            .willReturn(issuedAt);

        // 회원 서비스 모킹
        given(memberService.verifyPassword(eq(new MemberPasswordVerificationData(givenUsername, givenPassword))))
            .willReturn(givenId);

        // 테스트에 사용할 Auth Token
        AuthToken givenAuthToken = authProcessorService.usernamePasswordAuth(givenUsername, givenPassword);

        // 갱신 요청 시간 고정
        Instant renewedAt = issuedAt.plusSeconds(2000);
        given(currentTimeProvider.getCurrentInstant())
            .willReturn(renewedAt);

        // when
        AuthToken authToken = authProcessorService.refreshTokenAuth(givenUsername, givenAuthToken.refreshToken());

        // then
        verifyAccessToken(authToken.accessToken(), renewedAt);

        assertThat(authToken.accessToken())
            .describedAs("액세스 토큰이 재발급 되어야하며, 기존 토큰 값과 달라야함")
            .isNotEqualTo(givenAuthToken.accessToken());

        assertThatThrownBy(() -> authProcessorService.refreshTokenAuth(givenUsername, givenAuthToken.refreshToken()))
            .describedAs("리프레시 토큰은 일회용이며, 한 번 갱신된 경우 값이 바뀌어야함 (재요청시 토큰을 찾을 수 없어야함)")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("not found");

    }

    @Test
    @DisplayName("refresh token 인증시, 토큰이 만료된 경우 예외를 반환해야함.")
    void refreshTokenAuth_expired() {
        // given
        // 발급 요청 시간 고정
        given(currentTimeProvider.getCurrentInstant())
            .willReturn(issuedAt);
        
        // 회원 서비스 모킹
        given(memberService.verifyPassword(eq(new MemberPasswordVerificationData(givenUsername, givenPassword))))
            .willReturn(givenId);
        
        // 테스트에 사용할 Auth Token
        AuthToken givenAuthToken = authProcessorService.usernamePasswordAuth(givenUsername, givenPassword);

        // 갱신 요청 시간 고정
        Instant renewedAt = issuedAt.plusSeconds(2000);
        given(currentTimeProvider.getCurrentInstant())
            .willReturn(renewedAt);

        // when
        AuthToken authToken = authProcessorService.refreshTokenAuth(givenUsername,
            givenAuthToken.refreshToken());

        // then
        verifyAccessToken(authToken.accessToken(), renewedAt);
        assertThat(authToken.accessToken())
            .describedAs("access token 재발급 되어야함.")
            .isNotEqualTo(givenAuthToken.accessToken());
        
    }

}
