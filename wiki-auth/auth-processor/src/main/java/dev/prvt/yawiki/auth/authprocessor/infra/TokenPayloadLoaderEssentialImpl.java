package dev.prvt.yawiki.auth.authprocessor.infra;

import dev.prvt.yawiki.auth.authprocessor.domain.TokenPayloadLoader;
import dev.prvt.yawiki.auth.authprocessor.exception.TokenPayloadLoaderException;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload.CustomClaim;
import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;


/**
 * 필수 정보만 포함하는 토큰 페이로드 로더. 커스텀 claim 불러오는 로직을 추가할 수 있음.
 * issuer, accessTokenLifespan 은 동적으로 변하는 정보가 아니라고 판단하여 final 값으로 두었음.
 */
@RequiredArgsConstructor
public class TokenPayloadLoaderEssentialImpl implements TokenPayloadLoader {

    private final CurrentTimeProvider currentTimeProvider;
    private final int accessTokenLifespan;
    private final String issuer;

    @Override
    public TokenPayload load(LoaderKey loaderKey) throws TokenPayloadLoaderException {
        try {
            Instant issuedAt = currentTimeProvider.getCurrentInstant();
            return new TokenPayload(
                issuer,                                     // issuer
                loaderKey.id().toString(),                  // subject
                issuedAt,                                   // issuedAt
                issuedAt.plusSeconds(accessTokenLifespan),  // expiresAt
                List.of(                                    // custom claims
                    new CustomClaim("name", loaderKey.username())
                )
            );
        } catch (Exception e) {
            throw new TokenPayloadLoaderException(e);
        }
    }
}
