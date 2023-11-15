package dev.prvt.yawiki.core.permission.domain.model;

/**
 * ActionType-PermissionLevel 쌍을 규정한 ACL(Access Control List) 인터페이스
 * 모든 행위에 대한 권한 수준을 규정하고 있어야함.
 */
public interface YawikiPermission {
    /**
     * actionType 에 따른 요구 권한 수준을 반환함.
     * @param actionType 행위 유형
     * @return 절대 null 값을 반환하지 않음.
     */
    PermissionLevel getRequiredPermissionLevel(ActionType actionType);

    /**
     * getRequiredPermissionLevel 구현에 전적으로 의존적인 메서드이기 떄문에 default 로 선언함.
     * @param actionType 행위 유형
     * @param actorPermissionLevel 행위자의 권한 수준
     * @return actorPermissionLevel 이 actionType 에 해당하는 요구 권한 레벨을 충족하는지 여부
     */
    default boolean canDo(ActionType actionType, PermissionLevel actorPermissionLevel) {
        return actorPermissionLevel.isHigherThanOrEqualTo(getRequiredPermissionLevel(actionType));
    }

    /**
     * getRequiredPermissionLevel 구현에 전적으로 의존적인 메서드이기 떄문에 default 로 선언함.
     * @param actionType 행위 유형
     * @return 행위 유형에 대한 요구 권한이 EVERYONE 인지 여부
     */
    default boolean isAllowedToEveryone(ActionType actionType) {
        return getRequiredPermissionLevel(actionType) == PermissionLevel.EVERYONE;
    }
}
