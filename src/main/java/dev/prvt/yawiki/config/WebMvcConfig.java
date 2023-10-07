package dev.prvt.yawiki.config;

import dev.prvt.yawiki.web.contributorresolver.converters.AnonymousAuthenticationTokenContributorInfoConverter;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import dev.prvt.yawiki.web.contributorresolver.converters.JwtAuthenticationTokenContributorInfoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AnonymousAuthenticationTokenContributorInfoConverter anonymousAuthenticationTokenContributorInfoConverter;
    private final JwtAuthenticationTokenContributorInfoConverter jwtAuthenticationTokenContributorInfoConverter;

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
}
