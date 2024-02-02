package dev.prvt.yawiki.auth.jwt.domain;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @param issuer       토큰 발급 주체
 * @param subject      토큰 발급 대상
 * @param issuedAt     토큰 발급 일시
 * @param expiresAt    토큰 만료 일시
 * @param customClaims 커스텀 클레임 목록
 */
public record TokenPayload(
    String issuer,
    String subject,
    Instant issuedAt,
    Instant expiresAt,
    Collection<CustomClaim> customClaims
) {

    public TokenPayload {
        requireNonNull(issuer);
        requireNonNull(subject);
        requireNonNull(issuedAt);
        requireNonNull(expiresAt);
        requireNonNull(customClaims);
    }

    public record CustomClaim(
        String key,
        String value
    ) {

    }

    /**
     * Map 에 custom claim 을 모두 넣는 Consumer. 값이 이미 존재하는 경우 덮어 씌우지 않음.
     */
    public Consumer<Map<String, Object>> putAllCustomClaimsToMap() {
        return this::putAllCustomClaimsToMap;
    }

    public void putAllCustomClaimsToMap(Map<String, Object> claimMap) {
        customClaims.forEach(
            claim -> claimMap.computeIfAbsent(claim.key(), value -> claim.value())
        );
    }

    public String getCustomClaim(String key) {
        return customClaims.stream()
                   .filter(cc -> cc.key().equals(key))
                   .map(CustomClaim::value)
                   .findAny()
                   .orElse(null);
    }

}
