package dev.prvt.yawiki.application.domain.wikipage;

import dev.prvt.yawiki.application.domain.wikipage.exception.EditValidationException;

public class DummyValidator implements DocumentEditValidator {
    @Override
    public void validate(Document document, Revision revision, String editToken) throws EditValidationException {

    }
}

