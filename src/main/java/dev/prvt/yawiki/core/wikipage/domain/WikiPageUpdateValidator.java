package dev.prvt.yawiki.core.wikipage.domain;

import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.repository.WikiPageRepository;
import dev.prvt.yawiki.core.wikipage.domain.validator.VersionCollisionValidator;
import dev.prvt.yawiki.core.wikipage.domain.validator.UpdatePermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Update 작업 이전에 수행돼야할 검증 작업. ReadOnly 트랜잭션으로 실행되어도 무방함.
 */
@Component
@RequiredArgsConstructor
public class WikiPageUpdateValidator {
    private final WikiPageRepository wikiPageRepository;
    private final UpdatePermissionValidator permissionValidator;
    private final VersionCollisionValidator versionCollisionValidator;

    public void validate(UUID actorId, String wikiPageTitle, String versionToken) {
        WikiPage wikiPage = wikiPageRepository.findByTitle(wikiPageTitle)
                .orElseThrow(NoSuchWikiPageException::new);

        validateVersionCollision(versionToken, wikiPage);
        validatePermission(actorId, wikiPage);
    }

    private void validateVersionCollision(String versionToken, WikiPage wikiPage) {
        versionCollisionValidator.validate(wikiPage, versionToken);
    }

    private void validatePermission(UUID actorId, WikiPage wikiPage) {
        permissionValidator.validateUpdate(wikiPage.getId(), actorId);
    }
}
