package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

import java.util.Set;
import java.util.UUID;

public interface WikiPageCommandService {
    /**
     * <p>모든 update 요청은 우선 update 를 시작하겠다는 요청부터 들어와야함. 따라서 update 이전 요청이 들어온 시점에 db 에 엔티티가 생성되어있어야함.</p>
     * <p>레퍼런스 파싱 등, Update 그 자체와 관련이 없는 작업은 트랜잭션의 범주 바깥에서 실행할것.</p>
     * @param contributorId 편집자 ID
     * @param title 문서 제목
     * @param comment 편집 코멘트
     * @param versionToken 편집 충돌 방지용 토큰
     * @param content 본문
     */
    void commitUpdate(UUID contributorId, WikiPageTitle title, String comment, String versionToken, String content, Set<WikiPageTitle> referencedTitles);

    /**
     * <p>편집을 시작하기 위해서 사용됨. DTO 형태로 반환하는 것을 고려할것.</p>
     * <p>편집 권한이 있는지 체크해서 통과하면 문서를 VersionToken 과 함께 반환함.</p>
     * @param contributorId 편집 요청자 ID
     * @param title 편집할 문서 제목
     * @return WikiPageDataForUpdate
     * @see WikiPageDataForUpdate
     */
    WikiPageDataForUpdate proclaimUpdate(UUID contributorId, WikiPageTitle title);

    void create(UUID contributorId, WikiPageTitle title);

    void delete(UUID contributorId, WikiPageTitle title, String comment, String versionToken);
}
