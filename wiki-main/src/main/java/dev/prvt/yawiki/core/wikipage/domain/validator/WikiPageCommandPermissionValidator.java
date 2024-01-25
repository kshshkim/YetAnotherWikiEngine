package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

import java.util.UUID;

public interface WikiPageCommandPermissionValidator {
    void validateEditCommit(UUID actorId, WikiPage wikiPage);

    void validateRename(UUID actorId, WikiPage wikiPage);

    void validateCreate(UUID actorId, WikiPage wikiPage);

    void validateDelete(UUID actorId, WikiPage wikiPage);

    /**
     * 편집 권한이 없더라도, 편집 권한이 있는 사람이 승인할 수 있도록 요청 가능하기 때문에 분리함.
     * @param actorId
     * @param wikiPage
     */
    void validateEditRequest(UUID actorId, WikiPage wikiPage);
}
