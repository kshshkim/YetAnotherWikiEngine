package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPagePermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WikiPagePermissionValidatorImpl implements WikiPagePermissionValidator {


    @Override
    public void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException {

    }

    @Override
    public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {

    }
}
