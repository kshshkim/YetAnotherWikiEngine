package dev.prvt.yawiki.core.wikipage.domain.model;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class WikiPageFactory {
    /**
     * @param title 네임스페이스 구분자가 포함되지 않은 문서 제목. (ex. '틀: 아무개' -> '아무개')
     * @param namespace 네임스페이스 enum
     * @return isActive가 false이고 currentRevision이 null인 새 문서.
     */
    public WikiPage create(String title, Namespace namespace) {
        return create(title, namespace, null);
    }

    /**
     * @param title 네임스페이스 구분자가 포함되지 않은 문서 제목. (ex. '틀: 아무개' -> '아무개')
     * @param namespace 네임스페이스 enum
     * @param contributorId 기여자 ID
     * @return isActive가 false이고 currentRevision이 null인 새 문서.
     */
    public WikiPage create(String title, Namespace namespace, UUID contributorId) {
        return WikiPage.builder()
                .lastModifiedBy(contributorId)
                .lastModifiedAt(LocalDateTime.now())
                .title(title)
                .namespace(namespace)
                .build();
    }

}
