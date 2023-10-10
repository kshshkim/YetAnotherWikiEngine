package dev.prvt.yawiki.auth.jwt.application;

import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtIssuerImpl implements JwtIssuer {
    private final AccessTokenGenerator accessTokenGenerator;
    private final RefreshTokenManager refreshTokenManager;

    @Override
    public AuthToken issue(UUID contributorId, String contributorName) {
        RefreshTokenRecord refreshTokenRecord = refreshTokenManager.issue(contributorId, contributorName);
        String accessToken = accessTokenGenerator.generate(contributorId, contributorName);
        return new AuthToken(accessToken, refreshTokenRecord.refreshToken());
    }

    @Override
    public AuthToken renew(String refreshToken, String contributorName) {
        RefreshTokenRecord renewedRefreshToken = refreshTokenManager.renew(refreshToken, contributorName);
        String accessToken = accessTokenGenerator.generate(renewedRefreshToken.issuedToId(), renewedRefreshToken.issuedToName());
        return new AuthToken(accessToken, renewedRefreshToken.refreshToken());
    }
}
