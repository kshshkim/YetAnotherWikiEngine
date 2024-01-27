package dev.prvt.yawiki.titleexistence.web.api;

import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverter;
import dev.prvt.yawiki.common.util.web.converter.WikiPageTitleConverterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Import(WikiPageTitleConverterConfig.class)
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final WikiPageTitleConverter wikiPageTitleConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(wikiPageTitleConverter);
    }
}
