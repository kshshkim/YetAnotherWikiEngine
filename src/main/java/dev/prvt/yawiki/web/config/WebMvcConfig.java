package dev.prvt.yawiki.web.config;

import dev.prvt.yawiki.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.web.contributorresolver.ContributorInfoArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final WikiPageTitleConverter wikiPageTitleConverter;
    private final ContributorInfoArgumentResolver contributorInfoArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(contributorInfoArgumentResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(wikiPageTitleConverter);
    }
}
