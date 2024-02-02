package dev.prvt.yawiki.auth.authprocessor.application;

import dev.prvt.yawiki.auth.authprocessor.domain.TokenPayloadLoader;
import dev.prvt.yawiki.auth.authprocessor.domain.TokenPayloadLoader.LoaderKey;
import dev.prvt.yawiki.auth.authprocessor.domain.UsernamePasswordAuthenticator;
import dev.prvt.yawiki.auth.jwt.domain.AccessTokenGenerator;
import dev.prvt.yawiki.auth.jwt.domain.AuthToken;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenManager;
import dev.prvt.yawiki.auth.jwt.domain.RefreshTokenRecord;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthProcessorServiceImpl implements AuthProcessorService {

    private final UsernamePasswordAuthenticator usernamePasswordAuthenticator;
    private final AccessTokenGenerator accessTokenGenerator;
    private final RefreshTokenManager refreshTokenManager;
    private final TokenPayloadLoader tokenPayloadLoader;

    @Override
    public AuthToken usernamePasswordAuth(String username, String password) {
        // 인증
        UUID memberId = usernamePasswordAuthenticator.authenticate(username, password);

        // 토큰 페이로드 로드
        TokenPayload payload = tokenPayloadLoader.load(new LoaderKey(memberId, username));

        // 액세스 토큰 발급
        String accessToken = accessTokenGenerator.generate(payload);

        // 리프레시 토큰 발급
        RefreshTokenRecord issued = refreshTokenManager.issue(memberId, username);

        return new AuthToken(accessToken, issued.refreshToken());
    }

    /**
     * refresh token 을 이용한 인증. refresh 토큰을 갱신하는 경우 값이 변경되기 때문에 가장 마지막에 수행되어야함.
     * RDB 기반 구현의 경우 트랜잭션 롤백을 통해 간단하게 해결 가능하지만, rdb 이외의 db 로 대체될 가능성이 높다고 판단하였기 때문에 인증과 갱신을 분리함.
     *
     * @param username 사용자의 username
     * @param refreshToken 사용자가 발급받은 refresh token
     */
    @Override
    public AuthToken refreshTokenAuth(String username, String refreshToken) {
        // 인증
        RefreshTokenRecord found = refreshTokenManager.verify(refreshToken, username);

        // 토큰 페이로드 로드
        TokenPayload fetch = tokenPayloadLoader.load(new LoaderKey(found.subjectId(), username));

        // 액세스 토큰 발급
        String accessToken = accessTokenGenerator.generate(fetch);

        // 리프레시 토큰 갱신
        RefreshTokenRecord renewed = refreshTokenManager.renew(refreshToken, username);

        return new AuthToken(accessToken, renewed.refreshToken());
    }

}
