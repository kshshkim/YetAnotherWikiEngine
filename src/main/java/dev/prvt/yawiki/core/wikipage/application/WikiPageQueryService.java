package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WikiPageQueryService {
    /**
     * 조회용 WikiPage DTO.
     * @param wikiPageTitle 위키 페이지 제목과 네임스페이스
     * @return WikiPageDataForRead
     * @see dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead
     */
    WikiPageDataForRead getWikiPage(WikiPageTitle wikiPageTitle);

    WikiPageDataForRead getWikiPage(WikiPageTitle wikiPageTitle, int version);

    /**
     * title 을 참조하고 있는 문서들의 title 목록. title 기준으로 오름차순 정렬된 값을 반환함.
     * @param wikiPageTitle 참조되고 있는 문서 제목&네임스페이스
     * @return WikiPageTitle in Page
     */
    Page<WikiPageTitle> getBackReferences(WikiPageTitle wikiPageTitle, Pageable pageable);

    /**
     * 페이징이 적용된 수정 내역 조회용 메소드.
     * @param wikiPageTitle 참조되고 있는 문서 제목&네임스페이스
     * @return RevisionData in Page
     */
    Page<RevisionData> getRevisionHistory(WikiPageTitle wikiPageTitle, Pageable pageable);
}
