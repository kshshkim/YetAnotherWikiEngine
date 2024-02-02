package dev.prvt.yawiki.auth.authprocessor.application;

import dev.prvt.yawiki.auth.jwt.domain.AuthToken;

/**
 * 인증/인가 서비스 인터페이스.
 */
public interface AuthProcessorService {

    /**
     * @param username 사용자의 username
     * @param password 사용자의 password
     * @return access token, refresh token 을 포함한 {@link AuthToken}
     */
    AuthToken usernamePasswordAuth(String username, String password);

    /**
     * @param username 사용자의 username
     * @param refreshToken 사용자가 발급받은 refresh token
     * @return access token, refresh token 을 포함한 {@link AuthToken}
     */
    AuthToken refreshTokenAuth(String username, String refreshToken);

}
