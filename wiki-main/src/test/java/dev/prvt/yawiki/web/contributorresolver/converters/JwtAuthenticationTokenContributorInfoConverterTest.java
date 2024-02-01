//package dev.prvt.yawiki.web.contributorresolver.converters;
//
//import dev.prvt.yawiki.fixture.SecurityFixture;
//import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArg;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class JwtAuthenticationTokenContributorInfoConverterTest {
//
//    JwtAuthenticationTokenContributorInfoConverter converter = new JwtAuthenticationTokenContributorInfoConverter();
//    @Test
//    void supports_success() {
//        boolean supports = converter.supports(SecurityFixture.aJwtAuthenticationToken());
//        assertThat(supports)
//                .isTrue();
//    }
//
//    @Test
//    void supports_fail() {
//        boolean supports = converter.supports(SecurityFixture.anAnonymousAuthenticationToken());
//        assertThat(supports)
//                .isFalse();
//    }
//
//    @Test
//    void convert() {
//        UUID givenId = UUID.randomUUID();
//        String givenName = UUID.randomUUID().toString();
//        JwtAuthenticationToken authenticationToken = SecurityFixture.aJwtAuthenticationToken(givenId, givenName);
//
//        // when
//        ContributorInfoArg converted = converter.convert(authenticationToken);
//        // then
//        assertThat(converted)
//                .isNotNull();
//        assertThat(converted.contributorId())
//                .isEqualTo(givenId);
//
//    }
//}