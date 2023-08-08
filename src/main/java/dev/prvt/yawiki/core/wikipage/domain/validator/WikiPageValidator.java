package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Update 작업 이전에 수행돼야할 검증 작업. ReadOnly 트랜잭션으로 실행되어도 무방함.
 */
@Component
@RequiredArgsConstructor
public class WikiPageValidator {
    private final UpdatePermissionValidator permissionValidator;
    private final VersionCollisionValidator versionCollisionValidator;

    public void validateUpdateCommit(UUID actorId, String versionToken, WikiPage wikiPage) {
        validateVersionCollision(versionToken, wikiPage);
        validateUpdatePermission(actorId, wikiPage);
    }

    public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {
        permissionValidator.validateUpdateProclaim(actorId, wikiPage.getId());
    }

    private void validateVersionCollision(String versionToken, WikiPage wikiPage) {
        versionCollisionValidator.validate(wikiPage, versionToken);
    }

    private void validateUpdatePermission(UUID actorId, WikiPage wikiPage) {
        permissionValidator.validateUpdate(actorId, wikiPage.getId());
    }
}
