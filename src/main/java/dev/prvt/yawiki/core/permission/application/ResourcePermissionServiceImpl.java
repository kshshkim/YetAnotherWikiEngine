package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.permission.domain.*;
import dev.prvt.yawiki.core.permission.domain.evaluator.PermissionEvaluator;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import dev.prvt.yawiki.core.permission.domain.repository.ResourcePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ResourcePermissionServiceImpl implements ResourcePermissionService {
    private final PermissionEvaluator permissionEvaluator;
    private final DefaultPermissionProperties defaultPermissionProperties;
    private final ResourcePermissionRepository resourcePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void updateResourcePermission(UUID resourceId) {
        updateResourcePermission(resourceId, defaultPermissionProperties.getDefaultPermissionGroupId());
    }

    @Override
    public void updateResourcePermission(UUID resourceId, UUID permissionGroupId) {
        updateResourcePermission(resourceId, permissionGroupId, null);
    }

    @Override
    public void updateResourcePermission(UUID resourceId, UUID permissionGroupId, PermissionData permissionData) {
        Permission specificPermission = getPermission(permissionData);
        ResourcePermission resourcePermission = getResourcePermission(resourceId);
        resourcePermission.updatePermission(specificPermission);
        resourcePermission.updateOwnerGroup(new PermissionGroup(permissionGroupId));
    }

    private Permission getPermission(PermissionData permissionData) {
        return permissionData == null ? null :
                permissionRepository.getOrCreateByAllAttributes(permissionData);
    }

    // jpa merge 사용하지 않기 위해 분리
    private ResourcePermission getResourcePermission(UUID resourceId) {
        Optional<ResourcePermission> resourcePermission = resourcePermissionRepository.findById(resourceId);
        return resourcePermission.orElseGet(
                () -> resourcePermissionRepository.save(
                        ResourcePermission.builder()
                                .id(resourceId)
                                .ownerGroup(new PermissionGroup(defaultPermissionProperties.getDefaultPermissionGroupId()))
                                .build()));
    }
}
