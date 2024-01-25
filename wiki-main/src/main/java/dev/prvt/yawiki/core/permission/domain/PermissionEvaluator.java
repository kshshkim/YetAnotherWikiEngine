package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.exception.PermissionEvaluationException;
import dev.prvt.yawiki.core.permission.domain.model.ActionType;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import dev.prvt.yawiki.core.permission.domain.model.YawikiPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionEvaluator {
    private final AuthorityLevelFinder authorityLevelFinder;
    private final ResourceAclFinder resourceAclFinder;

    /**
     * @param actionType 행위 유형
     * @param actorId 행위자의 ID
     * @param wikiPageId 페이지 ID
     * @return 행위자가 페이지에 대해 해당 행위를 수행할수 있는지 여부
     *
     * 모두에게 허용된 경우 행위자의 권한 정보에 접근하지 않음. 추후 차단 기능을 도입할 때, 구현 방식에 따라 변경을 고려햘수 있음.
     */
    public boolean hasEnoughPermission(ActionType actionType, UUID actorId, UUID wikiPageId) {
        YawikiPermission pageAcl = getRequiredPermission(wikiPageId);

        return pageAcl.isAllowedToEveryone(actionType)
                || pageAcl.canDo(actionType, getActorPermissionLevel(actorId));
    }

    private YawikiPermission getRequiredPermission(UUID wikiPageId) {
        return resourceAclFinder.findWikiPageAclByWikiPageId(wikiPageId)
                .orElseThrow(() -> new IllegalStateException("PagePermission 엔티티가 존재하지 않습니다. WikiPage ID: " + wikiPageId));
    }

    /**
     * 권한이 부족한 경우 예외를 반환함.
     * @param actionType 행위 유형
     * @param actorId 행위자 ID
     * @param wikiPageId 페이지 ID
     */
    public void validatePermission(ActionType actionType, UUID actorId, UUID wikiPageId) {
        if (!hasEnoughPermission(actionType, actorId, wikiPageId)) {
            throw new PermissionEvaluationException();
        }
    }

    private PermissionLevel getActorPermissionLevel(UUID actorId) {
        return authorityLevelFinder.findPermissionLevelByActorId(actorId);
    }
}
