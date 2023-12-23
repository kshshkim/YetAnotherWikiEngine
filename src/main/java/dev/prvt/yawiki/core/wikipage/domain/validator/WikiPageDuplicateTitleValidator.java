package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;

public interface WikiPageDuplicateTitleValidator {
    void validate(WikiPageTitle wikiPageTitle);
}
