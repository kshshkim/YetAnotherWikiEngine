package dev.prvt.yawiki.common.util;

import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringToWikiPageTitleConverter {

    private final NamespaceParser namespaceParser;

    public WikiPageTitle convert(String source) {
        Namespace namespace = namespaceParser.getNamespace(source);
        String title = namespace.equals(Namespace.NORMAL)
                           ? source
                           : getStrippedTitle(source);

        return new WikiPageTitle(title, namespace);
    }


    private static String getStrippedTitle(String titleWithIdentifier) {
        int separatorIdx = titleWithIdentifier.indexOf(":");
        return titleWithIdentifier
                   .substring(separatorIdx + 1)
                   .strip();
    }

}
