package dev.prvt.yawiki.core.wikireference.application;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikireference.domain.WikiReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
public class WikiReferenceQueryService {
    private final WikiReferenceRepository wikiReferenceRepository;

    /**
     * wikiPageTitle 을 참조하고 있는 문서들의 제목&네임스페이스 목록 조회. wikiPageTitle.title 기준으로 오름차순 정렬된 값을 반환함.
     * @param wikiPageTitle 참조되고 있는 문서 제목&네임스페이스
     * @param pageable pageable
     * @return Page 로 래핑된 WikiPageTitle 반환
     */
    public Page<WikiPageTitle> getBacklinks(WikiPageTitle wikiPageTitle, Pageable pageable) {
        return wikiReferenceRepository.findBacklinksByWikiPageTitle(wikiPageTitle.title(), wikiPageTitle.namespace(), pageable);
    }
}
