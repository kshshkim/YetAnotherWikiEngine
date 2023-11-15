package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.ActionType;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageCommandPermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WikiPageCommandPermissionValidatorImpl implements WikiPageCommandPermissionValidator {
    private final PermissionEvaluator permissionEvaluator;
    @Override
    public void validateEditCommit(UUID actorId, WikiPage wikiPage) {
        permissionEvaluator.validatePermission(ActionType.EDIT_COMMIT, actorId, wikiPage.getId());
    }

    @Override
    public void validateRename(UUID actorId, WikiPage wikiPage) {
        permissionEvaluator.validatePermission(ActionType.RENAME, actorId, wikiPage.getId());
    }

    @Override
    public void validateCreate(UUID actorId, WikiPage wikiPage) {
        permissionEvaluator.validatePermission(ActionType.CREATE, actorId, wikiPage.getId());
    }

    @Override
    public void validateDelete(UUID actorId, WikiPage wikiPage) {
        permissionEvaluator.validatePermission(ActionType.DELETE, actorId, wikiPage.getId());
    }

    @Override
    public void validateEditRequest(UUID actorId, WikiPage wikiPage) {
        permissionEvaluator.validatePermission(ActionType.EDIT_REQUEST, actorId, wikiPage.getId());
    }
}
