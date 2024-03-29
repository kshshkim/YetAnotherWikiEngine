package dev.prvt.yawiki.core.wikipage.application.dto;


import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;

public record WikiPageDataForUpdate(
        String title,
        Namespace namespace,
        String content,
        String versionToken
) {

    public WikiPageTitle titleNamespaceToWikiPageTitle() {
        return new WikiPageTitle(title, namespace);
    }
}
