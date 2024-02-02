package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenException;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import dev.prvt.yawiki.common.util.test.FixedCurrentTimeProvider;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class JpaRefreshTokenManagerTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JpaRefreshTokenManager refreshTokenManager;

    private UUID givenId;
    private String givenName;

    private RefreshToken.RefreshTokenBuilder refreshTokenBuilder;
    Instant now;
    Instant issuedAt;
    Instant renewedAt;
    Instant expiresAt;

    @BeforeEach
    void init() {
        int refreshTokenLifespan = 18000;
        now = Instant.now();
        refreshTokenManager = new JpaRefreshTokenManager(
            refreshTokenRepository,
            new FixedCurrentTimeProvider(now), refreshTokenLifespan
        );

        givenId = UUID.randomUUID();
        givenName = randString();

        issuedAt = now.minusSeconds(24000 * 60);
        renewedAt = now.minusSeconds(12000 * 60);
        expiresAt = now.plusSeconds(300 * 60);

        refreshTokenBuilder = RefreshToken.builder()
                                  .subjectId(givenId)
                                  .subjectName(givenName)
                                  .id(UUID.randomUUID())
                                  .token(UUID.randomUUID())
                                  .issuedAt(issuedAt)
                                  .renewedAt(renewedAt)
                                  .expiresAt(expiresAt)
        ;
    }

    /**
     * refresh token record 값을 검증하는 메서드
     *
     * @param refreshTokenRecord 검증할 record
     * @param subjectId          발급 대상자 ID
     * @param subjectName        발급 대상자 이름
     */
    void assertTokenValueMatches(
        RefreshTokenRecord refreshTokenRecord,
        UUID subjectId,
        String subjectName
    ) {
        assertThat(refreshTokenRecord.subjectId())
            .describedAs("발급 대상의 ID가 제대로 설정되어야함.")
            .isEqualTo(subjectId);

        assertThat(refreshTokenRecord.subjectName())
            .describedAs("발급 대상의 이름이 제대로 설정되어야함.")
            .isEqualTo(subjectName);

        assertThat(refreshTokenRecord.refreshToken())
            .describedAs("토큰 값은 UUID 값과 길이가 같아야함.")
            .isNotNull()
            .hasSize(36);

        assertThatCode(() -> UUID.fromString(refreshTokenRecord.refreshToken()))
            .describedAs("토큰 값은 UUID 타입으로 변환될 수 있어야함.")
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("토큰 발급, 성공해야함.")
    void issue() {
        // given

        // when
        RefreshTokenRecord issued = refreshTokenManager.issue(givenId, givenName);
        assertTokenValueMatches(issued, givenId, givenName);
    }

    @Test
    @DisplayName("토큰 갱신, 성공해야함.")
    void renew() {
        // given
        RefreshToken refreshTokenEntity = refreshTokenBuilder.build();
        String beforeTokenValue = refreshTokenEntity.getTokenValue();

        given(refreshTokenRepository.findByToken(refreshTokenEntity.getToken()))
            .willReturn(Optional.of(refreshTokenEntity));

        // when
        RefreshTokenRecord renewed = refreshTokenManager.renew(
            beforeTokenValue,
            refreshTokenEntity.getSubjectName()
        );

        // then
        assertTokenValueMatches(renewed, givenId, givenName);
        assertThat(renewed.refreshToken())
            .describedAs("refresh 토큰은 일회용이기 때문에, 토큰이 갱신되면 값이 바뀌어야함.")
            .isNotEqualTo(beforeTokenValue);
    }

    @Test
    @DisplayName("토큰 갱신, 만료된 경우 실패해야함")
    void renew_when_token_expired() {
        // given
        RefreshToken token = refreshTokenBuilder
                                 .expiresAt(now.minusSeconds(1))  // 1초 전 만료됨.
                                 .build();

        String tokenValue = token.getTokenValue();

        given(refreshTokenRepository.findByToken(token.getToken()))
            .willReturn(Optional.of(token));

        // when then
        assertThatThrownBy(() -> refreshTokenManager.renew(tokenValue, token.getSubjectName()))
            .describedAs("토큰이 만료된 경우 실패해야함.")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("토큰 갱신, 발급 대상자 이름이 일치하지 않으면 실패해야함.")
    void renew_when_name_mismatch() {
        // given
        RefreshToken token = refreshTokenBuilder.build();
        String tokenValue = token.getTokenValue();

        given(refreshTokenRepository.findByToken(token.getToken()))
            .willReturn(Optional.of(token));

        // when then
        assertThatThrownBy(() -> refreshTokenManager.renew(tokenValue, "WrongName!"))
            .describedAs("발급 대상의 이름이 일치하지 않으면 실패하여야함.")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("mismatch")
        ;
    }

    @Test
    @DisplayName("토큰 갱신, 토큰 값과 일치하는 엔티티를 찾지 못하는 경우 실패해야함.")
    void renew_not_found() {
        // given
        RefreshToken token = refreshTokenBuilder.build();
        String tokenValue = token.getTokenValue();

        given(refreshTokenRepository.findByToken(token.getToken()))
            .willReturn(Optional.empty());

        // when then
        assertThatThrownBy(
            () -> refreshTokenManager.renew(tokenValue, token.getSubjectName()))
            .describedAs("토큰 값으로 찾을 수 없는 경우 실패해야함.")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("not found")
        ;
    }

    @Test
    @DisplayName("토큰 유효성 검증, 토큰 값과 일치하는 엔티티를 찾지 못하는 경우 실패해야함.")
    void verify_not_found() {
        // given
        RefreshToken token = refreshTokenBuilder.build();
        String tokenValue = token.getTokenValue();

        given(refreshTokenRepository.findByToken(token.getToken()))
            .willReturn(Optional.empty());

        // when then
        assertThatThrownBy(
            () -> refreshTokenManager.verify(tokenValue, token.getSubjectName()))
            .describedAs("토큰 값으로 찾을 수 없는 경우 실패해야함.")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("not found")
        ;
    }

    @Test
    @DisplayName("토큰 유효성 검증, 발급 대상 이름이 일치하지 않으면 실패해야함.")
    void verify_name_mismatch() {
        // given
        RefreshToken token = refreshTokenBuilder.build();
        String tokenValue = token.getTokenValue();

        given(refreshTokenRepository.findByToken(token.getToken()))
            .willReturn(Optional.of(token));

        // when then
        assertThatThrownBy(
            () -> refreshTokenManager.verify(tokenValue, "wrongName" + token.getSubjectName()))
            .describedAs("토큰 값으로 찾을 수 없는 경우 실패해야함.")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("mismatch")
        ;
    }

    @Test
    @DisplayName("토큰 유효성 검증, 만료된 경우 실패해야함")
    void verify_when_token_expired() {
        // given
        RefreshToken token = refreshTokenBuilder
                                 .expiresAt(now.minusSeconds(1))  // 1초 전 만료됨.
                                 .build();

        String tokenValue = token.getTokenValue();

        given(refreshTokenRepository.findByToken(token.getToken()))
            .willReturn(Optional.of(token));

        // when then
        assertThatThrownBy(() -> refreshTokenManager.verify(tokenValue, token.getSubjectName()))
            .describedAs("토큰이 만료된 경우 실패해야함.")
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("expired");
    }
}