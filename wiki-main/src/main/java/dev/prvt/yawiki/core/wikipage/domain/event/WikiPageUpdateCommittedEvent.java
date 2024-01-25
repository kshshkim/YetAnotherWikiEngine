package dev.prvt.yawiki.core.wikipage.domain.event;

import dev.prvt.yawiki.common.model.WikiPageTitle;

import java.util.Set;
import java.util.UUID;

public record WikiPageUpdateCommittedEvent(
        UUID contributorId,
        UUID wikiPageId,
        WikiPageTitle wikiPageTitle,
        Set<WikiPageTitle> referencedTitles
) {
}