package dev.prvt.yawiki.web.config;

import dev.prvt.yawiki.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.web.converter.NamespaceParser;
import dev.prvt.yawiki.web.converter.WikiPageTitleConverterImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WikiPageTitleProperties.class)
@RequiredArgsConstructor
public class WikiPageTitleConverterConfig {
    private final WikiPageTitleProperties wikiPageTitleProperties;

    @Bean
    public NamespaceParser namespaceParser() {
        return new NamespaceParser(wikiPageTitleProperties.getIdentifierMap());
    }
    @Bean
    public WikiPageTitleConverter wikiPageTitleConverter() {
        return new WikiPageTitleConverterImpl(namespaceParser());
    }
}
