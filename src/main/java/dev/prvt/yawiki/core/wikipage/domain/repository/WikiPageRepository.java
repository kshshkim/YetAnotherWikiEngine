package dev.prvt.yawiki.core.wikipage.domain.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

import java.util.Optional;
import java.util.UUID;

public interface WikiPageRepository {
    WikiPage findOrCreate(String title);

    Optional<WikiPage> findById(UUID id);

    WikiPage save(WikiPage entity);

    Optional<WikiPage> findByTitle(String title);

    Optional<WikiPage> findByTitleWithRevisionAndRawContent(String title);
}
