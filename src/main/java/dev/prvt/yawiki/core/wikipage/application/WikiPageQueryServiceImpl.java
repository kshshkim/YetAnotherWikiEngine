package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageReferenceRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WikiPageQueryServiceImpl implements WikiPageQueryService {

    private final WikiPageRepository wikiPageRepository;
    private final WikiPageQueryRepository wikiPageQueryRepository;
    private final WikiPageReferenceRepository wikiReferenceRepository;
    private final ContributorRepository contributorRepository;
    private final WikiPageMapper wikiPageMapper;

    @Override
    public WikiPageDataForRead getWikiPage(WikiPageTitle wikiPageTitle) {
        WikiPage found = wikiPageRepository.findByTitleWithRevisionAndRawContent(wikiPageTitle.title(), wikiPageTitle.namespace())
                .orElseThrow(NoSuchWikiPageException::new);
        Set<WikiPageTitle> validWikiReferences = wikiReferenceRepository.findExistingWikiPageTitlesByRefererId(found.getId());
        return new WikiPageDataForRead(wikiPageTitle.title(), found.getNamespace(), found.getContent(), validWikiReferences);
    }

    /**
     * 과거 버전에 대해서는 위키 레퍼런스 관련 정보를 제공하지 않음.
     */
    @Override
    public WikiPageDataForRead getWikiPage(WikiPageTitle wikiPageTitle, int version) {
        Revision found = wikiPageQueryRepository.findRevisionByWikiPageTitleWithRawContent(wikiPageTitle, version)
                .orElseThrow(NoSuchWikiPageException::new);
        return new WikiPageDataForRead(wikiPageTitle.title(), wikiPageTitle.namespace(), found.getContent(), null);
    }

    @Override
    public Page<WikiPageTitle> getBackReferences(WikiPageTitle wikiPageTitle, Pageable pageable) {
        return wikiReferenceRepository.findBackReferencesByWikiPageTitle(wikiPageTitle.title(), wikiPageTitle.namespace(), pageable);
    }

    // todo refactor
    /**
     * <p>한 번의 쿼리로 DTO 를 조회하는게 성능상 좋아보이지만, 큰 차이는 없을 것으로 생각됨. 개발 초기 단계이므로 좀 더 유연한 구조를 유지함.</p>
     */
    @Override
    public Page<RevisionData> getRevisionHistory(WikiPageTitle wikiPageTitle, Pageable pageable) {
        WikiPage wikiPage = wikiPageRepository.findByTitleAndNamespace(wikiPageTitle.title(), wikiPageTitle.namespace())
                .orElseThrow(NoSuchWikiPageException::new);

        Page<Revision> revisionEntities = wikiPageQueryRepository.findRevisionsByWikiPageId(wikiPage.getId(), pageable);

        List<UUID> contributorIds = revisionEntities.getContent().stream()
                .map(Revision::getContributorId).distinct()
                .toList();

        Map<UUID, Contributor> contributorMap = contributorRepository.findContributorsByIds(contributorIds)
                .collect(Collectors.toMap(
                        Contributor::getId,  // key
                        contributor -> contributor  // value
                ));

        return revisionEntities.map(revision -> wikiPageMapper.mapFrom(revision, contributorMap));
    }
}
