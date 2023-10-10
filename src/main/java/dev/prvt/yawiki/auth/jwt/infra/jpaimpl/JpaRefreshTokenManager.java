package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import dev.prvt.yawiki.config.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class JpaRefreshTokenManager implements RefreshTokenManager {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    public RefreshTokenRecord issue(UUID contributorId, String contributorName) {
        RefreshToken saved = refreshTokenRepository.save(RefreshToken.create(contributorId, contributorName, jwtProperties.getRefreshTokenLifespan()));
        return new RefreshTokenRecord(saved.getTokenValue(), saved.getIssuedToId(), saved.getIssuedToName());
    }

    @Override
    public RefreshTokenRecord renew(String refreshToken, String contributorName) {
        RefreshToken found = refreshTokenRepository.findByToken(UUID.fromString(refreshToken))
                .orElseThrow(() -> new RuntimeException("token not found"));

        if (!found.getIssuedToName().equals(contributorName)) {
            throw new RuntimeException("name mismatch");
        }

        found.renew(jwtProperties.getRefreshTokenLifespan());
        return new RefreshTokenRecord(found.getTokenValue(), found.getIssuedToId(), contributorName);
    }
}
