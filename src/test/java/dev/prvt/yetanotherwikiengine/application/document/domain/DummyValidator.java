package dev.prvt.yetanotherwikiengine.application.document.domain;

import dev.prvt.yetanotherwikiengine.application.document.domain.dependency.DocumentEditValidator;
import dev.prvt.yetanotherwikiengine.application.document.domain.exception.EditValidationException;

public class DummyValidator implements DocumentEditValidator {
    @Override
    public void validate(Document document, Revision revision, String editToken) throws EditValidationException {

    }
}

