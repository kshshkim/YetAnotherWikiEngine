package dev.prvt.yawiki.core.wikipage.application;

import dev.prvt.yawiki.core.wikipage.application.dto.WikiPageDataForUpdate;
import dev.prvt.yawiki.core.wikipage.domain.event.WikiPageEventPublisher;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageFactory;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class WikiPageCommandServiceImpl implements WikiPageCommandService {
    private final WikiPageRepository wikiPageRepository;
    private final WikiPageValidator wikiPageValidator;
    private final WikiPageEventPublisher wikiPageEventPublisher;
    private final WikiPageMapper wikiPageMapper;
    private final WikiPageFactory wikiPageFactory;

    @Override
    public void commitUpdate(
            UUID contributorId,
            WikiPageTitle title,
            String comment,
            String versionToken,
            String content,
            Set<WikiPageTitle> referencedTitles
    ) {
        WikiPage wikiPage = getWikiPage(title);

        wikiPageValidator.validateUpdate(contributorId, versionToken, wikiPage);
        wikiPage.update(contributorId, comment, content);
        wikiPageEventPublisher.updateCommitted(wikiPage, referencedTitles);
    }

    @Transactional(readOnly = true)
    @Override
    public WikiPageDataForUpdate proclaimUpdate(UUID contributorId, WikiPageTitle title) {
        WikiPage wikiPage = getWikiPage(title);
        wikiPageValidator.validateProclaim(contributorId, wikiPage);

        return wikiPageMapper.mapFrom(wikiPage);
    }

    @Override
    public void create(UUID contributorId, WikiPageTitle wikiPageTitle) {
        wikiPageValidator.validateCreate(contributorId, wikiPageTitle);
        WikiPage created = wikiPageRepository.save(wikiPageFactory.create(wikiPageTitle.title(), wikiPageTitle.namespace(), contributorId));
        wikiPageEventPublisher.created(created);
    }

    @Override
    public void delete(UUID contributorId, WikiPageTitle title, String comment, String versionToken) {
        WikiPage wikiPage = getWikiPage(title);

        wikiPageValidator.validateDelete(contributorId, versionToken, wikiPage);
        wikiPage.deactivate(contributorId, comment);
        wikiPageEventPublisher.deactivated(wikiPage);
    }

    private WikiPage getWikiPage(WikiPageTitle title) {
        return wikiPageRepository.findByTitleAndNamespace(title.title(), title.namespace())
                .orElseThrow(NoSuchWikiPageException::new);
    }
}
