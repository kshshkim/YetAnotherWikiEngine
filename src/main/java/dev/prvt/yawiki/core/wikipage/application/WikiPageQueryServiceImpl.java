package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.contributor.domain.Contributor;
import dev.prvt.yawiki.core.contributor.domain.ContributorRepository;
import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.Revision;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageQueryRepository;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
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
    private final WikiReferenceRepository wikiReferenceRepository;
    private final ContributorRepository contributorRepository;
    private final WikiPageMapper wikiPageMapper;

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

    /**
     * <p>한 번의 쿼리로 DTO 를 조회하는게 성능상 좋아보이지만, 큰 차이는 없을 것으로 생각됨. 개발 초기 단계이므로 좀 더 유연한 구조를 유지함.</p>
     */
    @Override
    public Page<RevisionData> getRevisionHistory(String title, Pageable pageable) {
        Page<Revision> revisionEntities = wikiPageQueryRepository.findRevisionsByTitle(title, pageable);

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
