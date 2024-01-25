package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.common.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WikiPageQueryServiceImpl implements WikiPageQueryService {

    private final WikiPageRepository wikiPageRepository;
    private final WikiPageQueryRepository wikiPageQueryRepository;
    private final WikiPageMapper wikiPageMapper;

    @Override
    public WikiPageDataForRead getWikiPageDataForRead(WikiPageTitle wikiPageTitle) {
        return wikiPageMapper.mapForRead(getWikiPage(wikiPageTitle));
    }

    /**
     * 과거 버전에 대해서는 위키 레퍼런스 관련 정보를 제공하지 않음.
     */
    @Override
    public WikiPageDataForRead getWikiPageDataForRead(WikiPageTitle wikiPageTitle, int version) {
        return wikiPageMapper.mapForRead(getRevision(wikiPageTitle, version));
    }

    @Override
    public WikiPageDataForUpdate getWikiPageDataForUpdate(WikiPageTitle wikiPageTitle) {
        return wikiPageMapper.mapForUpdate(getWikiPage(wikiPageTitle));
    }

    @Override
    public WikiPageDataForUpdate getWikiPageDataForUpdate(WikiPageTitle wikiPageTitle, int version) {
        return wikiPageMapper.mapForUpdate(getRevision(wikiPageTitle, version));
    }

    @Override
    public Page<RevisionData> getRevisionHistory(WikiPageTitle wikiPageTitle, Pageable pageable) {
        return wikiPageQueryRepository.findRevisionsByWikiPageTitle(wikiPageTitle, pageable)
                .map(wikiPageMapper::mapFrom);
    }

    private WikiPage getWikiPage(WikiPageTitle wikiPageTitle) {
        return wikiPageRepository.findByTitleAndNamespace(wikiPageTitle.title(), wikiPageTitle.namespace())
                .orElseThrow(NoSuchWikiPageException::new);
    }

    private Revision getRevision(WikiPageTitle wikiPageTitle, int version) {
        return wikiPageQueryRepository.findRevisionByWikiPageTitle(wikiPageTitle, version)
                .orElseThrow(NoSuchWikiPageException::new);
    }

}
