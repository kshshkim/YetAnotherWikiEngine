package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import dev.prvt.yawiki.core.permission.domain.repository.ResourcePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionComparator {
    private final AuthorityProfileRepository authorityProfileRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;

    /**
     * 1. WikiPage 권한 체크
     * 2. 권한이 0(모두 허용)이면 행위자 ID는 체크하지 않음.
     * 3. 권한이 0이 아니면 행위자 권한 비교 들어감.
     */
    public void validatePermission(UUID actorId, UUID resourceId, ActionType actionType) {
        ResourcePermission resourcePermission = resourcePermissionRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("cannot find resource permission. resource id: " + resourceId));// todo 적절한 예외 클래스 생성

        int requiredLevel = resourcePermission.getRequiredAuthorityLevel(actionType);

        if (requiredLevel != 0) {
            AuthorityProfile authorityProfile = authorityProfileRepository.findById(actorId)
                    .orElseThrow(() -> new RuntimeException("cannot find AuthorityProfile. profile id: " + actorId));
            authorityProfile.validateAuthority(resourcePermission.getOwnerGroup().getId(), requiredLevel);
        }
    }
}
