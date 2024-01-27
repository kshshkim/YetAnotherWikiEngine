package dev.prvt.yawiki.common.util.web.converter;

import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.common.util.NamespaceParser;
import dev.prvt.yawiki.common.util.StringToWikiPageTitleConverter;

public class WikiPageTitleConverterImpl implements WikiPageTitleConverter {
    private final StringToWikiPageTitleConverter stringToWikiPageTitleConverter;

    public WikiPageTitleConverterImpl(StringToWikiPageTitleConverter stringToWikiPageTitleConverter) {
        this.stringToWikiPageTitleConverter = stringToWikiPageTitleConverter;
    }

    public WikiPageTitleConverterImpl(NamespaceParser namespaceParser) {
        this.stringToWikiPageTitleConverter = new StringToWikiPageTitleConverter(namespaceParser);
    }

    @Override
    public WikiPageTitle convert(String source) {
        return stringToWikiPageTitleConverter.convert(source);
    }
}
