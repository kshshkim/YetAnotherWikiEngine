package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.evaluator.PermissionEvaluator;
import dev.prvt.yawiki.core.wikipage.domain.exception.UpdatePermissionException;
import dev.prvt.yawiki.core.wikipage.domain.model.WikiPage;
import dev.prvt.yawiki.core.wikipage.domain.validator.WikiPageCommandPermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 회원 징계(정지, 차단) 여부는 이 클래스의 책임이 아님.
 *
 */
@Component
@RequiredArgsConstructor
public class WikiPageCommandPermissionValidatorImpl implements WikiPageCommandPermissionValidator {
    private final PermissionEvaluator permissionComparator;
    @Override
    public void validateUpdate(UUID actorId, WikiPage wikiPage) throws UpdatePermissionException {
        permissionComparator.validatePermission(actorId, wikiPage.getId(), ActionType.UPDATE);
    }

    @Override
    public void validateDelete(UUID actorId, WikiPage wikiPage) {
        permissionComparator.validatePermission(actorId, wikiPage.getId(), ActionType.DELETE);
    }

    @Override
    public void validateUpdateProclaim(UUID actorId, WikiPage wikiPage) {  // todo 편집 요청은 가능하지만 편집 커밋은 불가한 권한 구성
        permissionComparator.validatePermission(actorId, wikiPage.getId(), ActionType.UPDATE);
    }
}
