package dev.prvt.yawiki.application.domain.wikipage;

import dev.prvt.yawiki.application.domain.wikipage.exception.EditValidationException;

public class DummyValidator implements EditValidator {
    @Override
    public void validate(WikiPage wikiPage, Revision revision, String editToken) throws EditValidationException {

    }
}

