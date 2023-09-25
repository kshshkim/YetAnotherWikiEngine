package dev.prvt.yawiki.core.permission.domain;

import dev.prvt.yawiki.core.permission.PermissionFixture;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileMemoryRepository;
import dev.prvt.yawiki.core.permission.domain.repository.AuthorityProfileRepository;
import dev.prvt.yawiki.core.permission.domain.repository.ResourcePermissionMemoryRepository;
import dev.prvt.yawiki.core.permission.domain.repository.ResourcePermissionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PermissionComparatorTest {
    AuthorityProfileRepository authorityProfileRepository;
    ResourcePermissionRepository resourcePermissionRepository;
    PermissionComparator permissionComparator;


    AuthorityProfile givenAuthorityProfile;
    PermissionGroup givenPermissionGroup;
    ResourcePermission givenResourcePermission;
    Permission givenPermission;

    @BeforeEach
    void init() {
        authorityProfileRepository = new AuthorityProfileMemoryRepository();
        resourcePermissionRepository = new ResourcePermissionMemoryRepository();
        permissionComparator = new PermissionComparator(authorityProfileRepository, resourcePermissionRepository);


        givenPermission = PermissionFixture.aPermission()
                .read(0)
                .update(1)
                .manage(4)
                .build();
        givenPermissionGroup = new PermissionGroup(UUID.randomUUID(), UUID.randomUUID().toString(), givenPermission);
        givenResourcePermission = new ResourcePermission(UUID.randomUUID(), givenPermissionGroup, null);
        givenAuthorityProfile = AuthorityProfile.createWithGroup(UUID.randomUUID(), givenPermissionGroup, 1);

        authorityProfileRepository.save(givenAuthorityProfile);
        resourcePermissionRepository.save(givenResourcePermission);
    }

    @Test
    void validatePermission_should_success_when_required_level_is_0() {
        assertThatCode(() -> permissionComparator.validatePermission(givenAuthorityProfile.getId(), givenResourcePermission.getId(), ActionType.READ))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePermission_should_success_when_required_level_is_not_0_and_has_enough_authority() {
        assertThatCode(() -> permissionComparator.validatePermission(givenAuthorityProfile.getId(), givenResourcePermission.getId(), ActionType.UPDATE))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePermission_should_throw_exception_with_not_enough_authority() {
        assertThatThrownBy(() -> permissionComparator.validatePermission(givenAuthorityProfile.getId(), givenResourcePermission.getId(), ActionType.MANAGE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not enough");
    }
}