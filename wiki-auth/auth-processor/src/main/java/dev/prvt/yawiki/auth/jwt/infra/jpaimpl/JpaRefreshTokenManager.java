package dev.prvt.yawiki.auth.jwt.infra.jpaimpl;

import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenException;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class JpaRefreshTokenManager implements RefreshTokenManager {

    private final RefreshTokenRepository refreshTokenRepository;
    private final int refreshTokenLifespan;
    private final CurrentTimeProvider currentTimeProvider;

    /**
     * @param refreshTokenRepository 리프레시 토큰 저장소
     * @param currentTimeProvider    현재 시각을 제공하는 클래스
     * @param refreshTokenLifespan   리프레시 토큰 유효 기간 (초)
     */
    public JpaRefreshTokenManager(
        RefreshTokenRepository refreshTokenRepository,
        CurrentTimeProvider currentTimeProvider,
        int refreshTokenLifespan
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenLifespan = refreshTokenLifespan;
        this.currentTimeProvider = currentTimeProvider;
    }

    @Override
    public RefreshTokenRecord issue(UUID subjectId, String subjectName) {
        RefreshToken created = RefreshToken.create(
            subjectId,
            subjectName,
            refreshTokenLifespan,
            currentTimeProvider.getCurrentInstant()
        );
        refreshTokenRepository.save(created);
        return created.mapToRecord();
    }

    @Override
    public RefreshTokenRecord renew(String refreshToken, String subjectName) {
        return getRefreshToken(refreshToken)
                   .renew(
                       subjectName,
                       refreshTokenLifespan,
                       currentTimeProvider.getCurrentInstant()
                   )
                   .mapToRecord();
    }

    @Override
    public RefreshTokenRecord verify(String refreshToken, String subjectName) throws RefreshTokenException {
        return getRefreshToken(refreshToken)
                   .verify(
                       subjectName,
                       currentTimeProvider.getCurrentInstant()
                   )
                   .mapToRecord();
    }

    private RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(UUID.fromString(refreshToken))
                   .orElseThrow(() -> RefreshTokenException.NOT_FOUND);
    }
}
