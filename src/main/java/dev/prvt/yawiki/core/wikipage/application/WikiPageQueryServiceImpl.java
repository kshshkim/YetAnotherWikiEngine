package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WikiPageQueryServiceImpl implements WikiPageQueryService {

    private final WikiPageRepository wikiPageRepository;
    private final WikiReferenceRepository wikiReferenceRepository;

    @Override
    public WikiPageDataForRead getWikiPage(String title) {
        WikiPage found = wikiPageRepository.findByTitleWithRevisionAndRawContent(title)
                .orElseThrow(NoSuchWikiPageException::new);
        Set<String> validWikiReferences = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(found.getId());
        return new WikiPageDataForRead(title, found.getContent(), validWikiReferences);
    }

    @Override
    public Page<String> getBackReferences(String title, Pageable pageable) {
        return wikiReferenceRepository.findBackReferencesByWikiPageTitle(title, pageable);
    }

    @Override
    public Page<RevisionData> getRevisionHistory(String title, Pageable pageable) {
        return null;  // todo implement
    }
}
