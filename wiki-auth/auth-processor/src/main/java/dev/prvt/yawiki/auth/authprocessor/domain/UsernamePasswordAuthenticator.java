package dev.prvt.yawiki.auth.authprocessor.domain;

import dev.prvt.yawiki.auth.authprocessor.exception.AuthenticationException;
import java.util.UUID;

public interface UsernamePasswordAuthenticator {

    /**
     * 유저네임-패스워드 기반 인증 처리기.
     * @param username username
     * @param password password
     * @return 사용자의 ID
     * @throws AuthenticationException
     */
    UUID authenticate(String username, String password) throws AuthenticationException;

}
