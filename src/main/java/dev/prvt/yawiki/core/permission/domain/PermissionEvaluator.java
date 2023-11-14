package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.repository.PagePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionEvaluator {
    private final AuthorityLevelFinder authorityLevelFinder;
    private final PagePermissionRepository pagePermissionRepository;

    /**
     * @param actionType 행위 유형
     * @param actorId 행위자의 ID
     * @param wikiPageId 페이지 ID
     * @return 행위자가 페이지에 대해 해당 행위를 수행할수 있는지 여부
     *
     * 모두에게 허용된 경우 행위자의 권한 정보에 접근하지 않음. 추후 차단 기능을 도입할 때, 구현 방식에 따라 변경을 고려햘수 있음.
     */
    public boolean hasEnoughPermission(ActionType actionType, UUID actorId, UUID wikiPageId) {
        PermissionLevel required = getRequiredPermissionLevel(actionType, wikiPageId);
        return isAllowedToEveryone(required)
                || hasEnoughPermission(actorId, required);
    }

    private PermissionLevel getRequiredPermissionLevel(ActionType actionType, UUID wikiPageId) {
        PagePermission pagePermission = pagePermissionRepository.findById(wikiPageId)
                .orElseThrow(() -> new IllegalStateException("PagePermission 엔티티가 존재하지 않습니다. WikiPage ID: " + wikiPageId));
        return pagePermission.getRequiredPermissionLevel(actionType);
    }

    private boolean hasEnoughPermission(UUID actorId, PermissionLevel required) {
        return getActorPermissionLevel(actorId).isHigherThanOrEqualTo(required);
    }

    private boolean isAllowedToEveryone(PermissionLevel required) {
        return required.equals(PermissionLevel.EVERYONE);
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
