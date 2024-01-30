package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Transactional
@RequiredArgsConstructor
public class JpaRefreshTokenManager implements RefreshTokenManager {
    private final RefreshTokenRepository refreshTokenRepository;
    private final int refreshTokenLifespan;

    @Override
    public RefreshTokenRecord issue(UUID contributorId, String contributorName) {
        RefreshToken saved = refreshTokenRepository.save(RefreshToken.create(contributorId, contributorName, refreshTokenLifespan));
        return new RefreshTokenRecord(saved.getTokenValue(), saved.getIssuedToId(), saved.getIssuedToName());
    }

    @Override
    public RefreshTokenRecord renew(String refreshToken, String contributorName) {
        RefreshToken found = refreshTokenRepository.findByToken(UUID.fromString(refreshToken))
                .orElseThrow(() -> new RuntimeException("token not found"));

        if (!found.getIssuedToName().equals(contributorName)) {
            throw new RuntimeException("name mismatch");
        }

        found.renew(refreshTokenLifespan);
        return new RefreshTokenRecord(found.getTokenValue(), found.getIssuedToId(), contributorName);
    }
}
