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

    private Permission createNewPermission() {
        Permission built = Permission.builder()
                .create(defaultPermissionProperties.getCreate())
                .read(defaultPermissionProperties.getRead())
                .update(defaultPermissionProperties.getUpdate())
                .delete(defaultPermissionProperties.getDelete())
                .manage(defaultPermissionProperties.getManage())
                .build();
        em.persist(built);
        return built;
    }

    public void initializePermissionConfig() {
        Permission defaultPermission = permissionRepository.findByAllAttributes(
                        defaultPermissionProperties.getCreate(),
                        defaultPermissionProperties.getRead(),
                        defaultPermissionProperties.getUpdate(),
                        defaultPermissionProperties.getDelete(),
                        defaultPermissionProperties.getManage())
                .orElseGet(this::createNewPermission);
        PermissionGroup defaultPermissionGroup = new PermissionGroup(defaultPermissionProperties.getDefaultPermissionGroupId(), "default", defaultPermission);
        em.persist(defaultPermissionGroup);
    }
}


