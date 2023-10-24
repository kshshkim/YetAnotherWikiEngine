package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WikiPageCommandPermissionValidatorImpl implements WikiPageCommandPermissionValidator {
    @Override
    public void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException {

    }

    @Override
    public void validateDelete(UUID actorId, WikiPage wikiPage) {

    }

    @Override
    public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {

    }
}
