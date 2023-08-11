package dev.prvt.yawiki.core.wikipage.application.dto;

import java.util.Collection;

public record WikiPageDataForRead(
        String title,
        String content,
        Collection<String> validWikiReferences) {
}
