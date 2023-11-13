package dev.prvt.yawiki.core.wikipage.application.dto;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Collection;

public record WikiPageDataForRead(
        String title,
        Namespace namespace,
        String content,
        Collection<WikiPageTitle> validWikiReferences) {
}
