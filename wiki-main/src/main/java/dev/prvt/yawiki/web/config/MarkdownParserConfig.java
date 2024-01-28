package dev.prvt.yawiki.web.config;

import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.web.markdown.ReferencedTitleExtractor;
import dev.prvt.yawiki.web.markdown.ReferencedTitleRegexExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MarkdownParserConfig {

    private final WikiPageTitleConverter wikiPageTitleConverter;

    @Bean
    public ReferencedTitleExtractor documentReferenceExtractor() {
        return new ReferencedTitleRegexExtractor(wikiPageTitleConverter);
    }
}
