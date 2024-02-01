//package dev.prvt.yawiki.web.contributorresolver.converters;
//
//import dev.prvt.yawiki.core.contributor.application.ContributorApplicationService;
//import dev.prvt.yawiki.core.contributor.application.ContributorData;
//import dev.prvt.yawiki.core.contributor.domain.AnonymousContributor;
//import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
//import dev.prvt.yawiki.fixture.SecurityFixture;
//import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArg;
//import lombok.Getter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//
//import java.net.InetAddress;
//import java.util.UUID;
//
//import static dev.prvt.yawiki.fixture.SecurityFixture.aJwtAuthenticationToken;
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class AnonymousAuthenticationTokenContributorInfoConverterTest {
//    @Getter
//    public static class DummyContributorApplicationService extends ContributorApplicationService {
//
//        public DummyContributorApplicationService(ContributorRepository contributorRepository) {
//            super(null);
//        }
//
//        private InetAddress called;
//
//        @Override
//        public ContributorData getContributorByIpAddress(InetAddress inetAddress) {
//            called = inetAddress;
//            return ContributorData.from(
//                    AnonymousContributor.builder()
//                            .id(UUID.randomUUID())
//                            .ipAddress(inetAddress)
//                            .build()
//            );
//        }
//    }
//
//    AnonymousAuthenticationToken givenAuthToken;
//    AnonymousAuthenticationTokenContributorInfoConverter converter;
//
//    DummyContributorApplicationService contributorApplicationService;
//
//    @BeforeEach
//    void init() {
//        givenAuthToken = SecurityFixture.anAnonymousAuthenticationToken();
//        contributorApplicationService = new DummyContributorApplicationService(null);
//        converter = new AnonymousAuthenticationTokenContributorInfoConverter(contributorApplicationService);
//    }
//
//    @Test
//    void supports_success() {
//        boolean supports = converter.supports(givenAuthToken);
//        assertThat(supports).isTrue();
//    }
//
//    @Test
//    void supports_fail() {
//        boolean supports = converter.supports(aJwtAuthenticationToken());
//        assertThat(supports).isFalse();
//    }
//
//    @Test
//    void convert() {
//        ContributorInfoArg converted = converter.convert(givenAuthToken);
//        WebAuthenticationDetails details = (WebAuthenticationDetails) givenAuthToken.getDetails();
//        String givenInetAddress = details.getRemoteAddress();
//        // then
//        assertThat(converted)
//                .isNotNull();
//        assertThat(contributorApplicationService.getCalled().getHostAddress())
//                .isNotNull()
//                .isEqualTo(givenInetAddress);
//    }
//}