package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.PermissionLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

@ExtendWith(MockitoExtension.class)
class SecurityContextPermissionLevelFinderTest {

    SecurityContextPermissionLevelFinder securityContextPermissionLevelFinder = new SecurityContextPermissionLevelFinder();

    @Mock
    SecurityContext mockSecurityContext;

    @Mock
    Authentication mockAuthentication;

    @Mock
    GrantedAuthority mockGrantedAuthority;

    @ParameterizedTest
    @ValueSource(strings = {"MEMBER", "ASSISTANT_MANAGER", "MANAGER", "ADMIN"})
    void findPermissionLevelByActorId(String authority) {
        // given
        SecurityContextHolder.setContext(mockSecurityContext);
        given(mockSecurityContext.getAuthentication())
                .willReturn(mockAuthentication);
        willReturn(List.of(mockGrantedAuthority))
                .given(mockAuthentication)
                .getAuthorities();
        given(mockGrantedAuthority.getAuthority())
                .willReturn(authority);

        // when
        PermissionLevel found = securityContextPermissionLevelFinder.findPermissionLevelByActorId(UUID.randomUUID());

        // then
        assertThat(found)
                .isEqualTo(PermissionLevel.valueOf(authority));
    }

    @Mock
    Principal mockPrincipal;
    @Test
    @DisplayName("AnonymousAuthenticationToken 인 경우 EVERYONE 수준 권한")
    void findPermissionLevelByActorId_AnonymousAuthenticationToken() {
        // given
        SecurityContextHolder.setContext(mockSecurityContext);

        given(mockSecurityContext.getAuthentication())
                .willReturn(new AnonymousAuthenticationToken("key", mockPrincipal, List.of(mockGrantedAuthority)));

        // when
        PermissionLevel found = securityContextPermissionLevelFinder.findPermissionLevelByActorId(UUID.randomUUID());

        // then
        assertThat(found).isEqualTo(PermissionLevel.EVERYONE);
    }

    @Test
    @DisplayName("Authentication이 null인 경우 EVERYONE 수준 권한")
    void findPermissionLevelByActorId_null() {
        // given
        SecurityContextHolder.setContext(mockSecurityContext);

        // when
        PermissionLevel found = securityContextPermissionLevelFinder.findPermissionLevelByActorId(UUID.randomUUID());

        // then
        assertThat(found).isEqualTo(PermissionLevel.EVERYONE);
    }


}