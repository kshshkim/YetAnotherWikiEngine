package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.RevisionData;
import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead;
import org.springframework.data.domain.Page;

public interface WikiPageQueryService {
    /**
     * 조회용 WikiPage DTO.
     * @param title 찾을 WikiPage 의 title
     * @return WikiPageDataForRead
     * @see dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForRead
     */
    WikiPageDataForRead getWikiPage(String title);

    /**
     * title 을 참조하고 있는 문서들의 title 목록. title 기준으로 오름차순 정렬된 값을 반환함.
     * @param title 참조되고 있는 문서 제목
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return WikiPage.title in Page
     */
    Page<String> getBackReferences(String title, int page, int size);

    /**
     * 페이징이 적용된 수정 내역 조회용 메소드.
     * @param title 조회할 문서 제목
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return RevisionData in Page
     */
    Page<RevisionData> getRevisionHistory(String title, int page, int size);
}
