package dev.prvt.yawiki.core.wikipage.infra.validator;

import dev.prvt.yawiki.core.wikipage.domain.exception.WikiPageDuplicateTitleException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageDuplicateTitleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WikiPageDuplicateTitleValidatorImpl implements WikiPageDuplicateTitleValidator {
    private final WikiPageRepository wikiPageRepository;

    @Override
    public void validate(WikiPageTitle wikiPageTitle) {
        wikiPageRepository.findByTitleAndNamespace(wikiPageTitle.title(), wikiPageTitle.namespace())
                .ifPresent(wp -> {
                    throw new WikiPageDuplicateTitleException(wikiPageTitle);
                });
    }
}
