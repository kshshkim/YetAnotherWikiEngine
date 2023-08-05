package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;

public interface VersionCollisionValidator {
    void validate(WikiPage wikiPage, String editToken) throws VersionCollisionException;
}
