package dev.prvt.yawiki.core.wikipage.domain.repository;

import dev.prvt.yawiki.core.wikipage.domain.model.Namespace;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface WikiPageReferenceRepository {
    Set<WikiPageTitle> findExistingWikiPageTitlesByRefererId(UUID refererId);
    Page<WikiPageTitle> findBackReferencesByWikiPageTitle(String wikiPageTitle, Namespace namespace, Pageable pageable);
}
