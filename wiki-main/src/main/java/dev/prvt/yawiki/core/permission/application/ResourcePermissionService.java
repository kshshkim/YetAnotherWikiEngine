package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.model.PermissionData;

import java.util.UUID;

/**
 * NamespacePermission & PagePermission 관련 서비스
 */
public interface ResourcePermissionService {
    /**
     * PermissionData를 받아 Permission을 생성. 이후 생성된 Permission의 ID 반환
     * @param permissionData dto
     * @return 생성, 저장된 Permission의 ID
     */
    Integer createPermission(PermissionData permissionData);
    /**
     * create resource permission with predefined permissionId
     */
    void createPagePermission(UUID pageId, Integer namespaceId);

    /**
     * update page permission to predefined permission
     */
    void updatePagePermission(UUID pageId, Integer permissionId);

    /**
     * update namespace permission with specified permission values.
     */
    void updateNamespacePermission(Integer namespaceId, PermissionData permissionData);

    /**
     * update namespace permission to predefined permission
     */
    void updateNamespacePermission(Integer namespaceId, Integer permissionId);
}
