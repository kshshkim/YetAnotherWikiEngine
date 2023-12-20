package dev.prvt.yawiki.web.converter;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class WikiPageTitleConverterImpl implements WikiPageTitleConverter {
    private final NamespaceParser namespaceParser;

    @Override
    public WikiPageTitle convert(String source) {
        Namespace namespace = namespaceParser.getNamespace(source);
        String title = namespace.equals(Namespace.NORMAL)
                ? source
                : getStrippedTitle(source);

        return new WikiPageTitle(title, namespace);
    }

    @NotNull
    private static String getStrippedTitle(String titleWithIdentifier) {
        int separatorIdx = titleWithIdentifier.indexOf(":");
        return titleWithIdentifier
                .substring(separatorIdx + 1)
                .strip();
    }
}
