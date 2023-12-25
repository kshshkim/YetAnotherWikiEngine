package dev.prvt.yawiki.core.wikipage.domain.repository;


import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface WikiPageQueryRepository {
    Page<Revision> findRevisionsByWikiPageId(UUID wikiPageId, Pageable pageable);
    Page<Revision> findRevisionsByWikiPageTitle(WikiPageTitle wikiPageTitle, Pageable pageable);
    Optional<Revision> findRevisionByWikiPageTitle(WikiPageTitle wikiPageTitle, int version);
}
