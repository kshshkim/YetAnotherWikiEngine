package dev.prvt.yawiki.core.permission.domain.impl;

import dev.prvt.yawiki.core.permission.domain.model.NamespacePermission;
import dev.prvt.yawiki.core.permission.domain.model.PagePermission;
import dev.prvt.yawiki.core.permission.domain.model.Permission;
import dev.prvt.yawiki.fixture.PermissionFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PagePermissionUpdateValidatorImplTest {

    @Mock
    NamespacePermission mockNamespacePermission;

    PagePermissionUpdateValidatorImpl pagePermissionUpdateValidator = new PagePermissionUpdateValidatorImpl();

    /**
     * mockNamespacePermission을 가진 pagePermission을 넘겼을 때, mockNamespacePermission.validatePermissionOverride() 를 정상적으로 호출하는지 확인함.
     */
    @Test
    void validate() {
        PagePermission givenPagePermission = PagePermission.builder()
                .namespacePermission(mockNamespacePermission)
                .build();
        Permission givenPermission = PermissionFixture.aPermission()
                .build();
        // when
        pagePermissionUpdateValidator.validate(givenPagePermission, givenPermission);

        // then
        verify(mockNamespacePermission)
                .validatePermissionOverride(givenPermission);
    }
}