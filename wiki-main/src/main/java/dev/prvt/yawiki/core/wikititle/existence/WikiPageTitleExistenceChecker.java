package dev.prvt.yawiki.core.wikititle.existence;

import dev.prvt.yawiki.common.model.WikiPageTitle;

import java.util.Collection;

/**
 * <p>위키 페이지의 제목이 존재하는지 여부를 체크하는 인터페이스.</p>
 * <p>유즈케이스: 존재하지 않는 제목을 가진 내부 링크를 빨간색으로 처리</p>
 * <p>대부분의 내부 링크는 존재하는 제목을 참조함. 유효하지 않은 내부 링크의 숫자보다 유효한 내부 링크의 숫자가 훨씬 많기 때문에 존재하지 않는 링크를 반환함.</p>
 */
public interface WikiPageTitleExistenceChecker {
    /**
     * Collection 을 받아 Collection을 반환하는 메서드
     * @param toFilter 필터링할 위키 페이지 제목
     * @return 존재하지 않는 위키 페이지 제목
     */
    Collection<WikiPageTitle> filterExistentTitles(Collection<WikiPageTitle> toFilter);

}
