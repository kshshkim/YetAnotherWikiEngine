package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.validator.VersionCollisionValidator;
import org.springframework.stereotype.Component;

@Component
public class VersionCollisionValidatorImpl implements VersionCollisionValidator {
    @Override
    public void validate(WikiPage wikiPage, String editToken) {
        if (!wikiPage.getVersionToken().equals(editToken)) {
            throw new VersionCollisionException();
        }
    }
}
