package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.PermissionData;

import java.util.UUID;

public interface ResourcePermissionService {
    /**
     * create resource permission with default permission
     * @param resourceId
     */
    void updateResourcePermission(UUID resourceId);

    /**
     * create resource permission with predefined permissionId
     * @param resourceId
     * @param permissionGroupId
     */
    void updateResourcePermission(UUID resourceId, UUID permissionGroupId);

    /**
     * create resource permission with specified permission values.
     * @param resourceId
     * @param permissionData
     */
    void updateResourcePermission(UUID resourceId, UUID permissionGroupId, PermissionData permissionData);
}
