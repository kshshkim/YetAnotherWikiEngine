package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;

import java.util.UUID;

public interface UpdatePermissionValidator {
    void validateUpdate(UUID wikiPageId, UUID actorId) throws UpdatePermissionException;
}
