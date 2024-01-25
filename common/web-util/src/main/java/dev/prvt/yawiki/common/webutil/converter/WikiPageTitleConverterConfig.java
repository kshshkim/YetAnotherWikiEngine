package dev.prvt.yawiki.common.webutil.converter;

import dev.prvt.yawiki.common.util.StringToWikiPageTitleConverter;
import dev.prvt.yawiki.common.util.NamespaceParser;
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
        return new WikiPageTitleConverterImpl(new StringToWikiPageTitleConverter(namespaceParser()));
    }
}
