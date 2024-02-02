package dev.prvt.yawiki.auth.authprocessor.domain;

import dev.prvt.yawiki.auth.authprocessor.exception.TokenPayloadLoaderException;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload;
import java.util.UUID;

public interface TokenPayloadLoader {

    /**
     * 인증 토큰에 포함될 Payload 를 반환함. {@link TokenPayload}의 모든 필드가 필수적으로 들어가야함.
     * @param loaderKey 발급 대상자의 ID, username 튜플
     * @return TokenPayload
     */
    TokenPayload load(LoaderKey loaderKey) throws TokenPayloadLoaderException;

    record LoaderKey(
        UUID id,
        String username
    ) {

    }

}
