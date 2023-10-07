package dev.prvt.yawiki.web.contributorresolver;

import dev.prvt.yawiki.fixture.SecurityFixture;
import dev.prvt.yawiki.web.contributorresolver.converters.AnonymousAuthenticationTokenContributorInfoConverter;
import dev.prvt.yawiki.web.contributorresolver.converters.AnonymousAuthenticationTokenContributorInfoConverterTest.DummyContributorApplicationService;
import dev.prvt.yawiki.web.contributorresolver.converters.JwtAuthenticationTokenContributorInfoConverter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.net.InetAddress;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContributorInfoArgumentResolverTest {

    ContributorInfoArgumentResolver contributorInfoArgumentResolver;
    AnonymousAuthenticationTokenContributorInfoConverter anonConverter;
    JwtAuthenticationTokenContributorInfoConverter jwtConverter = new JwtAuthenticationTokenContributorInfoConverter();
    DummyContributorApplicationService dummyContributorApplicationService;
    @BeforeEach
    void init() {
        dummyContributorApplicationService = new DummyContributorApplicationService(null);
        anonConverter = new AnonymousAuthenticationTokenContributorInfoConverter(dummyContributorApplicationService);
        contributorInfoArgumentResolver = new ContributorInfoArgumentResolver();
        contributorInfoArgumentResolver.addConverter(anonConverter);
        contributorInfoArgumentResolver.addConverter(jwtConverter);
        SecurityContextHolder.clearContext();
    }

    @Test
    void addConverter_duplicate() {
        assertThatThrownBy(() -> contributorInfoArgumentResolver.addConverter(new JwtAuthenticationTokenContributorInfoConverter()))
                .hasMessageContaining("duplicate")
        ;
    }

    @SneakyThrows
    @Test
    void resolveArgument_jwt() {
        // given
        JwtAuthenticationToken givenJwtToken = SecurityFixture.aJwtAuthenticationToken();
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(givenJwtToken);

        // when
        Object o = contributorInfoArgumentResolver.resolveArgument(null, null, null, null);
        // then
        assertThat(o).isInstanceOf(ContributorInfoArg.class);
        ContributorInfoArg contributorInfoArg = (ContributorInfoArg) o;
        assertThat(contributorInfoArg).isNotNull();
        assertThat(contributorInfoArg.contributorId())
                .isEqualTo(UUID.fromString((String) givenJwtToken.getTokenAttributes().get("contributorId")));
    }

    @SneakyThrows
    @Test
    void resolveArgument_anon() {
        // given
        AnonymousAuthenticationToken anonymousAuthenticationToken = SecurityFixture.anAnonymousAuthenticationToken();
        WebAuthenticationDetails details = (WebAuthenticationDetails) anonymousAuthenticationToken.getDetails();

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(anonymousAuthenticationToken);

        // when
        Object o = contributorInfoArgumentResolver.resolveArgument(null, null, null, null);
        // then
        assertThat(o)
                .isNotNull()
                .isInstanceOf(ContributorInfoArg.class);
        assertThat(dummyContributorApplicationService.getCalled())
                .isEqualTo(InetAddress.getByName(details.getRemoteAddress()));
    }
}