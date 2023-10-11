package dev.prvt.yawiki.core.wikipage.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface WikiPageReferenceRepository {
    Set<String> findExistingWikiPageTitlesByRefererId(UUID refererId);
    Page<String> findBackReferencesByWikiPageTitle(String wikiPageTitle, Pageable pageable);
}
