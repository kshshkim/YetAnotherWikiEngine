package dev.prvt.yawiki.auth.jwt.application;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static dev.prvt.yawiki.common.testutil.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
class JwtIssuerImplTest {

    JwtIssuerImpl jwtIssuer;

    @Mock
    AccessTokenGenerator accessTokenGenerator;

    @Mock
    RefreshTokenManager refreshTokenManager;

    UUID givenContributorId;
    String givenContributorName;
    String expectingAccessToken;
    String expectingRefreshToken;
    String expectingRenewedRefreshToken;

    @BeforeEach
    void init() {
        givenContributorId = UUID.randomUUID();
        givenContributorName = randString();
        expectingAccessToken = givenContributorName + givenContributorId.toString();
        expectingRefreshToken = UUID.randomUUID().toString();

        Mockito.when(accessTokenGenerator.generate(givenContributorId, givenContributorName))
                .thenReturn(expectingAccessToken);

        jwtIssuer = new JwtIssuerImpl(accessTokenGenerator, refreshTokenManager);
    }

    @Test
    void issue() {
        Mockito.when(refreshTokenManager.issue(givenContributorId, givenContributorName))
                .thenReturn(new RefreshTokenRecord(expectingRefreshToken, givenContributorId, givenContributorName));

        AuthToken issued = jwtIssuer.issue(givenContributorId, givenContributorName);
        assertThat(tuple(issued.accessToken(), issued.refreshToken()))
                .isEqualTo(tuple(expectingAccessToken, expectingRefreshToken));
    }

    @Test
    void renew() {
        Mockito.when(refreshTokenManager.renew(expectingRefreshToken, givenContributorName))
                .thenReturn(new RefreshTokenRecord(expectingRenewedRefreshToken, givenContributorId, givenContributorName));

        String givenRefreshToken = expectingRefreshToken;
        AuthToken renew = jwtIssuer.renew(givenRefreshToken, givenContributorName);
        assertThat(renew)
                .isNotNull()
                .isEqualTo(new AuthToken(expectingAccessToken, expectingRenewedRefreshToken));
    }
}