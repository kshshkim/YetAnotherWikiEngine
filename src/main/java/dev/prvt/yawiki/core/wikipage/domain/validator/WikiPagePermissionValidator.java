package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

import java.util.UUID;

public interface WikiPagePermissionValidator {
    void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException;

    void validateUpdateProclaim(UUID actorId, WikiPage wikiPage);
}
