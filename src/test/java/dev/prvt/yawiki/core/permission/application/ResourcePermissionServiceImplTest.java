package dev.prvt.yawiki.core.permission.application;


import dev.prvt.yawiki.config.permission.DefaultPermissionProperties;
import dev.prvt.yawiki.core.permission.domain.Permission;
import dev.prvt.yawiki.core.permission.domain.PermissionData;
import dev.prvt.yawiki.core.permission.domain.PermissionGroup;
import dev.prvt.yawiki.core.permission.domain.ResourcePermission;
import dev.prvt.yawiki.core.permission.domain.evaluator.PermissionEvaluator;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import dev.prvt.yawiki.core.permission.domain.repository.ResourcePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

import static dev.prvt.yawiki.Fixture.randString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class ResourcePermissionServiceImplTest {
    @Autowired
    DefaultPermissionProperties defaultPermissionProperties;

    @Autowired
    ResourcePermissionRepository resourcePermissionRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    PermissionEvaluator permissionEvaluator;

    @Autowired
    EntityManager em;

    ResourcePermissionService resourcePermissionService;

    UUID givenId;

    @BeforeEach
    void init() {
        givenId = UUID.randomUUID();
        resourcePermissionService = new ResourcePermissionServiceImpl(permissionEvaluator, defaultPermissionProperties, resourcePermissionRepository, permissionRepository);
    }

    @Test
    void updatePermission_default() {
        // when
        resourcePermissionService.updateResourcePermission(givenId);
        em.flush();
        em.clear();

        // then
        ResourcePermission resourcePermission = resourcePermissionRepository.findById(givenId).orElseThrow();
        assertThat(resourcePermission.getSpecificPermission())
                .isNull();
        assertThat(resourcePermission.getOwnerGroup().getId())
                .isEqualTo(defaultPermissionProperties.getDefaultPermissionGroupId());
        assertThat(resourcePermission.getOwnerGroup().getDefaultResourcePermission())
                .isNotNull();
    }

    @Test
    void updatePermission_with_groupId() {
        // given
        UUID givenPermissionGroupId = UUID.randomUUID();
        Permission givenPermission = permissionRepository.getOrCreateByAllAttributes(1, 2, 3, 4, 4);
        em.persist(new PermissionGroup(givenPermissionGroupId, randString(), givenPermission));
        em.flush();
        em.clear();

        // when
        resourcePermissionService.updateResourcePermission(givenId, givenPermissionGroupId);
        em.flush();
        em.clear();

        // then
        ResourcePermission resourcePermission = resourcePermissionRepository.findById(givenId).orElseThrow();
        assertThat(resourcePermission.getSpecificPermission())
                .isNull();
        assertThat(resourcePermission.getOwnerGroup().getId())
                .isEqualTo(givenPermissionGroupId);
    }

    @Test
    void updatePermission_with_groupId_and_specific_permission() {
        // given
        UUID givenPermissionGroupId = UUID.randomUUID();
        Permission givenPermission = permissionRepository.getOrCreateByAllAttributes(1, 2, 3, 4, 4);
        em.persist(new PermissionGroup(givenPermissionGroupId, randString(), givenPermission));
        em.flush();
        em.clear();
        // when
        resourcePermissionService.updateResourcePermission(givenId, givenPermissionGroupId, PermissionData.from(givenPermission));
        em.flush();
        em.clear();

        // then
        ResourcePermission resourcePermission = resourcePermissionRepository.findById(givenId).orElseThrow();
        Permission specificPermission = resourcePermission.getSpecificPermission();
        assertThat(specificPermission)
                .isNotNull();
        assertThat(tuple(specificPermission.getCreate(), specificPermission.getRead(), specificPermission.getUpdate(), specificPermission.getDelete(), specificPermission.getManage()))
                .isEqualTo(tuple(1, 2, 3, 4, 4));
        assertThat(resourcePermission.getOwnerGroup().getId())
                .isEqualTo(givenPermissionGroupId);
    }
}