package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.common.model.WikiPageTitle;

public interface WikiPageDuplicateTitleValidator {
    void validate(WikiPageTitle wikiPageTitle);
}
