package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;

import java.util.UUID;

/**
 * 사용자의 권한 수준을 반환하는 인터페이스.
 * 구현 방법에 따라 네트워크 비용을 최소화할 수 있음.
 */
public interface AuthorityLevelFinder {
    PermissionLevel findPermissionLevelByActorId(UUID actorId);
}
