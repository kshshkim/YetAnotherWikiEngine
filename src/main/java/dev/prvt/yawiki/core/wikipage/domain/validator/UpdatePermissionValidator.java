package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;

import java.util.UUID;

public interface UpdatePermissionValidator {
    void validateUpdate(UUID actorId, UUID wikiPageId) throws UpdatePermissionException;

    void validateUpdateProclaim(UUID actorId, UUID wikiPageId);
}
