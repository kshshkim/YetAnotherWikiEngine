package dev.prvt.yawiki.core.permission.domain;

import java.util.UUID;

public interface PermissionComparator {
    /**
     * 행위자의 authority 와 자원의 permission 을 비교, 행위자의 authority 가 충분하지 않으면 예외를 반환함.
     * @param actorId 행위자 ID
     * @param resourceId 자원 ID
     * @param actionType 행위 유형
     */
    void validatePermission(UUID actorId, UUID resourceId, ActionType actionType);
}
