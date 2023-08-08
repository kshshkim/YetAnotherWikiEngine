package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.validator.UpdatePermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdatePermissionValidatorImpl implements UpdatePermissionValidator {

    @Override
    public void validateUpdate(UUID wikiPageId, UUID actorId) {  // todo implement
    }

    @Override
    public void validateUpdateProclaim(UUID actorId, UUID wikiPageId) {  // todo implement

    }
}
