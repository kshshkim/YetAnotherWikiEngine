package dev.prvt.yawiki.auth.authprocessor.infra;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.auth.authprocessor.domain.TokenPayloadLoader;
import dev.prvt.yawiki.auth.authprocessor.domain.TokenPayloadLoader.LoaderKey;
import dev.prvt.yawiki.auth.jwt.domain.TokenPayload;
import dev.prvt.yawiki.common.util.CurrentTimeProvider;
import dev.prvt.yawiki.common.util.test.FixedCurrentTimeProvider;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TokenPayloadLoaderEssentialImplTest {

    Instant now = Instant.now();
    int accessTokenLifespan = 1000;
    String issuer = "issuer" + randString();

    CurrentTimeProvider fixedTimeProvider = new FixedCurrentTimeProvider(now);
    TokenPayloadLoader tokenPayloadLoader = new TokenPayloadLoaderEssentialImpl(
        fixedTimeProvider,
        accessTokenLifespan,
        issuer
    );

    @Test
    void load() {
        UUID givenId = UUID.randomUUID();
        String givenUsername = "username" + randString();

        TokenPayload payload = tokenPayloadLoader.load(new LoaderKey(givenId, givenUsername));

        assertThat(payload.issuer())
            .describedAs("발급자가 정상적으로 입력되어야함.")
            .isEqualTo(issuer);

        assertThat(payload.subject())
            .describedAs("subject 정상적으로 입력되어야함.")
            .isEqualTo(givenId.toString());

        assertThat(payload.issuer())
            .describedAs("발급자가 정상적으로 입력되어야함.")
            .isEqualTo(issuer);

        assertThat(payload.issuedAt())
            .describedAs("발급 일자가 정상적으로 입력되어야함.")
            .isEqualTo(now);

        assertThat(payload.expiresAt())
            .describedAs("만료 일자가 정상적으로 입력되어야함.")
            .isEqualTo(now.plusSeconds(accessTokenLifespan));

        assertThat(payload.getCustomClaim("name"))
            .describedAs("커스텀 클레임 name 필드가 정상적으로 입력되어야함.")
            .isEqualTo(givenUsername);


    }
}