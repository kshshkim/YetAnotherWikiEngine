package dev.prvt.yawiki.core.permission.application;

import dev.prvt.yawiki.core.permission.domain.*;
import dev.prvt.yawiki.core.permission.domain.repository.NamespacePermissionRepository;
import dev.prvt.yawiki.core.permission.domain.repository.PagePermissionRepository;
import dev.prvt.yawiki.core.permission.domain.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResourcePermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private PagePermissionRepository pagePermissionRepository;
    @Mock
    private NamespacePermissionRepository namespacePermissionRepository;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private PagePermissionUpdateValidator pagePermissionUpdateValidator;

    @Mock
    private Permission mockPermission;

    @Mock
    private PagePermission mockPagePermission;

    @Mock
    private NamespacePermission mockNamespacePermission;

    @InjectMocks
    private ResourcePermissionServiceImpl resourcePermissionService;


    @Captor
    ArgumentCaptor<PagePermission> pagePermissionCaptor;

    @Test
    void createPermission() {
        PermissionData givenPermissionData = PermissionData.builder().build();
        given(permissionMapper.map(givenPermissionData))
                .willReturn(mockPermission);
        given(permissionRepository.save(any())).willReturn(mockPermission);
        // when
        resourcePermissionService.createPermission(givenPermissionData);

        // then
        verify(permissionRepository).save(mockPermission);
    }

    @Test
    void createPagePermission() {
        Integer givenNamespaceId = 1;
        UUID givenPageId = UUID.randomUUID();
        given(namespacePermissionRepository.findById(1))
                .willReturn(Optional.of(mockNamespacePermission));

        // when
        resourcePermissionService.createPagePermission(givenPageId, givenNamespaceId);

        verify(pagePermissionRepository).save(pagePermissionCaptor.capture());

        assertThat(pagePermissionCaptor.getValue().getNamespacePermission())
                .isSameAs(mockNamespacePermission);
    }

    @Test
    void updatePagePermission_with_predefined_id() {
        Integer givenPermissionId = 10;
        UUID givenPageId = UUID.randomUUID();
        given(permissionRepository.findById(givenPermissionId))
                .willReturn(Optional.of(mockPermission));
        given(pagePermissionRepository.findById(givenPageId))
                .willReturn(Optional.of(mockPagePermission));

        // when
        resourcePermissionService.updatePagePermission(givenPageId, givenPermissionId);

        // then
        verify(permissionRepository).findById(givenPermissionId);
        verify(pagePermissionRepository).findById(givenPageId);
        verify(mockPagePermission).updatePermission(mockPermission, pagePermissionUpdateValidator);
    }
}