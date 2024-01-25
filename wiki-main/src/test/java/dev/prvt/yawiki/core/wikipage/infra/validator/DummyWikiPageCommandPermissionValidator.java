package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageCommandPermissionValidator;

import java.util.UUID;

/**
 * 권한 판정이 필요 없는 통합 테스트를 위한 더미 구현체. 아무런 동작을 하지 않음.
 */
public class DummyWikiPageCommandPermissionValidator implements WikiPageCommandPermissionValidator {
    @Override
    public void validateEditCommit(UUID actorId, WikiPage wikiPage) {

    }

    @Override
    public void validateRename(UUID actorId, WikiPage wikiPage) {

    }

    @Override
    public void validateCreate(UUID actorId, WikiPage wikiPage) {

    }

    @Override
    public void validateDelete(UUID actorId, WikiPage wikiPage) {

    }

    @Override
    public void validateEditRequest(UUID actorId, WikiPage wikiPage) {

    }
}
