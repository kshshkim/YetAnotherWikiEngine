package dev.prvt.yawiki.web.config;

import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import dev.prvt.yawiki.web.contributorresolver.converters.AnonymousAuthenticationTokenContributorInfoConverter;
import dev.prvt.yawiki.web.contributorresolver.converters.JwtAuthenticationTokenContributorInfoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ContributorInfoArgumentResolverConfig {
    private final AnonymousAuthenticationTokenContributorInfoConverter anonymousAuthenticationTokenContributorInfoConverter;
    private final JwtAuthenticationTokenContributorInfoConverter jwtAuthenticationTokenContributorInfoConverter;

    @Bean
    public ContributorInfoArgumentResolver contributorInfoArgumentResolver() {
        ContributorInfoArgumentResolver contributorInfoArgumentResolver = new ContributorInfoArgumentResolver();
        contributorInfoArgumentResolver.addConverter(anonymousAuthenticationTokenContributorInfoConverter);
        contributorInfoArgumentResolver.addConverter(jwtAuthenticationTokenContributorInfoConverter);
        return contributorInfoArgumentResolver;
    }

}
