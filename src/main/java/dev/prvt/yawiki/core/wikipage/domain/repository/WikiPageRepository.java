package dev.prvt.yawiki.core.wikipage.domain.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

import java.util.Optional;
import java.util.UUID;

// todo findByTitle -> findByWikiPageTitle
public interface WikiPageRepository {
    Optional<WikiPage> findById(UUID id);

    WikiPage save(WikiPage entity);

    Optional<WikiPage> findByTitleAndNamespace(String title, Namespace namespace);

    Optional<WikiPage> findByTitleWithRevisionAndRawContent(String title, Namespace namespace);
}
