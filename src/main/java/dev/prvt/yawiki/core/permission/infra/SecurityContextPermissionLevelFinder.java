package dev.prvt.yawiki.core.permission.infra;

import dev.prvt.yawiki.core.permission.domain.AuthorityLevelFinder;
import dev.prvt.yawiki.core.permission.domain.model.PermissionLevel;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * <p>
 * SecurityContext를 통해 Authentication에 접근하여 PermissionLevel 정보를 가져옴.
 * Authentication이 null이거나, 익명 사용자인 경우에는 EVERYONE을 반환함.
 * </p>
 */
public class SecurityContextPermissionLevelFinder implements AuthorityLevelFinder {
    @Override
    public PermissionLevel findPermissionLevelByActorId(UUID actorId) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return PermissionLevel.EVERYONE;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(PermissionLevel::valueOf)
                .findAny()
                .orElse(PermissionLevel.EVERYONE);
    }
}
