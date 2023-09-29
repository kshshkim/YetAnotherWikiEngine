package dev.prvt.yawiki.core.permission;

import dev.prvt.yawiki.config.permission.DefaultPermissionConfigInitializer;
import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.permission.domain.Permission;
import dev.prvt.yawiki.core.permission.domain.PermissionGroup;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class DefaultPermissionConfigInitializerImplTest {
    DefaultPermissionConfigInitializer defaultPermissionConfigInitializer;

    @Autowired
    EntityManager em;

    @Autowired
    PermissionRepository permissionRepository;

    @Test
    void when_doInitialize_is_false() {
        DefaultPermissionProperties defaultPermissionProperties = new DefaultPermissionProperties(false, 0, 0, 0, 0, 0, UUID.randomUUID());
        defaultPermissionConfigInitializer = new DefaultPermissionConfigInitializerImpl(em, permissionRepository, defaultPermissionProperties);

        defaultPermissionConfigInitializer.initialize();

        // then
        PermissionGroup permissionGroup = em.find(PermissionGroup.class, defaultPermissionProperties.getDefaultPermissionGroupId());
        assertThat(permissionGroup).isNull();
    }

    @Test
    void when_doInitialize_is_true() {
        DefaultPermissionProperties defaultPermissionProperties = new DefaultPermissionProperties(true, 1, 1, 1, 1, 1, UUID.randomUUID());
        defaultPermissionConfigInitializer = new DefaultPermissionConfigInitializerImpl(em, permissionRepository, defaultPermissionProperties);

        // when
        defaultPermissionConfigInitializer.initialize();

        // then
        PermissionGroup permissionGroup = em.find(PermissionGroup.class, defaultPermissionProperties.getDefaultPermissionGroupId());
        assertThat(permissionGroup)
                .describedAs("permission group should be created")
                .isNotNull();

        Permission defaultResourcePermission = permissionGroup.getDefaultResourcePermission();
        assertThat(defaultResourcePermission)
                .describedAs("default resource permission should be set")
                .isNotNull();
        assertThat(tuple(defaultResourcePermission.getCreate(), defaultResourcePermission.getRead(), defaultResourcePermission.getUpdate(), defaultResourcePermission.getDelete(), defaultResourcePermission.getManage()))
                .isEqualTo(tuple(defaultPermissionProperties.getCreate(), defaultPermissionProperties.getRead(), defaultPermissionProperties.getUpdate(), defaultPermissionProperties.getDelete(), defaultPermissionProperties.getManage()));

    }
}