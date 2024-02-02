package dev.prvt.yawiki.auth.jwt.domain;

import static dev.prvt.yawiki.common.util.test.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;

import dev.prvt.yawiki.auth.jwt.domain.TokenPayload.CustomClaim;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class TokenPayloadTest {

    Stream<CustomClaim> randomCustomClaims() {
        return Stream.generate(() -> new CustomClaim(randString(), randString()))
            .limit(10);
    }

    List<CustomClaim> getRandomCustomClaimsWith(CustomClaim... fixedClaims) {
        List<CustomClaim> customClaims = new ArrayList<>(randomCustomClaims().toList());
        customClaims.addAll(Arrays.stream(fixedClaims).toList());
        customClaims.addAll(randomCustomClaims().toList());
        return customClaims;
    }

    @Test
    void getCustomClaim() {
        // given
        CustomClaim givenClaim = new CustomClaim("customKey", "customValue");
        Instant now = Instant.now();

        List<CustomClaim> customClaims = getRandomCustomClaimsWith(givenClaim);

        TokenPayload tokenPayload = new TokenPayload(
            randString(),
            randString(),
            now,
            now.plusSeconds(1000),
            customClaims
        );

        // when then
        assertThat(tokenPayload.getCustomClaim(givenClaim.key()))
            .describedAs("값이 일치해야함.")
            .isEqualTo(givenClaim.value());

    }

}