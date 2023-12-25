package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
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
    WikiPageDataForRead getWikiPageDataForRead(WikiPageTitle wikiPageTitle);

    WikiPageDataForRead getWikiPageDataForRead(WikiPageTitle wikiPageTitle, int version);

    WikiPageDataForUpdate getWikiPageDataForUpdate(WikiPageTitle wikiPageTitle);

    WikiPageDataForUpdate getWikiPageDataForUpdate(WikiPageTitle wikiPageTitle, int version);

    /**
     * 페이징이 적용된 수정 내역 조회용 메소드.
     * @param wikiPageTitle 참조되고 있는 문서 제목&네임스페이스
     * @return RevisionData in Page
     */
    Page<RevisionData> getRevisionHistory(WikiPageTitle wikiPageTitle, Pageable pageable);
}
