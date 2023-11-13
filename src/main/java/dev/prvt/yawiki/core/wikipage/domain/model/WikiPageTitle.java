package dev.prvt.yawiki.core.wikipage.domain.model;

import com.querydsl.core.annotations.QueryProjection;

public record WikiPageTitle(
        String title,
        Namespace namespace
) {
    /**
     * 네임스페이스가 NORMAL일 때를 제외하고, 제목 앞에 네임스페이스 이름을 붙여서 반환함.
     * @return `네임스페이스: 제목` 형태의 문자열.
     */
    public String toUnparsedString() {  // todo 프로퍼티나 맵 등을 받아 반환되는 네임스페이스 표시 형태 반환
        return namespace == Namespace.NORMAL ? title : namespace + ": " + title;
    }
    @QueryProjection
    public WikiPageTitle {
    }
}