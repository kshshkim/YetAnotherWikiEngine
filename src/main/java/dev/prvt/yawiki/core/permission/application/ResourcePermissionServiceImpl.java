package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.*;
import dev.prvt.yawiki.core.permission.domain.repository.NamespacePermissionRepository;
import dev.prvt.yawiki.core.permission.domain.repository.PagePermissionRepository;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ResourcePermissionServiceImpl implements ResourcePermissionService {
    private final PermissionRepository permissionRepository;
    private final PagePermissionRepository pagePermissionRepository;
    private final NamespacePermissionRepository namespacePermissionRepository;

    private final PermissionMapper permissionMapper;
    private final PagePermissionUpdateValidator pagePermissionUpdateValidator;

    @Override
    public Integer createPermission(PermissionData permissionData) {
         return permissionRepository.save(permissionMapper.map(permissionData)).getId();
    }

    @Override
    public void createPagePermission(UUID pageId, Integer namespaceId) {
        NamespacePermission namespacePermission = namespacePermissionRepository.findById(namespaceId)
                .orElseThrow();

        PagePermission newPagePermission = PagePermission.builder()
                .id(pageId)
                .namespacePermission(namespacePermission)
                .build();

        pagePermissionRepository.save(newPagePermission);
    }

    @Override
    public void updatePagePermission(UUID pageId, Integer permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow();
        PagePermission pagePermission = pagePermissionRepository.findById(pageId)
                .orElseThrow();

        pagePermission.updatePermission(
                permission,
                pagePermissionUpdateValidator
        );
    }

    @Override
    public void updateNamespacePermission(Integer namespaceId, PermissionData permissionData) {

    }

    @Override
    public void updateNamespacePermission(Integer namespaceId, Integer permissionId) {

    }
}
