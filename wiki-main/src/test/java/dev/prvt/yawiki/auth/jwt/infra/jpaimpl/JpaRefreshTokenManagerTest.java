package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenExpirationException;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class JpaRefreshTokenManagerTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    private JpaRefreshTokenManager jpaRefreshTokenManager;

    private UUID givenId;
    private String givenName;

    private RefreshToken.RefreshTokenBuilder refreshTokenBuilder;

    @BeforeEach
    void init() {
        int refreshTokenLifespan = 18000;
        jpaRefreshTokenManager = new JpaRefreshTokenManager(refreshTokenRepository, refreshTokenLifespan);
        givenId = UUID.randomUUID();
        givenName = randString();
        refreshTokenBuilder = RefreshToken.builder()
                .issuedToName(givenName)
                .issuedToId(givenId)
                .id(UUID.randomUUID())
                .token(UUID.randomUUID())
                .issuedAt(LocalDateTime.now().minusMinutes(24000))
                .renewedAt(LocalDateTime.now().minusMinutes(12000))
                .expiresAt(LocalDateTime.now().plusMinutes(300))
        ;
    }

    @Test
    void issue() {
        when(refreshTokenRepository.save(any()))
                .thenAnswer(
                        invocation -> invocation.getArgument(0)
                );
        RefreshTokenRecord issue = jpaRefreshTokenManager.issue(givenId, givenName);
        assertThat(issue.issuedToId())
                .isEqualTo(givenId);
        assertThat(issue.issuedToName())
                .isEqualTo(givenName);
        assertThat(issue.refreshToken())
                .isNotNull()
                .hasSize(36);
    }

    @Test
    void renew_when_token_exist() {
        // given
        RefreshToken givenRefreshToken = refreshTokenBuilder.build();
        String givenRefreshTokenTokenValue = givenRefreshToken.getTokenValue();

        when(refreshTokenRepository.findByToken(givenRefreshToken.getToken()))
                .thenReturn(Optional.of(givenRefreshToken));

        // when
        RefreshTokenRecord renew = jpaRefreshTokenManager.renew(givenRefreshTokenTokenValue, givenRefreshToken.getIssuedToName());

        // then
        assertThat(renew.refreshToken())
                .isNotNull()
                .isNotBlank()
                .isNotEqualTo(givenRefreshTokenTokenValue);

        assertThat(renew.issuedToName())
                .isEqualTo(givenRefreshToken.getIssuedToName());
        assertThat(renew.issuedToId())
                .isEqualTo(givenRefreshToken.getIssuedToId());
    }

    @Test
    void renew_when_token_expired() {
        RefreshToken givenRefreshToken = refreshTokenBuilder
                .expiresAt(LocalDateTime.now().minusMinutes(3))
                .build();
        String givenRefreshTokenTokenValue = givenRefreshToken.getTokenValue();

        when(refreshTokenRepository.findByToken(givenRefreshToken.getToken()))
                .thenReturn(Optional.of(givenRefreshToken));

        // when then
        assertThatThrownBy(() -> jpaRefreshTokenManager.renew(givenRefreshTokenTokenValue, givenRefreshToken.getIssuedToName()))
                .isInstanceOf(RefreshTokenExpirationException.class);
    }

    @Test
    void renew_when_name_mismatch() {
        // given
        RefreshToken givenRefreshToken = refreshTokenBuilder.build();
        String givenRefreshTokenTokenValue = givenRefreshToken.getTokenValue();

        when(refreshTokenRepository.findByToken(givenRefreshToken.getToken()))
                .thenReturn(Optional.of(givenRefreshToken));

        // when then
        assertThatThrownBy(() -> jpaRefreshTokenManager.renew(givenRefreshTokenTokenValue, givenRefreshToken.getIssuedToName() + randString()))
                .describedAs("contributor name mismatch")
                .hasMessageContaining("mismatch")
        ;
    }

    @Test
    void renew_not_found() {
        // given
        RefreshToken givenRefreshToken = refreshTokenBuilder.build();
        String givenRefreshTokenTokenValue = givenRefreshToken.getTokenValue();

        when(refreshTokenRepository.findByToken(givenRefreshToken.getToken()))
                .thenReturn(Optional.ofNullable(null));

        // when then
        assertThatThrownBy(() -> jpaRefreshTokenManager.renew(givenRefreshTokenTokenValue, givenRefreshToken.getIssuedToName()))
                .describedAs("refresh token not found")
                .hasMessageContaining("not found")
        ;
    }
}