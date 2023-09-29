package dev.prvt.yawiki.core.permission;

import dev.prvt.yawiki.config.permission.DefaultPermissionConfigInitializer;
import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.permission.domain.Permission;
import dev.prvt.yawiki.core.permission.domain.PermissionGroup;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@Transactional
@RequiredArgsConstructor
public class DefaultPermissionConfigInitializerImpl implements DefaultPermissionConfigInitializer {
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (defaultPermissionProperties.isDoInitialize()) {
            initializePermissionConfig();
        }
    }

    private final EntityManager em;
    private final PermissionRepository permissionRepository;
    private final DefaultPermissionProperties defaultPermissionProperties;

    public void initializePermissionConfig() {
        Permission defaultPermission = permissionRepository.getOrCreateByAllAttributes(
                        defaultPermissionProperties.getCreate(),
                        defaultPermissionProperties.getRead(),
                        defaultPermissionProperties.getUpdate(),
                        defaultPermissionProperties.getDelete(),
                        defaultPermissionProperties.getManage());
        PermissionGroup defaultPermissionGroup = new PermissionGroup(defaultPermissionProperties.getDefaultPermissionGroupId(), "default", defaultPermission);
        em.persist(defaultPermissionGroup);
    }
}


