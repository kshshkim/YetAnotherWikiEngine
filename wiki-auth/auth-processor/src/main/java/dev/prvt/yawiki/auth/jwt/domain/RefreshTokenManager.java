package dev.prvt.yawiki.auth.jwt.domain;

import java.util.UUID;

public interface RefreshTokenManager {
    RefreshTokenRecord issue(UUID subjectId, String subjectName) throws RefreshTokenException;

    /**
     * 리프레시 토큰을 갱신함. 기존 토큰은 폐기되어 새로 생성된 값으로 대체됨.
     * <p>타 웹사이트에서, Redis 등의 key value storage 를 세션 저장소로 사용했다가, 내부 오류 발생으로 인해 엉뚱한 인증 정보를 반환한 사례들이 존재함. 최소한의 검증을 위해 발급 대상자 이름을 인자로 받음.</p>
     */
    RefreshTokenRecord renew(String refreshToken, String subjectName) throws RefreshTokenException;

    /**
     * 토큰 값과 발급 대상자 이름이 일치하는지 검증함. 성공하는 경우 기존에 존재하는 리프레시 토큰을 그대로 반환함.
     */
    RefreshTokenRecord verify(String refreshToken, String subjectName) throws RefreshTokenException;
}
