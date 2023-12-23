package dev.prvt.yawiki.core.wikipage.domain.validator;

import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPageTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WikiPageValidator {
    private final VersionCollisionValidator versionCollisionValidator;
    private final WikiPageCommandPermissionValidator wikiPageCommandPermissionValidator;
    private final WikiPageDuplicateTitleValidator duplicateTitleValidator;

    public void validateDelete(UUID contributorId, String versionToken, WikiPage wikiPage) {
        validateVersionCollision(versionToken, wikiPage);
        wikiPageCommandPermissionValidator.validateDelete(contributorId, wikiPage);
    }

    public void validateProclaim(UUID contributorId, WikiPage wikiPage) {
        wikiPageCommandPermissionValidator.validateEditRequest(contributorId, wikiPage);
    }

    public void validateUpdate(UUID contributorId, String versionToken, WikiPage wikiPage) {
        validateVersionCollision(versionToken, wikiPage);
        wikiPageCommandPermissionValidator.validateEditCommit(contributorId, wikiPage);
    }

    /**
     * 제목 변경 검증. 비용이 가장 적은 버전 충돌 검증 -> 중복 제목 검증 -> 권한 검증 순으로 진행함.
     */
    public void validateRename(UUID contributorId, String versionToken, WikiPage wikiPage, String newTitle) {
        validateVersionCollision(versionToken, wikiPage);
        duplicateTitleValidator.validate(new WikiPageTitle(newTitle, wikiPage.getNamespace()));
        wikiPageCommandPermissionValidator.validateRename(contributorId, wikiPage);
    }

    public void validateCreate(UUID contributorId, WikiPageTitle wikiPageTitle) {
        duplicateTitleValidator.validate(wikiPageTitle);
    }

    private void validateVersionCollision(String versionToken, WikiPage wikiPage) {
        versionCollisionValidator.validate(wikiPage, versionToken);
    }
}
