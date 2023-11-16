package dev.prvt.yawiki.config;

import dev.prvt.yawiki.core.wikipage.infra.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import dev.prvt.yawiki.web.contributorresolver.converters.AnonymousAuthenticationTokenContributorInfoConverter;
import dev.prvt.yawiki.web.contributorresolver.converters.JwtAuthenticationTokenContributorInfoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AnonymousAuthenticationTokenContributorInfoConverter anonymousAuthenticationTokenContributorInfoConverter;
    private final JwtAuthenticationTokenContributorInfoConverter jwtAuthenticationTokenContributorInfoConverter;
    private final WikiPageTitleConverter wikiPageTitleConverter;
    @Bean
    public ContributorInfoArgumentResolver contributorInfoArgumentResolver() {
        ContributorInfoArgumentResolver contributorInfoArgumentResolver = new ContributorInfoArgumentResolver();
        contributorInfoArgumentResolver.addConverter(anonymousAuthenticationTokenContributorInfoConverter);
        contributorInfoArgumentResolver.addConverter(jwtAuthenticationTokenContributorInfoConverter);
        return contributorInfoArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(contributorInfoArgumentResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(wikiPageTitleConverter);
    }
}
